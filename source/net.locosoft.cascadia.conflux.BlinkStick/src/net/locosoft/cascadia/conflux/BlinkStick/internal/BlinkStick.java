/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.BlinkStick.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlinkStick {

	public static final BlinkStick _NullBlinkStick = new BlinkStick();

	public final String _Serial;
	public final String _Description;
	public final String _CurrentColor;
	public final int _LED_Count;

	private BlinkStick() {
		_Serial = "BS-??";
		_Description = "???";
		_CurrentColor = "#000000";
		_LED_Count = 0;
	}

	private static final Pattern _descriptionPattern = Pattern.compile("Description:\\s+(.*)");
	private static final Pattern _serialPattern = Pattern.compile("Serial:\\s+(.*)");
	private static final Pattern _currentColorPattern = Pattern.compile("Current Color:\\s+(.*)");

	public BlinkStick(String infoText) {
		Matcher matcher;

		matcher = _serialPattern.matcher(infoText);
		_Serial = matcher.find() ? matcher.group(1).trim() : "?";

		matcher = _descriptionPattern.matcher(infoText);
		_Description = matcher.find() ? matcher.group(1).trim() : "?";

		matcher = _currentColorPattern.matcher(infoText);
		_CurrentColor = matcher.find() ? matcher.group(1).trim() : "?";

		_LED_Count = isNano() ? 2 : 8;
	}

	public boolean isNano() {
		return _Description.endsWith("Nano");
	}

}
