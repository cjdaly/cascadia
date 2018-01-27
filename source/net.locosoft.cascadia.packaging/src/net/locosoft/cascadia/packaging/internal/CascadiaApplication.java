/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.packaging.internal;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import net.locosoft.cascadia.core.util.CoreUtil;
import net.locosoft.cascadia.core.util.LogUtil;

public class CascadiaApplication implements IApplication {

	private boolean _stop = false;

	public Object start(IApplicationContext context) throws Exception {
		int cycle = 0;

		String version = CoreUtil.getInternalVersion();
		LogUtil.log("Starting Cascadia (version: " + version + ") ...");

		int pid = CoreUtil.getPID();
		while ((pid == -1) && (!_stop)) {
			LogUtil.log("... waiting for cascadia.PID file ...");
			Thread.sleep(500);
			pid = CoreUtil.getPID();
		}

		LogUtil.log("Cascadia process: " + pid + " ...");

		while ((pid != -1) && (!_stop)) {

			cycle++;
			if (cycle > 10) {
				LogUtil.log("...");
				cycle = 0;
			}

			Thread.sleep(500);
			pid = CoreUtil.getPID();
		}

		return IApplication.EXIT_OK;
	}

	public void stop() {
		_stop = true;
	}

}
