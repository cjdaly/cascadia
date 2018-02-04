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

import net.locosoft.cascadia.core.IId;
import net.locosoft.cascadia.core.Strom;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.LongDrop;
import net.locosoft.cascadia.core.drop.StringDrop;
import net.locosoft.cascadia.core.util.ExecUtil;

public class BlinkStickStrom extends Strom {

	private int _index = 0;

	public BlinkStickStrom() {
		super("BlinkStick");
	}

	protected String[] registerInflowChannelIds() {
		return new String[] {};
	}

	public Drop pullInflow(IId entryId, IId cascadeId) {
		int action = ThreadLocalRandom.current().nextInt(3);
		if (action == 2) {
			int colorIndex = ThreadLocalRandom.current().nextInt(_BasicColors.length);
			String colorName = _BasicColors[colorIndex];
			return new StringDrop(colorName);
		} else {
			return new LongDrop(action);
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] {};
	}

	public void pushOutflow(Drop drop, IId exitId, IId cascadeId) {
		if (drop instanceof LongDrop) {
			LongDrop d = (LongDrop) drop;
			int index = d.asInt();
			if (index == 0 || index == 1) {
				_index = index;
			}
		}
		if (drop instanceof StringDrop) {
			StringDrop d = (StringDrop) drop;
			String colorName = d.getValue();
			if (isColorName(colorName)) {
				String blinkStickCommand = "blinkstick --limit 10 --index " + _index + " " + colorName;
				ExecUtil.execCommand(blinkStickCommand, null, null);
			}
		}
	}

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
