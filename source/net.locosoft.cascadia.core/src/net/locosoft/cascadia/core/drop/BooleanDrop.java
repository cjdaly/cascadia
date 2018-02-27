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

public final class BooleanDrop extends NumericDrop {

	private boolean _default = false;
	private boolean[] _values;

	public BooleanDrop(boolean... values) {
		_values = values == null ? new boolean[0] : values;
	}

	public String getTypeName() {
		return "boolean";
	}

	public boolean isScalar() {
		return _values.length == 1;
	}

	public int getSize() {
		return _values.length;
	}

	public boolean getValue() {
		if (_values.length == 0)
			return _default;
		else
			return _values[0];
	}

	public boolean getValue(int index) {
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
