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

import java.util.LinkedList;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.exceptions.Neo4jException;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.BooleanDrop;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.util.LogUtil;

public class Neo4jSession extends Cascade {

	private Driver _driver;
	private Session _session;
	private LinkedList<Cypher> _cypherQueue = new LinkedList<Cypher>();

	public Neo4jSession(Conflux conflux) {
		super("Neo4jSession", conflux);
	}

	protected void init() {
		_driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "cascade"));
		_session = _driver.session();
		if (!_session.isOpen()) {
			LogUtil.log("Neo4j session not open!");
		}
	}

	protected long getCycleSleepMillis() {
		return 1000 * 2;
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "babble", "wordCount" };
	}

	protected Drop spill(Id context) {
		if (thisId(context))
			return new BooleanDrop(true);
		else
			return null;
	}

	protected String[] registerInflowChannelIds() {
		return new String[] { "commonWord", "interestingWord", "mixedToken", "nGram" };
	}

	protected void fill(Drop drop, Id context) {
		if (thisId(context)) {
			if (_session.isOpen()) {
				LogUtil.log(this, "Neo4j session open. " + drop);
			}
		}
	}

	//
	//

	synchronized int getCypherQueueLength() {
		return _cypherQueue.size();
	}

	synchronized void enqueueCypher(Cypher cypher) {
		_cypherQueue.add(cypher);
	}

	synchronized Cypher dequeueCypher() {
		if (_cypherQueue.isEmpty())
			return null;
		else
			return _cypherQueue.removeFirst();
	}

	synchronized void runCypher() {

		Cypher cypher = dequeueCypher();
		if (cypher == null)
			return;

		if (!_session.isOpen())
			return;

		try {
			StatementResult result = null;
			if (cypher.useTransaction()) {
				try (Transaction tx = _session.beginTransaction()) {
					result = tx.run(cypher.getText(), cypher.getParams());
					tx.success();
				}
			} else {
				result = _session.run(cypher.getText(), cypher.getParams());
			}

			if (result != null) {
				cypher.handleResult(result);
			}
		} catch (Neo4jException ex) {
			LogUtil.log(ex.toString());
		}

	}

}
