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

public final class CharDrop extends NumericDrop {

	private char _default = '?';
	private char[] _values;

	public CharDrop(char... values) {
		_values = values == null ? new char[0] : values;
	}

	public String getTypeName() {
		return "char";
	}

	public boolean isScalar() {
		return _values.length == 1;
	}

	public int getSize() {
		return _values.length;
	}

	public char getValue() {
		if (_values.length == 0)
			return _default;
		else
			return _values[0];
	}

	public char getValue(int index) {
		if ((index < 0) || (index >= _values.length))
			return _default;
		else
			return _values[index];
	}

	//

	public boolean asBoolean() {
		return getValue() != 0;
	}

	public int asInt() {
		return getValue();
	}

	public long asLong() {
		return getValue();
	}

	public float asFloat() {
		return (float) getValue();
	}

	public double asDouble() {
		return (double) getValue();
	}

	public String asString(int index) {
		return Character.toString(getValue(index));
	}

}
