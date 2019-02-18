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
import java.util.LinkedList;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;

public class CircuitPythonReader extends Cascade {

	private String _devicePath;
	private BufferedReader _reader;

	private LinkedList<StringDrop> _readDrops = new LinkedList<StringDrop>();
	private LinkedList<StringDrop> _echoDrops = new LinkedList<StringDrop>();

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
		if (thisId(context) && _reader.ready()) {
			String line = _reader.readLine();
			if ((line != null) && !line.trim().isEmpty()) {
				if (line.startsWith(">>>")) {
					_echoDrops.add(new StringDrop(line));
				} else {
					_readDrops.add(new StringDrop(line));
				}
			}
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "readLine", "echoLine" };
	}

	protected Drop spill(Id context) throws Exception {
		switch (context.getId()) {
		case "readLine":
			if (!_readDrops.isEmpty())
				return _readDrops.remove();
			break;
		case "echoLine":
			if (!_echoDrops.isEmpty())
				return _echoDrops.remove();
			break;
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
