/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core;

public class Id {

	public static final String _QId_Separator = ".";

	private String _id;
	private String _qid;
	private final Id _qualifier;

	Id() {
		this(null, null);
	}

	Id(String id) {
		this(id, null);
	}

	Id(String id, Id qualifier) {
		_id = id == null ? "<?>" : id;
		_qualifier = qualifier;
	}

	protected final Id newSubId(String id) {
		return new Id(id, this);
	}

	void initId(String id) {
		_id = id;
	}

	public final String getId() {
		return _id;
	}

	public final boolean thisId(Id id) {
		return id == this;
	}

	public final Id getQualifier() {
		return _qualifier;
	}

	public final String getQId() {
		if (_qid == null) {
			if (_qualifier == null) {
				_qid = _id;
			} else {
				_qid = _qualifier.getQId() + _QId_Separator + _id;
			}
		}
		return _qid;
	}

	static String[] split(String qid) {
		if (qid == null)
			return new String[0];
		else
			return qid.split("\\.");
	}

	public String toString() {
		return _id;
	}

}
