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

import java.util.ArrayList;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;

public class CircuitPythonConflux extends Conflux {

	protected Cascade[] constructCascades() {
		ArrayList<Cascade> cascades = new ArrayList<Cascade>();

		for (int i = 0; i < 4; i++) {
			String devicePath = getConfigLocal("devicePath." + i, null);
			if (devicePath != null) {
				CircuitPythonReader cpReader = new CircuitPythonReader(this, i, devicePath);
				cascades.add(cpReader);
				CircuitPythonWriter cpWriter = new CircuitPythonWriter(this, i, devicePath, cpReader);
				cascades.add(cpWriter);
			}
		}

		return (Cascade[]) cascades.toArray(new Cascade[cascades.size()]);
	}

}
