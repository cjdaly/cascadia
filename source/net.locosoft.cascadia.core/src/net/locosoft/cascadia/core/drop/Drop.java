/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core.drop;

public abstract class Drop {

	private long _creationTimeMillis;

	public Drop() {
		_creationTimeMillis = System.currentTimeMillis();
	}

	public long getCreationTime() {
		return _creationTimeMillis;
	}

	public abstract boolean isArray();

	public abstract int getSize();

}
