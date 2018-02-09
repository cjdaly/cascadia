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

public class IntDrop extends NumericDrop {

	private int _default = -1;
	private int _value;
	private int[] _values;

	public IntDrop(int value) {
		_value = value;
	}

	public IntDrop(int[] values) {
		_values = values == null ? new int[0] : values;
	}

	public char getTypeChar() {
		return isArray() ? 'I' : 'i';
	}

	public String getTypeName() {
		return "int";
	}

	public boolean isArray() {
		return _values != null;
	}

	public int getSize() {
		if (_values != null)
			return _values.length;
		else
			return 1;
	}

	public int getValue() {
		if (_values == null)
			return _value;
		if (_values.length == 0)
			return _default;
		else
			return _values[0];
	}

	public int getValue(int index) {
		if (_values == null)
			return index == 0 ? _value : _default;
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
		return Integer.toString(getValue(index));
	}

}
