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

public class LogUtil {

	public static void log(String message) {
		log(message, true);
	}

	public static void log(String message, boolean newline) {
		if (newline) {
			System.out.println(message);
		} else {
			System.out.print(message);
		}
	}
}
