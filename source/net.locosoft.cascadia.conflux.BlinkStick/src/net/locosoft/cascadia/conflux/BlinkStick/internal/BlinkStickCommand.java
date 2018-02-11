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
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.IntDrop;
import net.locosoft.cascadia.core.drop.StringDrop;
import net.locosoft.cascadia.core.util.ExecUtil;

public class BlinkStickCommand extends Cascade {

	private int _index = 0;
	private String _colorName = "off";
	private String _commandLine = "";
	private TreeMap<String, BlinkStick> _blinkSticks = new TreeMap<String, BlinkStick>();

	public BlinkStickCommand(Conflux conflux) {
		super("BlinkStickCommand", conflux);
	}

	protected String[] registerInflowChannelIds() {
		return new String[] {};
	}

	public Drop localInflow(Id context) {
		if (thisId(context)) {
			int action = ThreadLocalRandom.current().nextInt(4);
			action--;
			switch (action) {
			case -1:
			case 0:
			case 1:
				return new IntDrop(action);
			case 2:
				int colorIndex = ThreadLocalRandom.current().nextInt(_BasicColors.length);
				String colorName = _BasicColors[colorIndex];
				return new StringDrop(colorName);
			}
		} else {
			switch (context.getId()) {
			case "index":
				return new IntDrop(_index);
			case "colorName":
				return new StringDrop(_colorName);
			case "commandLine":
				return new StringDrop(_commandLine);
			case "BlinkStick_Info":
				return new BlinkStickDrop(_blinkSticks.values().toArray(new BlinkStick[0]));
			}
		}
		return null;
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { //
				"index", "colorName", "commandLine", "BlinkStick_Info" //
		};
	}

	public void localOutflow(Drop drop, Id context) {
		if (thisId(context)) {
			if (drop instanceof IntDrop) {
				IntDrop d = (IntDrop) drop;
				int index = d.getValue();
				if (index == 0 || index == 1) {
					_index = index;
				} else if (index == -1) {
					StringBuilder output = new StringBuilder();
					_commandLine = "blinkstick -i";
					ExecUtil.execCommand(_commandLine, output, null);
					updateBlinkStickInfo(output.toString());
				}
			}
			if (drop instanceof StringDrop) {
				StringDrop d = (StringDrop) drop;
				String colorName = d.getValue();
				if (isColorName(colorName)) {
					_commandLine = "blinkstick --limit 10 --index " + _index + " " + colorName;
					ExecUtil.execCommand(_commandLine, null, null);
					_colorName = colorName;
				}
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
			_blinkSticks.put(blinkStick._Serial, blinkStick);

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
