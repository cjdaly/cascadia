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

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.LongDrop;
import net.locosoft.cascadia.core.drop.StringDrop;
import net.locosoft.cascadia.core.util.ExecUtil;

public class BlinkStickCascade extends Cascade {

	private int _index = 0;
	private String _colorName;
	private String _commandLine;
	private String _infoText;

	public BlinkStickCascade(Conflux conflux) {
		super("BlinkStick", conflux);
	}

	protected String[] registerInflowChannelIds() {
		return new String[] {};
	}

	public Drop localInflow(Id contextId) {
		switch (contextId.getId()) {
		case "BlinkStick":
			int action = ThreadLocalRandom.current().nextInt(4);
			action--;
			switch (action) {
			case -1:
			case 0:
			case 1:
				return new LongDrop(action);
			case 2:
				int colorIndex = ThreadLocalRandom.current().nextInt(_BasicColors.length);
				String colorName = _BasicColors[colorIndex];
				return new StringDrop(colorName);
			}
		case "index":
			return new LongDrop(_index);
		case "colorName":
			return new StringDrop(_colorName);
		case "commandLine":
			if (_commandLine != null)
				return new StringDrop(_commandLine);
			break;
		case "description":
			if (_infoText != null) {
				Matcher matcher = _descriptionPattern.matcher(_infoText);
				String description = matcher.find() ? matcher.group(1) : "?";
				return new StringDrop(description);
			}
			break;
		case "serial":
			if (_infoText != null) {
				Matcher matcher = _serialPattern.matcher(_infoText);
				String serial = matcher.find() ? matcher.group(1) : "?";
				return new StringDrop(serial);
			}
			break;
		case "led0.RGB":
			if (_infoText != null) {
				Matcher matcher = _currentColorPattern.matcher(_infoText);
				String currentColor = matcher.find() ? matcher.group(1) : "?";
				return new StringDrop(currentColor);
			}
			break;
		case "led0.red":
		case "led0.green":
		case "led0.blue":
		}
		return null;
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { //
				"index", "colorName", "commandLine", //
				"description", "serial", //
				"led0.RGB", "led0.red", "led0.green", "led0.blue" //
		};
	}

	public void localOutflow(Drop drop, Id contextId) {
		if (drop instanceof LongDrop) {
			LongDrop d = (LongDrop) drop;
			int index = d.asInt();
			if (index == 0 || index == 1) {
				_index = index;
			} else if (index == -1) {
				StringBuilder output = new StringBuilder();
				String blinkStickCommand = "blinkstick -i";
				ExecUtil.execCommand(blinkStickCommand, output, null);
				_infoText = output.toString();
			}
		}
		if (drop instanceof StringDrop) {
			StringDrop d = (StringDrop) drop;
			String colorName = d.getValue();
			if (isColorName(colorName)) {
				String blinkStickCommand = "blinkstick --limit 10 --index " + _index + " " + colorName;
				ExecUtil.execCommand(blinkStickCommand, null, null);
				_colorName = colorName;
			}
		}
	}

	private static final Pattern _descriptionPattern = Pattern.compile("Description:\\s+(.*)");
	private static final Pattern _serialPattern = Pattern.compile("Serial:\\s+(.*)");
	private static final Pattern _currentColorPattern = Pattern.compile("Current Color:\\s+(.*)");
	//
	//

	private HashSet<String> _colorNames;

	private boolean isColorName(String name) {
		if (name == null)
			return false;

		if (_colorNames == null) {
			_colorNames = new HashSet<String>();
			for (String colorName : _ExtendedColors) {
				_colorNames.add(colorName);
			}
		}

		return _colorNames.contains(name.toLowerCase());
	}

	// see here: https://www.w3.org/TR/css3-color/
	private static final String[] _BasicColors = new String[] { //
			"black", "silver", "gray", "white", //
			"maroon", "red", "purple", "fuchsia", //
			"green", "lime", "olive", "yellow", //
			"navy", "blue", "teal", "aqua" //
	};
	//
	private static final String[] _ExtendedColors = new String[] { //
			"aliceblue", "antiquewhite", "aqua", "aquamarine", "azure", //
			"beige", "bisque", "black", "blanchedalmond", "blue", "blueviolet", "brown", "burlywood", //
			"cadetblue", "chartreuse", "chocolate", "coral", "cornflowerblue", "cornsilk", "crimson", "cyan", //
			"darkblue", "darkcyan", "darkgoldenrod", "darkgray", "darkgreen", "darkgrey", "darkkhaki", //
			"darkmagenta", "darkolivegreen", "darkorange", "darkorchid", "darkred", "darksalmon", //
			"darkseagreen", "darkslateblue", "darkslategray", "darkslategrey", "darkturquoise", //
			"darkviolet", "deeppink", "deepskyblue", "dimgray", "dimgrey", "dodgerblue", //
			"firebrick", "floralwhite", "forestgreen", "fuchsia", //
			"gainsboro", "ghostwhite", "gold", "goldenrod", "gray", "green", "greenyellow", "grey", //
			"honeydew", "hotpink", //
			"indianred", "indigo", "ivory", //
			"khaki", //
			"lavender", "lavenderblush", "lawngreen", "lemonchiffon", "lightblue", "lightcoral", //
			"lightcyan", "lightgoldenrodyellow", "lightgray", "lightgreen", "lightgrey", "lightpink", //
			"lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightslategrey", //
			"lightsteelblue", "lightyellow", "lime", "limegreen", "linen", //
			"magenta", "maroon", "mediumaquamarine", "mediumblue", "mediumorchid", "mediumpurple", //
			"mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise", "mediumvioletred", //
			"midnightblue", "mintcream", "mistyrose", "moccasin", //
			"navajowhite", "navy", //
			"oldlace", "olive", "olivedrab", "orange", "orangered", "orchid", //
			"palegoldenrod", "palegreen", "paleturquoise", "palevioletred", "papayawhip", "peachpuff", //
			"peru", "pink", "plum", "powderblue", "purple", //
			"red", "rosybrown", "royalblue", //
			"saddlebrown", "salmon", "sandybrown", "seagreen", "seashell", "sienna", "silver", //
			"skyblue", "slateblue", "slategray", "slategrey", "snow", "springgreen", "steelblue", //
			"tan", "teal", "thistle", "tomato", "turquoise", //
			"violet", //
			"wheat", "white", "whitesmoke", //
			"yellow", "yellowgreen" //
	};

}
