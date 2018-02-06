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

import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.util.LogUtil;

public abstract class Cascade extends Id implements Runnable {

	private Thread _thread = new Thread(this);
	private int _cycle = 0;
	private boolean _stop = false;
	private boolean _done = false;

	private TreeMap<String, Channel.Exit> _inflow = new TreeMap<String, Channel.Exit>();
	private TreeMap<String, Channel.Entry> _outflow = new TreeMap<String, Channel.Entry>();

	public Cascade(String id, Conflux conflux) {
		super(id, conflux);
	}

	void start() {
		LogUtil.log("- starting cascade: " + getId());

		for (String id : registerInflowChannelIds()) {
			_inflow.put(id, new Channel(id, this, false).getExit());
			LogUtil.log(" - inflow channel: " + id);
		}
		for (String id : registerOutflowChannelIds()) {
			_outflow.put(id, new Channel(id, this, true).getEntry());
			LogUtil.log(" - outflow channel: " + id);
		}

		_thread.start();
	}

	void stop() {
		LogUtil.log("- stopping cascade: " + getId());
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
			LogUtil.log("- skipping cascade: " + getId());
		}
	}

	protected void init() {
	}

	protected void fini() {
	}

	protected Drop localInflow(Id contextId) {
		return null;
	}

	protected void localOutflow(Drop drop, Id contextId) {
	}

	protected String[] registerInflowChannelIds() {
		return new String[0];
	}

	protected String[] registerOutflowChannelIds() {
		return new String[0];
	}

	protected long getThreadSleepMillis() {
		return 100;
	}

	protected long getCycleSleepMillis() {
		return 3000;
	}

	protected long getSkipCycles() {
		return getCycleSleepMillis() / getThreadSleepMillis();
	}

	public void run() {
		while (!_stop) {
			try {
				if (_cycle < getSkipCycles()) {
					_cycle++;
				} else {

					// 1x1
					Drop drop = localInflow(this);
					if (drop != null)
						localOutflow(drop, this);

					// 1xM
					for (Channel.Entry entry : _outflow.values()) {
						drop = localInflow(entry);
						if (drop != null)
							entry.push(drop);
					}

					// Nx1
					for (Channel.Exit exit : _inflow.values()) {
						drop = exit.pull();
						if (drop != null)
							localOutflow(drop, exit);
					}

					// NxM (todo?)

					_cycle = 0;
				}
				Thread.sleep(getThreadSleepMillis());
			} catch (InterruptedException e) {
			}
		}

		LogUtil.log("- stopped cascade: " + getId());
		_done = true;
	}

}
