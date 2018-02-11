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

	private static String _pidFilePath;
	private static File _pidFile;
	private static int _pid = -1;

	public static int getPID() {
		if (_pidFilePath == null) {
			_pidFilePath = getHomeDir() + "/cascadia.PID";
			_pidFile = new File(_pidFilePath);
		}

		if (!_pidFile.exists())
			return -1;

		if (_pid == -1) {
			String pidFileText = FileUtil.readFileToString(_pidFilePath, false);
			_pid = ParseUtil.asInt(pidFileText.trim(), -1);
		}

		return _pid;
	}

	public static void stopCascadia() {
		if ((_pidFile != null) && _pidFile.exists()) {
			_pidFile.delete();
		}
	}

}
