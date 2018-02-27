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

public final class FloatDrop extends NumericDrop {

	private float _default = (float) -1.0;
	private float[] _values;

	public FloatDrop(float... values) {
		_values = values == null ? new float[0] : values;
	}

	public String getTypeName() {
		return "float";
	}

	public boolean isScalar() {
		return _values.length == 1;
	}

	public int getSize() {
		return _values.length;
	}

	public float getValue() {
		if (_values.length == 0)
			return _default;
		else
			return _values[0];
	}

	public float getValue(int index) {
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
		return (int) getValue();
	}

	public long asLong() {
		return (long) getValue();
	}

	public float asFloat() {
		return getValue();
	}

	public double asDouble() {
		return getValue();
	}

	public String asString(int index) {
		return Float.toString(getValue(index));
	}

}
