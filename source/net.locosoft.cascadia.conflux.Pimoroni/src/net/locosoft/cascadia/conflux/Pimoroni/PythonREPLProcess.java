/*********************************************************************
* Copyright (c) 2019 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.Pimoroni;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;

public class PythonREPLProcess {

	private String _command;
	private Process _process;
	private InputCascade _inputCascade;
	private OutputCascade _outputCascade;
	private OutputCascade _errorCascade;

	public PythonREPLProcess(String scriptName, PimoroniConflux conflux) {
		_command = "python -u " + conflux.getScriptPath(scriptName);
		_inputCascade = new InputCascade(scriptName, conflux);
		_outputCascade = new OutputCascade(scriptName, false, conflux);
		_errorCascade = new OutputCascade(scriptName, true, conflux);
	}

	public InputCascade getInputCascade() {
		return _inputCascade;
	}

	public OutputCascade getOutputCascade() {
		return _outputCascade;
	}

	public OutputCascade getErrorCascade() {
		return _errorCascade;
	}

	private synchronized Process getProcess() throws IOException {
		if (_process == null) {
			_process = Runtime.getRuntime().exec(_command);
		}
		return _process;
	}

	public class InputCascade extends Cascade {
		private static final char _CTRL_D = 4;
		private static final char _EOL = '\n';

		private BufferedWriter _writer;

		private int _waitCycle = 0;
		private int _waitCycleMax = 8;

		private LinkedList<StringDrop> _writtenDrops = new LinkedList<StringDrop>();

		public InputCascade(String scriptName, Conflux conflux) {
			super(scriptName + "_IN", conflux);
		}

		protected long getCycleSleepMillis() {
			return 1000;
		}

		protected void init() throws IOException {
			_writer = new BufferedWriter(new OutputStreamWriter(getProcess().getOutputStream()));
		}

		protected String[] registerInflowChannelIds() {
			return new String[] { "writeLines" };
		}

		protected void fill(Drop drop, Id context) throws Exception {

		}

		protected String[] registerOutflowChannelIds() {
			return new String[] { "wroteLines" };
		}

		private void writeLine(String line) throws IOException {
			_writer.write(line);
			_writer.write(_EOL);
			_writer.flush();
			_writtenDrops.add(new StringDrop(line));
		}

		protected Drop spill(Id context) throws Exception {
			if (thisId(context)) {
				if (_waitCycle < _waitCycleMax) {
					_waitCycle++;
				} else {
					_waitCycle = 0;
					writeLine("hi");
				}
			} else if ("wroteLines".equals(context.getId())) {
				if (!_writtenDrops.isEmpty()) {
					return _writtenDrops.remove();
				}
			}

			return null;
		}

		protected void fini() {
			if (_writer != null) {
				try {
					_writer.write(_CTRL_D);
					_writer.flush();
					_writer.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

	}

	public class OutputCascade extends Cascade {
		private boolean _isErrorOutput;
		private BufferedReader _reader;
		private LinkedList<StringDrop> _lineDrops = new LinkedList<StringDrop>();

		public OutputCascade(String scriptName, boolean isErrorOutput, Conflux conflux) {
			super(scriptName + (isErrorOutput ? "_ERR" : "_OUT"), conflux);
			_isErrorOutput = isErrorOutput;
		}

		protected long getCycleSleepMillis() {
			return 300;
		}

		protected void init() throws IOException {
			if (_isErrorOutput) {
				_reader = new BufferedReader(new InputStreamReader(getProcess().getErrorStream()));
			} else {
				_reader = new BufferedReader(new InputStreamReader(getProcess().getInputStream()));
			}
		}

		protected void fill(Drop drop, Id context) throws Exception {
			if (thisId(context) && (drop instanceof StringDrop)) {
				_lineDrops.add((StringDrop) drop);
			}
		}

		protected String[] registerOutflowChannelIds() {
			return new String[] { "lines" };
		}

		protected Drop spill(Id context) throws Exception {
			if (thisId(context)) {
				String line = _reader.readLine();
				if ((line != null) && !line.trim().isEmpty()) {
					return new StringDrop(line);
				}
			} else if ("lines".equals(context.getId())) {
				if (!_lineDrops.isEmpty())
					return _lineDrops.remove();
			}

			return null;
		}

		protected void fini() {
			if (_reader != null) {
				try {
					_reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

	}

}
