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

import net.locosoft.cascadia.core.IId;
import net.locosoft.cascadia.core.Strom;
import net.locosoft.cascadia.core.drop.Drop;

public class BlinkStickStrom extends Strom {

	public BlinkStickStrom() {
		super("BlinkStick");
	}

	// inflow

	protected String[] registerInflowChannelIds() {
		return new String[] {};
	}

	public Drop pullInflow(IId entryId, IId cascadeId) {
		return null;
	}

	// outflow

	protected String[] registerOutflowChannelIds() {
		return new String[] { "serialNumber", "type", "led0.RGB" };
	}

	public void pushOutflow(Drop drop, IId exitId, IId cascadeId) {
	}

}
