/*********************************************************************
* Copyright (c) 2019 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.CircuitPython.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;

public class CircuitPythonWriter extends Cascade {

	private static final char _CTRL_D = 4;
	private static final char _EOL = '\r';

	private String _devicePath;
	private CircuitPythonReader _cpReader;

	private BufferedWriter _writer;

	private int _waitCycle = 0;
	private int _waitCycleMax = 60;

	private int _dutyCycle = 0;
	private int _dutyCycleMax = 8;

	private LinkedList<StringDrop> _writtenDrops = new LinkedList<StringDrop>();

	public CircuitPythonWriter(Conflux conflux, int deviceIndex, String devicePath, CircuitPythonReader cpReader) {
		super("CPWriter_" + deviceIndex, conflux);
		_devicePath = devicePath;
		_cpReader = cpReader;
	}

	protected long getCycleSleepMillis() {
		return 1000;
	}

	protected void init() {
		File deviceFile = new File(_devicePath);
		if (deviceFile.exists()) {
			try {
				_writer = new BufferedWriter(new FileWriter(_devicePath, true));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				_writer = null;
			} catch (IOException ex) {
				ex.printStackTrace();
				_writer = null;
			}
		} else {
			_writer = null;
		}
	}

	protected String[] registerInflowChannelIds() {
		return new String[] { "writeLines" };
	}

	protected void fill(Drop drop, Id context) throws Exception {
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "wroteLines" };
	}

	private void writePythonLine(String pythonLine) throws IOException {
		_writer.write(pythonLine);
		_writer.write(_EOL);
		_writer.flush();
		_writtenDrops.add(new StringDrop(pythonLine));
	}

	protected Drop spill(Id context) throws Exception {

		if (_cpReader.isStopped()) {
			// feed the reader to avoid blocking shutdown
			_writer.write(_EOL);
			_writer.flush();
			return null;
		}

		if (thisId(context)) {
			if (_waitCycle < _waitCycleMax) {
				_waitCycle++;
			} else {
				_waitCycle = 0;

				if (_dutyCycle >= _dutyCycleMax) {
					_dutyCycle = 0;
				} else {
					if (_dutyCycle == 0) {
						// rest
					} else if (_dutyCycle == 1) {
						// reset
						_writer.write(_CTRL_D);
						_writer.flush();
						_writer.write(_EOL);
						_writer.flush();
						_writtenDrops.add(new StringDrop("## RESET!"));
					} else if (_dutyCycle == 2) {
						writePythonLine("import cascadia");
					} else if (_dutyCycle == 3) {
						writePythonLine("cascadia.init()");
					} else if (_dutyCycle == 4) {
						writePythonLine("cascadia.RGB0_set(0,33,0)");
					} else if (_dutyCycle == 5) {
						writePythonLine("cascadia.RGB0_set(0,0,42)");
					} else {
						// ...
					}

					_dutyCycle++;
				}
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
				_writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
