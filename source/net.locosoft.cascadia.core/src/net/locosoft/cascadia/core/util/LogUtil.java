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
import java.util.Properties;

import net.locosoft.cascadia.core.Id;

public class LogUtil {

	public static void log(String message) {
		_Logger.log(null, message, true);
	}

	public static void log(String message, boolean newline) {
		_Logger.log(null, message, newline);
	}

	public static void log(Id context, String message) {
		_Logger.log(context, message, true);
	}

	public static void log(Id context, String message, boolean newline) {
		_Logger.log(context, message, newline);
	}

	public static boolean isEnabled(Id context) {
		return _Logger.isEnabled(context);
	}

	private static final Logger _Logger = new Logger();

	static class Logger {
		private String _logPropertiesFilePath;
		private File _logPropertiesFile;
		private long _logPropertiesFileModified;
		private Properties _logProperties;

		Logger() {
			_logPropertiesFilePath = CoreUtil.getConfigDir() + "/log.properties";
			_logPropertiesFile = new File(_logPropertiesFilePath);
		}

		synchronized boolean isEnabled(Id context) {
			long mod = _logPropertiesFile.lastModified();
			if ((_logProperties == null) || (mod > _logPropertiesFileModified)) {
				_logProperties = FileUtil.loadPropertiesFile(_logPropertiesFilePath);
				_logPropertiesFileModified = mod;
				LogUtil.log(
						"Loaded log properties file: " + _logPropertiesFilePath + ", size:" + _logProperties.size());
			}
			String value = _logProperties.getProperty(context.getQId(), "false");
			boolean isEnabled = value.equals("true");
			return isEnabled;
		}

		synchronized void log(Id context, String message, boolean newline) {
			if (context == null) {
				if (newline) {
					System.out.println(message);
				} else {
					System.out.print(message);
				}
			} else {
				if (isEnabled(context)) {
					if (newline) {
						System.out.println("<" + context.getQId() + "> " + message);
					} else {
						System.out.print("<" + context.getQId() + "> " + message);
					}
				}
			}
		}

	}
}
