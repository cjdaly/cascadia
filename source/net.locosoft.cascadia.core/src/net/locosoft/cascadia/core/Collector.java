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

import java.util.ArrayList;
import java.util.HashMap;

import net.locosoft.cascadia.core.Channel.Entry;
import net.locosoft.cascadia.core.Channel.Exit;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.util.LogUtil;

public abstract class Collector extends Cascade {

	private int _initCycles = 42;
	private boolean _initConnections = false;
	private ArrayList<Connect> _connects = new ArrayList<Connect>();
	private HashMap<String, Connect> _inflowExitIdToConnect = new HashMap<String, Connect>();
	private HashMap<String, Connect> _outflowEntryIdToConnect = new HashMap<String, Connect>();

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
				if (channel.extend(this, channel.getId())) {
					Exit exit = channel.getExit();
					_inflow.put(channel.getId(), exit);
					LogUtil.log("~> channel: " + channel.getQId());
				} else {
					LogUtil.log("!~> inflow channel already connected: " + channel.getQId());
				}
			} else {
				LogUtil.log("!~> inflow channel not found: " + qid);
			}
		}

		for (String qid : registerOutflowChannelQIds()) {
			Channel channel = findChannel(qid, false);
			if (channel != null) {
				if (channel.extend(this, channel.getId())) {
					Entry entry = channel.getEntry();
					_outflow.put(channel.getId(), entry);
					LogUtil.log("<~ channel: " + channel.getQId());
				} else {
					LogUtil.log("!~> outflow channel already connected: " + channel.getQId());
				}
			} else {
				LogUtil.log("!~> outflow channel not found: " + qid);
			}
		}

		for (Connect connect : registerConnects()) {
			LogUtil.log("~~ connect: " + connect.getMapping());
			for (String inflowExitId : connect.getInflowExitIds()) {
				if (_inflow.containsKey(inflowExitId)) {
					_connects.add(connect);
					_inflowExitIdToConnect.put(inflowExitId, connect);
				} else {
					LogUtil.log("!~~ inflowExitId not found: " + inflowExitId);
				}
			}
			for (String outflowEntryId : connect.getOutflowEntryIds()) {
				if (_outflow.containsKey(outflowEntryId)) {
					_connects.add(connect);
					_outflowEntryIdToConnect.put(outflowEntryId, connect);
				} else {
					LogUtil.log("!~~ outflowEntryId not found: " + outflowEntryId);
				}
			}
		}
	}

	protected void cycleBegin() throws Exception {
		if (!_initConnections) {
			initConnections();
			_initConnections = true;
		}
		for (Connect connect : _connects) {
			connect.cycleBegin();
		}
	}

	protected void fill(Drop drop, Id context) throws Exception {
		if (!thisId(context)) {
			Connect connect = _inflowExitIdToConnect.get(context.getId());
			if (connect != null) {
				connect.fill(drop, context.getId());
			}
		}
	}

	protected Drop spill(Id context) throws Exception {
		if (!thisId(context)) {
			Connect connect = _outflowEntryIdToConnect.get(context.getId());
			if (connect != null) {
				return connect.spill(context.getId());
			}
		}
		return null;
	}

	protected void cycleEnd() throws Exception {
		for (Connect connect : _connects) {
			connect.cycleEnd();
		}
	}

}
