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

import java.util.HashMap;

import net.locosoft.cascadia.core.Channel.Entry;
import net.locosoft.cascadia.core.Channel.Exit;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.util.LogUtil;

public abstract class Collector extends Cascade {

	private int _initCycles = 20;
	private boolean _initConnections = false;
	private HashMap<String, Tranche> _exitIdToTranche = new HashMap<String, Tranche>();
	private HashMap<String, Tranche> _entryIdToTranche = new HashMap<String, Tranche>();

	public Collector(String id, Conflux conflux) {
		super(id, conflux);
	}

	protected long getCycleSleepMillis() {
		return 200;
	}

	protected long getThreadSleepMillis() {
		return 50;
	}

	protected String[] registerInflowChannelQIds() {
		return new String[0];
	}

	protected String[] registerOutflowChannelQIds() {
		return new String[0];
	}

	protected Tranche[] registerTranches() {
		return new Tranche[0];
	}

	protected boolean cycleSkip() {
		if (_initCycles > 0) {
			_initCycles--;
			return true;
		}
		return false;
	}

	private void initConnections() {
		LogUtil.log("~ collector: " + getQId());

		for (String qid : registerInflowChannelQIds()) {
			Channel channel = findChannel(qid, true);
			if (channel != null) {
				if (channel.getExit() == null) {
					channel.extend(this);
					Exit exit = channel.getExit();
					if (exit != null) {
						_inflow.put(channel.getId(), exit);
						LogUtil.log("~> channel: " + channel.getQId());
					}
				}
			}
		}

		for (String qid : registerOutflowChannelQIds()) {
			Channel channel = findChannel(qid, false);
			if (channel != null) {
				if (channel.getEntry() == null) {
					channel.extend(this);
					Entry entry = channel.getEntry();
					if (entry != null) {
						_outflow.put(channel.getId(), entry);
						LogUtil.log("<~ channel: " + channel.getQId());
					}
				}
			}
		}

		for (Tranche tranche : registerTranches()) {
			if (_inflow.containsKey(tranche._fromExitId) && (_outflow.containsKey(tranche._toEntryId))) {
				_exitIdToTranche.put(tranche._fromExitId, tranche);
				_entryIdToTranche.put(tranche._toEntryId, tranche);
				LogUtil.log("~~ tranche: " + tranche._fromExitId + " -> " + tranche._toEntryId);
			}
		}
	}

	protected void cycleBegin() throws Exception {
		if (!_initConnections) {
			initConnections();
			_initConnections = true;
		}
	}

	protected void fill(Drop drop, Id context) throws Exception {
		Tranche tranche = _exitIdToTranche.get(context.getId());
		if (tranche != null) {
			tranche.fill(drop);
		}
	}

	protected Drop spill(Id context) throws Exception {
		Tranche tranche = _entryIdToTranche.get(context.getId());
		if (tranche != null) {
			return tranche.spill();
		}
		return null;
	}

	public static class Tranche {

		final String _fromExitId;
		final String _toEntryId;
		protected Drop _drop;

		public Tranche(String fromExitId, String toEntryId) {
			_fromExitId = fromExitId;
			_toEntryId = toEntryId;
		}

		public void fill(Drop drop) {
			_drop = drop;
		}

		public Drop spill() {
			return _drop;
		}

	}

}
