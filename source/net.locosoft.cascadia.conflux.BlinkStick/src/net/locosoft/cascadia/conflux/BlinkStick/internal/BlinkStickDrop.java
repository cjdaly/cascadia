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

import net.locosoft.cascadia.core.drop.ComplexDrop;

public final class BlinkStickDrop extends ComplexDrop<BlinkStick> {

	public BlinkStickDrop(BlinkStick[] blinkSticks) {
		super(blinkSticks);
	}

	public BlinkStick getDefault() {
		return BlinkStick._NullBlinkStick;
	}

	public String asString(int index) {
		BlinkStick blinkStick = getValue(index);
		return blinkStick._Serial;
	}

}
