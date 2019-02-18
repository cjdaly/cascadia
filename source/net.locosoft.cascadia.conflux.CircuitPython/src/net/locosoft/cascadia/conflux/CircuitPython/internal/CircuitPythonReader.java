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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;

public class CircuitPythonReader extends Cascade {

	private String _devicePath;
	private BufferedReader _reader;

	public CircuitPythonReader(Conflux conflux, int deviceIndex, String devicePath) {
		super("CPReader_" + deviceIndex, conflux);
		_devicePath = devicePath;
	}

	protected long getCycleSleepMillis() {
		return 300;
	}

	protected void init() {
		File deviceFile = new File(_devicePath);
		if (deviceFile.exists()) {
			try {
				_reader = new BufferedReader(new FileReader(_devicePath));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				_reader = null;
			}
		} else {
			_reader = null;
		}
	}

	protected void fill(Drop drop, Id context) throws Exception {
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "readLine", "echoLine" };
	}

	protected Drop spill(Id context) throws Exception {
		String line = _reader.readLine();
		if (line == null)
			return null;

		if (line.startsWith(">>>")) {
			if ("echoLine".equals(context.getId()))
				return new StringDrop(line);
		} else if ("readLine".equals(context.getId())) {
			return new StringDrop(line);
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
