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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.IntDrop;
import net.locosoft.cascadia.core.drop.StringDrop;
import net.locosoft.cascadia.core.util.ExecUtil;

public class BlinkStickCommand extends Cascade {

	private final Id _paramLimit = newSubId("paramLimit");

	private ArrayList<BlinkStick> _blinkSticks = new ArrayList<BlinkStick>();
	private BlinkStick _blinkStick;
	private int _ledIndex = -1;
	private String _colorName;
	private String _commandLine;

	public BlinkStickCommand(Conflux conflux) {
		super("BlinkStickCommand", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 5;
	}

	protected String[] registerInflowChannelIds() {
		return new String[] {};
	}

	private int random(int bound) {
		return ThreadLocalRandom.current().nextInt(bound);
	}

	public Drop localInflow(Id context) {
		if (thisId(context)) {
			if (_blinkSticks.isEmpty()) {
				_blinkStick = null;
				return new IntDrop(-1); // blinkstick -i
			} else {
				int blinkStickIndex = random(_blinkSticks.size());
				_blinkStick = _blinkSticks.get(blinkStickIndex);
				_ledIndex = random(_blinkStick._LED_Count);
				int colorIndex = random(_ExtendedColors.length);
				return new StringDrop(_ExtendedColors[colorIndex]);
			}
		} else {
			switch (context.getId()) {
			case "ledIndex":
				return new IntDrop(_ledIndex);
			case "colorName":
				return (_colorName == null) ? null : new StringDrop(_colorName);
			case "commandLine":
				return (_commandLine == null) ? null : new StringDrop(_commandLine);
			case "serial":
				return (_blinkStick == null) ? null : new StringDrop(_blinkStick._Serial);
			case "BlinkStick_Info":
				return new BlinkStickDrop(_blinkSticks.toArray(new BlinkStick[0]));
			}
		}
		return null;
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { //
				"ledIndex", "colorName", "commandLine", "serial", "BlinkStick_Info" //
		};
	}

	public void localOutflow(Drop drop, Id context) {
		if (thisId(context)) {
			if (drop instanceof IntDrop) {
				IntDrop d = (IntDrop) drop;
				int index = d.getValue();
				if (index == -1) {
					StringBuilder output = new StringBuilder();
					_commandLine = "blinkstick -i";
					ExecUtil.execCommand(_commandLine, output, null);
					updateBlinkStickInfo(output.toString());
					_blinkStick = null;
					_ledIndex = -1;
					_colorName = null;
				}
			} else if (drop instanceof StringDrop) {
				StringDrop d = (StringDrop) drop;
				String colorName = d.getValue();
				if (isColorName(colorName) && _blinkStick != null) {
					_colorName = colorName;
					_commandLine = "blinkstick" + //
							" --limit " + getConfig(_paramLimit, "20") + //
							" --serial " + _blinkStick._Serial + //
							" --index " + _ledIndex + //
							" " + colorName;
					ExecUtil.execCommand(_commandLine, null, null);
				}
				_blinkSticks.clear();
			}
		}
	}

	private void updateBlinkStickInfo(String rawInfoText) {
		_blinkSticks.clear();

		String infoTmp = rawInfoText;
		int index = infoTmp.lastIndexOf("Found device:");
		while (index >= 0) {
			String infoText = infoTmp.substring(index);

			BlinkStick blinkStick = new BlinkStick(infoText);
			_blinkSticks.add(blinkStick);

			infoTmp = infoTmp.substring(0, index);
			index = infoTmp.lastIndexOf("Found device:");
		}
	}

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
	static final String[] _BasicColors = new String[] { //
			"black", "silver", "gray", "white", //
			"maroon", "red", "purple", "fuchsia", //
			"green", "lime", "olive", "yellow", //
			"navy", "blue", "teal", "aqua" //
	};
	//
	static final String[] _ExtendedColors = new String[] { //
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
