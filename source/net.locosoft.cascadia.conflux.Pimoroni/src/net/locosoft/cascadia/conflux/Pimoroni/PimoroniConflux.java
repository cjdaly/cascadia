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

import java.util.ArrayList;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;

public class PimoroniConflux extends Conflux {

	protected Cascade[] constructCascades() {
		ArrayList<Cascade> cascades = new ArrayList<Cascade>();

		if ("true".equals(getConfigLocal("LedShim.present", "false"))) {
			PythonREPLProcess p = new PythonREPLProcess("led_shim", this);
			cascades.add(p.getErrorCascade());
			cascades.add(p.getOutputCascade());
			cascades.add(p.getInputCascade());
		}

		if ("true".equals(getConfigLocal("PianoHat.present", "false"))) {
			PythonREPLProcess p = new PythonREPLProcess("piano_hat", this);
			cascades.add(p.getErrorCascade());
			cascades.add(p.getOutputCascade());
			cascades.add(p.getInputCascade());
		}

		return (Cascade[]) cascades.toArray(new Cascade[cascades.size()]);
	}

	String getScriptPath(String scriptName) {
		return getConfluxPath() + "/" + scriptName + ".py";
	}

}
