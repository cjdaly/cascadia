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

import net.locosoft.cascadia.core.Collector;
import net.locosoft.cascadia.core.Conflux;

public class Neo4jCollector extends Collector {

	public Neo4jCollector(Conflux conflux) {
		super("neo4jCollector", conflux);
	}

	protected String[] registerInflowChannelQIds() {
		return new String[] { //
				"AllPoetry.AllPoetryCollector.poemTitle_Neo4j", //
				"AllPoetry.AllPoetryCollector.poemAuthor_Neo4j", //
				"AllPoetry.AllPoetryCollector.poemLine_Neo4j" //
		};
	}

}
