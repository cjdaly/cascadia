/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.CircuitPython.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;

public class CircuitPythonREPL extends Cascade {

	private static final char _CTRL_D = 4;
	private static final char _EOL = '\r';
	private String _devicePath;
	private REPLReader _reader;
	private REPLWriter _writer;
	private boolean _done = false;

	public CircuitPythonREPL(Conflux conflux) {
		super("CircuitPythonREPL", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000;
	}

	protected void init() {
		_devicePath = getConfigLocal("devicePath", "/dev/tty_NO_DEVICE");
		File deviceFile = new File(_devicePath);
		if (deviceFile.exists()) {
			_reader = new REPLReader();
			_reader.start();

			_writer = new REPLWriter();
			_writer.start();
		} else {
			_done = true;
		}
	}

	protected void fini() {
		_done = true;
	}

	private final String[] _python = { //
			"##~RESET", //
			"import os", //
			"os.uname()", //
			"print('hello world!')", //
			"help()", //
			"help('modules')", //
			"12345", //
			"22 + 11", //
			"x = x + 1", //
			"print('x:' + str(x))", //
			"x = 0" //
	};

	protected Drop spill(Id context) throws Exception {
		switch (context.getId()) {
		case "replReadLine":
			String line = _reader.dequeueLine();
			return line == null ? null : new StringDrop(line);
		case "replWriteLine":
			if (random(64) == 1) {
				line = _python[random(_python.length)];
				_writer.enqueueLine(line);
				return new StringDrop(line);
			}
			return null;
		default:
			return null;
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "replReadLine", "replWriteLine" };
	}

	private class REPLReader extends REPLThread {
		private BufferedReader _reader;

		void init() throws FileNotFoundException, IOException {
			_reader = new BufferedReader(new FileReader(_devicePath));
		}

		void cycle() throws FileNotFoundException, IOException, InterruptedException {
			String line = _reader.readLine();
			while (line != null) {
				enqueueLine(line);
				line = _reader.readLine();
			}
		}

		void fini() throws IOException {
			_reader.close();
		}

	}

	private class REPLWriter extends REPLThread {
		private BufferedWriter _writer;

		void init() throws FileNotFoundException, IOException {
			_writer = new BufferedWriter(new FileWriter(_devicePath, true));
		}

		void cycle() throws FileNotFoundException, IOException, InterruptedException {
			String line = dequeueLine();
			while (line != null) {
				if (line.startsWith("#")) {
					if (line.startsWith("##~RESET")) {
						_writer.write(_CTRL_D);
						_writer.flush();
						_writer.write(_EOL);
						_writer.flush();
					}
				} else {
					_writer.write(line);
					_writer.write(_EOL);
					_writer.flush();
				}
				line = dequeueLine();
			}
		}

		void fini() throws IOException {
			_writer.close();
		}

	}

	private abstract class REPLThread extends Thread {
		private LinkedList<String> _lineBuffer = new LinkedList<String>();
		private int _lineBufferMax = 256;
		private int _cycle = 0;

		synchronized void enqueueLines(String... lines) {
			for (String line : lines) {
				if (line != null) {
					_lineBuffer.addFirst(line);
					while (_lineBuffer.size() > _lineBufferMax) {
						_lineBuffer.removeLast();
					}
				}
			}
		}

		synchronized void enqueueLine(String line) {
			enqueueLines(line);
		}

		synchronized String dequeueLine() {
			if (_lineBuffer.isEmpty())
				return null;
			return _lineBuffer.removeLast();
		}

		abstract void init() throws FileNotFoundException, IOException;

		abstract void cycle() throws FileNotFoundException, IOException, InterruptedException;

		abstract void fini() throws IOException;

		protected long getCycleSleepMillis() {
			return 200;
		}

		protected long getThreadSleepMillis() {
			return 50;
		}

		protected long getThreadPreInitSleepMillis() {
			return 5000;
		}

		protected long getThreadPostInitSleepMillis() {
			return 2000;
		}

		protected long getWaitCycles() {
			return getCycleSleepMillis() / getThreadSleepMillis();
		}

		public void run() {
			try {
				Thread.sleep(getThreadPreInitSleepMillis());
				init();
				Thread.sleep(getThreadPostInitSleepMillis());
				do {
					if (_cycle < getWaitCycles()) {
						_cycle++;
					} else {
						_cycle = 0;
						cycle();
					}
					Thread.sleep(getThreadSleepMillis());
				} while (!_done);
				fini();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

		}
	}
}
