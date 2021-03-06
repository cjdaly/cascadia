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

public abstract class NumericDrop extends Drop {

	public boolean isNumeric() {
		return true;
	}

	public boolean isComplex() {
		return false;
	}

	public abstract boolean asBoolean();

	public abstract int asInt();

	public abstract long asLong();

	public abstract float asFloat();

	public abstract double asDouble();

}
