/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.AllPoetry.internal;

import net.locosoft.cascadia.core.Collector;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Connect;

public class AllPoetryCollector extends Collector {

	public AllPoetryCollector(Conflux conflux) {
		super("AllPoetryCollector", conflux);
	}

	protected String[] registerInflowChannelQIds() {
		return new String[] { //
				"AllPoetry.AllPoetryReader.poemTitle", //
				"AllPoetry.AllPoetryReader.poemAuthor", //
				"AllPoetry.AllPoetryReader.poemLine" //
		};
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { //
				"poemTitle_Neo4j", //
				"poemAuthor_Neo4j", //
				"poemLine_Neo4j" //
		};
	}

	protected Connect[] registerConnects() {
		return new Connect[] { //
				new Connect.Single("poemTitle", "poemTitle_Neo4j"), //
				new Connect.Single("poemAuthor", "poemAuthor_Neo4j"), //
				new Connect.Single("poemLine", "poemLine_Neo4j") //
		};
	}
}
