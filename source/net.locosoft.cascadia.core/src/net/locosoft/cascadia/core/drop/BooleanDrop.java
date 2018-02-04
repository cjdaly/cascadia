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

public class BooleanDrop extends NumericDrop {

	private boolean _default = false;
	private boolean _value;
	private boolean[] _values;

	public BooleanDrop(boolean value) {
		_value = value;
	}

	public BooleanDrop(boolean[] values) {
		_values = values == null ? new boolean[0] : values;
	}

	public char getTypeChar() {
		return isArray() ? 'B' : 'b';
	}

	public String getTypeName() {
		return "boolean";
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

	public boolean getValue() {
		if (_values == null)
			return _value;
		if (_values.length == 0)
			return _default;
		else
			return _values[0];
	}

	public boolean getValue(int index) {
		if (_values == null)
			return index == 0 ? _value : _default;
		if ((index < 0) || (index >= _values.length))
			return _default;
		else
			return _values[index];
	}

	//

	public boolean asBoolean() {
		return getValue();
	}

	public int asInt() {
		return getValue() ? 1 : 0;
	}

	public long asLong() {
		return getValue() ? 1 : 0;
	}

	public float asFloat() {
		return getValue() ? 1 : 0;
	}

	public double asDouble() {
		return getValue() ? 1 : 0;
	}

	public String asString(int index) {
		return Boolean.toString(getValue(index));
	}

}
