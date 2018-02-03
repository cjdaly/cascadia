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

	private TreeMap<String, IExit> _inflow = new TreeMap<String, IExit>();
	private TreeMap<String, IEntry> _outflow = new TreeMap<String, IEntry>();

	public Cascade(String id) {
		super(id);
	}

	void start() {
		LogUtil.log("- starting cascade: " + getId());

		for (IExit exit : registerInflowExits()) {
			_inflow.put(exit.getId(), exit);
			LogUtil.log(" - inflow exit: " + exit.getId());
		}
		for (String id : registerInflowChannelIds()) {
			_inflow.put(id, new Channel(id).getExit());
			LogUtil.log(" - inflow channel: " + id);
		}
		for (IEntry entry : registerOutflowEntries()) {
			_outflow.put(entry.getId(), entry);
			LogUtil.log(" - outflow entry: " + entry.getId());
		}
		for (String id : registerOutflowChannelIds()) {
			_outflow.put(id, new Channel(id).getEntry());
			LogUtil.log(" - outflow channel: " + id);
		}

		_thread.start();
	}

	void stop() {
		LogUtil.log("- stopping cascade: " + getId());
		_stop = true;

		int waitCycles = 6;
		while (!_done && waitCycles != 0) {
			try {
				waitCycles--;
				LogUtil.log("...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		if (waitCycles == 0) {
			LogUtil.log("- skipping cascade: " + getId());
		}
	}

	protected IExit[] registerInflowExits() {
		return new IExit[0];
	}

	protected String[] registerInflowChannelIds() {
		return new String[0];
	}

	protected IEntry[] registerOutflowEntries() {
		return new IEntry[0];
	}

	protected String[] registerOutflowChannelIds() {
		return new String[0];
	}

	protected boolean filter(IExit exit, IEntry entry, Drop drop) {
		return false;
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
					for (IExit exit : _inflow.values()) {
						for (IEntry entry : _outflow.values()) {
							Drop drop = exit.pullInflow(entry, this);
							if (drop != null) {
								if (!filter(exit, entry, drop)) {
									entry.pushOutflow(drop, exit, this);
								}
							}
						}
					}
					_cycle = 0;
				}
				Thread.sleep(getThreadSleepMillis());
			} catch (InterruptedException e) {
			}
			LogUtil.log("- stopped cascade: " + getId());
			_done = true;
		}
	}

}
