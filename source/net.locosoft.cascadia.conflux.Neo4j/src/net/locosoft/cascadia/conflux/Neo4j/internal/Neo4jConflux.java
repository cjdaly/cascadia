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

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;

public class Neo4jConflux extends Conflux {

	protected Cascade[] constructCascades() {
		return new Cascade[] { new Neo4jSession(this), new Neo4jCollector(this) };
	}

}
