/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.vitals.internal;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.LongDrop;

public class JavaRuntimeCascade extends Cascade {

	public JavaRuntimeCascade(Conflux conflux) {
		super("javaRuntimeSpring", conflux);
	}

	public Drop localInflow(Id contextId) {
		Runtime runtime = Runtime.getRuntime();
		switch (contextId.getId()) {
		case "totalMemory":
			return new LongDrop(runtime.totalMemory());
		case "freeMemory":
			return new LongDrop(runtime.freeMemory());
		case "maxMemory":
			return new LongDrop(runtime.maxMemory());
		case "availableProcessors":
			return new LongDrop(runtime.availableProcessors());
		default:
			return null;
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "totalMemory", "freeMemory", "maxMemory", "availableProcessors" };
	}
}
