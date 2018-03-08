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

import net.locosoft.cascadia.core.drop.Drop;

public class Connector extends Cascade {

	public Connector(String id, Conflux conflux) {
		super(id, conflux);
	}

	protected long getCycleSleepMillis() {
		return 200;
	}

	protected long getThreadSleepMillis() {
		return 50;
	}

	protected final String[] registerInflowChannelIds() {
		return new String[0];
	}

	protected final String[] registerOutflowChannelIds() {
		return new String[0];
	}

	protected Route[] registerRoutes() {
		return new Route[0];
	}

	protected void cycleBegin() throws Exception {
		// find and bind with Connectors of remote Cascades
		// (and local Collectors)
	}

	protected void fill(Drop drop, Id context) throws Exception {
		//
	}

	protected Drop spill(Id context) throws Exception {
		return null;
	}

	public class Route {

		final String _fromExitQId;
		final String _toEntryQId;
		private Drop _drop;

		public Route(String fromExitQId, String toEntryQId) {
			_fromExitQId = fromExitQId;
			_toEntryQId = toEntryQId;
		}

		public void fill(Drop drop) {
			_drop = drop;
		}

		public Drop spill() {
			return _drop;
		}

	}
}
