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

import net.locosoft.cascadia.core.IId;
import net.locosoft.cascadia.core.Spring;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.LongDrop;

public class JavaRuntimeSpring extends Spring {

	public JavaRuntimeSpring() {
		super("javaRuntimeSpring");
	}

	public Drop pullInflow(IId entryId, IId cascadeId) {
		Runtime runtime = Runtime.getRuntime();
		switch (entryId.getId()) {
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
