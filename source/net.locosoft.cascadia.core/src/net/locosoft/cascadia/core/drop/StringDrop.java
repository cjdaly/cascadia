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

public final class StringDrop extends ComplexDrop<String> {

	public StringDrop(String... values) {
		super(values);
	}

	public String getDefault() {
		return "???";
	}

	public String getTypeName() {
		return "string";
	}

}
