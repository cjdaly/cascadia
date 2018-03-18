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

import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import net.locosoft.cascadia.core.Channel.Entry;
import net.locosoft.cascadia.core.Channel.Exit;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.util.LogUtil;

public abstract class Cascade extends Id implements Runnable {

	private final Conflux _conflux;
	private Thread _thread = new Thread(this);
	private int _cycle = 0;
	private boolean _stop = false;
	private boolean _done = false;

	TreeMap<String, Channel.Exit> _inflow = new TreeMap<String, Channel.Exit>();
	TreeMap<String, Channel.Entry> _outflow = new TreeMap<String, Channel.Entry>();

	public Cascade(String id, Conflux conflux) {
		super(id, conflux);
		_conflux = conflux;
	}

	void start() {
		LogUtil.log("~ cascade: " + getQId());

		for (String id : registerInflowChannelIds()) {
			Channel.Exit exit = new Channel(id, this, false).getExit();
			_inflow.put(id, exit);
			LogUtil.log("~> channel: " + exit.getChannel().getQId());
		}
		for (String id : registerOutflowChannelIds()) {
			Channel.Entry entry = new Channel(id, this, true).getEntry();
			_outflow.put(id, entry);
			LogUtil.log("<~ channel: " + entry.getChannel().getQId());
		}

		_thread.start();
	}

	void stop() {
		LogUtil.log("~ stopping cascade: " + getQId());
		_stop = true;

		int waitCycles = 4;
		try {
			Thread.sleep(getThreadSleepMillis() * 2);
			while (!_done && waitCycles != 0) {
				waitCycles--;
				LogUtil.log("...");
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
		}
		if (waitCycles == 0) {
			LogUtil.log("- skipping cascade: " + getQId());
		}
	}

	Channel getInflowChannel(String id) {
		Exit exit = _inflow.get(id);
		return exit == null ? null : exit.getChannel();
	}

	Channel getOutflowChannel(String id) {
		Entry entry = _outflow.get(id);
		return entry == null ? null : entry.getChannel();
	}

	Channel findChannel(String qid, boolean isOutflow) {
		return _conflux.findChannel(qid, isOutflow);
	}

	protected String[] registerInflowChannelIds() {
		return new String[0];
	}

	protected String[] registerOutflowChannelIds() {
		return new String[0];
	}

	protected void init() {
	}

	protected void fini() {
	}

	protected boolean cycleSkip() {
		return false;
	}

	protected void cycleBegin() throws Exception {
	}

	protected void cycleEnd() throws Exception {
	}

	protected void fill(Drop drop, Id context) throws Exception {
	}

	protected Drop spill(Id context) throws Exception {
		return null;
	}

	protected long getCycleSleepMillis() {
		return 3000;
	}

	protected long getThreadSleepMillis() {
		return 100;
	}

	protected long getWaitCycles() {
		return getCycleSleepMillis() / getThreadSleepMillis();
	}

	protected final int random(int bound) {
		return ThreadLocalRandom.current().nextInt(bound);
	}

	protected final Id getCascadiaId(String suffix) {
		return _conflux.getCascadiaId(suffix);
	}

	protected final String getConfig(Id id, String default_) {
		return _conflux.getConfig(id, default_);
	}

	public void run() {
		while (!_stop) {
			try {
				if (_cycle < getWaitCycles()) {
					_cycle++;
				} else if (cycleSkip()) {
					_cycle = 0;
				} else {
					_cycle = 0;
					cycleBegin();

					Drop drop;
					// Nx1 - inflow
					for (Channel.Exit exit : _inflow.values()) {
						drop = exit.pull();
						if (drop != null) {
							Channel exitChannel = exit.getChannel();
							if (LogUtil.isEnabled(exit)) {
								LogUtil.log(exit, "<~ " + exitChannel.getId() + " " + drop);
							}
							fill(drop, exitChannel);
						}
					}

					// 1x1 - crossover
					drop = spill(this);
					if (drop != null) {
						if (LogUtil.isEnabled(this)) {
							LogUtil.log(this, "~> " + this.getId() + " " + drop);
						}
						fill(drop, this);
					}

					// 1xM - outflow
					for (Channel.Entry entry : _outflow.values()) {
						Channel entryChannel = entry.getChannel();
						drop = spill(entryChannel);
						if (drop != null) {
							if (LogUtil.isEnabled(entry)) {
								LogUtil.log(entry, "~> " + entryChannel.getId() + " " + drop);
							}
							entry.push(drop);
						}
					}

					cycleEnd();
				}
				Thread.sleep(getThreadSleepMillis());
			} catch (InterruptedException e) {
				_stop = true;
			} catch (Exception e) {
				_stop = true;
				e.printStackTrace();
			}
		}

		LogUtil.log(". stopped cascade: " + getQId());
		_done = true;
	}

}
