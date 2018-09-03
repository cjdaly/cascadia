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

import net.locosoft.cascadia.core.Collector;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Connect;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;

public class CircuitPythonCollector extends Collector {

	public CircuitPythonCollector(Conflux conflux) {
		super("CircuitPythonCollector", conflux);
	}

	protected String[] registerInflowChannelQIds() {
		return new String[] { //
				"CircuitPython.CircuitPythonREPL.replReadLine", //
				"CircuitPython.CircuitPythonREPL.replWriteLine" //
		};
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { //
				"replReadLine_TensorFlow", //
				"replReadLineEcho_TensorFlow", //
				"replReadLineBlank_TensorFlow", //
				"replWriteLine_TensorFlow" //
		};
	}

	protected Connect[] registerConnects() {
		return new Connect[] { //
				new Connect.FanOut("replReadLine", //
						new String[] { //
								"replReadLine_TensorFlow", //
								"replReadLineEcho_TensorFlow", //
								"replReadLineBlank_TensorFlow" //
						}, //
						new REPLReadLineBuffer()), //
				new Connect.Single("replWriteLine", "replWriteLine_TensorFlow") //
		};
	}

	class REPLReadLineBuffer extends Connect.SimpleBuffer {
		public Drop spill(String outflowEntryId) {
			if (!(_buffer instanceof StringDrop))
				return null;

			StringDrop drop = (StringDrop) _buffer;
			String val = drop.getValue();
			if (val == null)
				return null;

			boolean isBlank = val.trim().isEmpty();
			boolean isEcho = val.startsWith(">>>");

			switch (outflowEntryId) {
			case "replReadLine_TensorFlow":
				return (isBlank || isEcho) ? null : drop;
			case "replReadLineEcho_TensorFlow":
				return isEcho ? drop : null;
			case "replReadLineBlank_TensorFlow":
				return isBlank ? drop : null;
			default:
				return null;
			}
		}
	}
}
