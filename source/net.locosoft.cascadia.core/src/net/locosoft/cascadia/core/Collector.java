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
	private HashMap<String, Connect> _exitIdToConnect = new HashMap<String, Connect>();
	private HashMap<String, Connect> _entryIdToConnect = new HashMap<String, Connect>();

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

	protected Connect[] registerConnects() {
		return new Connect[0];
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

		for (Connect connect : registerConnects()) {
			if (_inflow.containsKey(connect._fromExitId) && (_outflow.containsKey(connect._toEntryId))) {
				_exitIdToConnect.put(connect._fromExitId, connect);
				_entryIdToConnect.put(connect._toEntryId, connect);
				LogUtil.log("~~ connect: " + connect._fromExitId + " -> " + connect._toEntryId);
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
		Connect connect = _exitIdToConnect.get(context.getId());
		if (connect != null) {
			connect.fill(drop);
		}
	}

	protected Drop spill(Id context) throws Exception {
		Connect connect = _entryIdToConnect.get(context.getId());
		if (connect != null) {
			return connect.spill();
		}
		return null;
	}

	public static class Connect {

		final String _fromExitId;
		final String _toEntryId;
		protected Drop _drop;

		public Connect(String fromExitId, String toEntryId) {
			_fromExitId = fromExitId;
			_toEntryId = toEntryId;
		}

		public void fill(Drop drop) {
			_drop = drop;
		}

		public Drop spill() {
			Drop drop = _drop;
			_drop = null;
			return drop;
		}

	}

}
