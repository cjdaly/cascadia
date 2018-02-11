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

public abstract class ComplexDrop<T> extends Drop {

	private final T[] _values;

	@SuppressWarnings("unchecked")
	public ComplexDrop(T value) {
		_values = (T[]) new Object[] { value };
	}

	@SuppressWarnings("unchecked")
	public ComplexDrop(T[] values) {
		_values = values == null ? (T[]) new Object[0] : values;
	}

	public abstract T getDefault();

	public String getTypeName() {
		return "complex";
	}

	public boolean isNumeric() {
		return false;
	}

	public boolean isArray() {
		return _values.length != 1;
	}

	public boolean isComplex() {
		return true;
	}

	public int getSize() {
		return _values.length;
	}

	public T getValue() {
		if (_values.length == 0)
			return getDefault();
		else {
			T v = _values[0];
			return v == null ? getDefault() : v;
		}
	}

	public T getValue(int index) {
		if ((index < 0) || (index >= _values.length))
			return getDefault();
		else {
			T v = _values[index];
			return v == null ? getDefault() : v;
		}
	}

}
