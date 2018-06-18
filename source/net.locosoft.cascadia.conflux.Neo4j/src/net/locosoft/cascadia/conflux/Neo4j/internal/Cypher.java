/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.Neo4j.internal;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.exceptions.Neo4jException;

import net.locosoft.cascadia.core.util.LogUtil;

public abstract class Cypher {

	private HashMap<String, Object> _params = new HashMap<String, Object>();
	private boolean _useTransaction = false;
	private boolean _wasHandled = false;

	public Cypher() {
		this(false);
	}

	public Cypher(boolean useTransaction) {
		_useTransaction = useTransaction;
	}

	public abstract String getText();

	protected void handle(StatementResult result) {
	}

	public final boolean useTransaction() {
		return _useTransaction;
	}

	Map<String, Object> getParams() {
		return _params;
	}

	public void addParam(String key, String value) {
		_params.put(key, value);
	}

	public void addParam(String key, long value) {
		_params.put(key, value);
	}

	public void addParam(String key, double value) {
		_params.put(key, value);
	}

	public void addParam(String key, Map<String, Object>[] map) {
		_params.put(key, map);
	}

	public final boolean wasHandled() {
		return _wasHandled;
	}

	final void handleResult(StatementResult result) {
		try {
			handle(result);
			_wasHandled = true;
		} catch (Neo4jException ex) {
			LogUtil.log(ex.toString());
		}
	}
}