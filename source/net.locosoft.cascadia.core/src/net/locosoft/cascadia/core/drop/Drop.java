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

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Drop {

	private static final SimpleDateFormat _timestampFormat = new SimpleDateFormat("HH:mm:ss.SS");
	private static final SimpleDateFormat _datestampFormat = new SimpleDateFormat("yyyyMMdd-HHmmss.SSSS");
	private final long _creationTimeMillis;

	Drop() {
		_creationTimeMillis = System.currentTimeMillis();
	}

	public long getCreationTime() {
		return _creationTimeMillis;
	}

	public String getTimestamp() {
		return _timestampFormat.format(new Date(_creationTimeMillis));
	}

	public String getDatestamp() {
		return _datestampFormat.format(new Date(_creationTimeMillis));
	}

	public abstract String getTypeName();

	public abstract boolean isNumeric();

	public abstract boolean isArray();

	public abstract boolean isComplex();

	public abstract int getSize();

	public abstract String asString(int index);

	public String asString() {
		if (getSize() == 0) {
			return "";
		} else if (!isArray() || (getSize() == 1)) {
			return asString(0);
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < getSize(); i++) {
				sb.append(asString(i));
				if (i != 0) {
					if (isNumeric())
						sb.append(',');
					else
						sb.append(";~;");
				}
			}
			return sb.toString();
		}
	}

	public String toString() {
		if (isArray()) {
			return "(drop:" + asString() + ";~type:" + getTypeName() + "[];~time:" + getTimestamp() + ")";
		} else {
			return "(drop:" + asString() + ";~type:" + getTypeName() + ";~time:" + getTimestamp() + ")";
		}
	}

}
