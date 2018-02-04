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

public class StringDrop extends Drop {

	private String _default = "???";
	private String _value;
	private String[] _values;

	public StringDrop(String value) {
		_value = value;
	}

	public StringDrop(String[] values) {
		_values = values == null ? new String[0] : values;
	}

	public char getTypeChar() {
		return isArray() ? 'S' : 's';
	}

	public String getTypeName() {
		return "string";
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

	public String getValue() {
		if (_values == null)
			return _value;
		if (_values.length == 0)
			return _default;
		else
			return _values[0];
	}

	public String getValue(int index) {
		if (_values == null)
			return index == 0 ? _value : _default;
		if ((index < 0) || (index >= _values.length))
			return _default;
		else
			return _values[index];
	}

	public String asString(int index) {
		return getValue(index);
	}

}
