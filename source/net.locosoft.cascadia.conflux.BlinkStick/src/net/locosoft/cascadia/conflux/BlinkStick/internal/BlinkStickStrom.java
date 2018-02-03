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

	// see here: https://www.w3.org/TR/css3-color/
	private static final String[] _BasicColors = new String[] { //
			"black", "silver", "gray", "white", //
			"maroon", "red", "purple", "fuchsia", //
			"green", "lime", "olive", "yellow", //
			"navy", "blue", "aqua", "teal" };

	private boolean isColorName(String name) {
		for (String colorName : _BasicColors) {
			if (colorName.equals(name))
				return true;
		}
		return false;
	}

}
