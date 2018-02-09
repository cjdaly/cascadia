/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core.util;

import java.io.File;
import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class CoreUtil {

	public static String getInternalVersion() {
		return System.getProperty("net.locosoft.cascadia.internalVersion");
	}

	private static String _homeDir;

	public static String getHomeDir() {
		if (_homeDir == null) {
			try {
				String eclipseLocation = System.getProperty("eclipse.home.location");
				IPath eclipsePath = new Path(new URI(eclipseLocation).getPath());
				IPath homePath = eclipsePath.removeLastSegments(1).removeTrailingSeparator();
				_homeDir = homePath.toString();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return _homeDir;
	}

	public static String getDataDir() {
		return getHomeDir() + "/data";
	}

	public static String getConfigDir() {
		return getHomeDir() + "/config";
	}

	//

	private static int _pid = -1;

	public static int getPID() {
		String pidFilePath = getHomeDir() + "/cascadia.PID";
		File pidFile = new File(pidFilePath);
		if (!pidFile.exists())
			return -1;

		if (_pid == -1) {
			String pidFileText = FileUtil.readFileToString(pidFilePath, false);
			_pid = ParseUtil.asInt(pidFileText.trim(), -1);
		}

		return _pid;
	}

	public static void stopCascadia() {
		File pidFile = new File(getHomeDir() + "/cascadia.PID");
		if (pidFile.exists()) {
			pidFile.delete();
		}
	}

}
