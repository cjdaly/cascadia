/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core;

public abstract class Strom extends Cascade implements IEntry, IExit {

	public Strom(String id) {
		super(id);
	}

	protected IExit[] registerInflowExits() {
		return new IExit[] { this };
	}

	protected IEntry[] registerOutflowEntries() {
		return new IEntry[] { this };
	}

}
