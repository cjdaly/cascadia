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

import java.util.LinkedList;

import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.util.LogUtil;

public final class Channel extends Id {

	Channel(String id, Cascade cascade, boolean isOutflow) {
		super(id, cascade);
		_isOutflow = isOutflow;
		if (isOutflow)
			_entry = new Entry(this);
		else
			_exit = new Exit(this);
	}

	private final boolean _isOutflow;
	private int _size = 16;
	private LinkedList<Drop> _drops = new LinkedList<Drop>();
	private Entry _entry;
	private Exit _exit;

	boolean isOutflow() {
		return _isOutflow;
	}

	boolean isInflow() {
		return !_isOutflow;
	}

	boolean isConnected() {
		return (_entry != null) && (_exit != null);
	}

	Entry getEntry() {
		return _entry;
	}

	Exit getExit() {
		return _exit;
	}

	synchronized boolean extend(Id context) {
		if (context != null) {
			if (_isOutflow) {
				if (_exit == null) {
					_exit = new Exit(context);
					return true;
				}
			} else {
				if (_entry == null) {
					_entry = new Entry(context);
					return true;
				}
			}
		}
		return false;
	}

	synchronized void push(Drop drop) {
		if (drop == null)
			return;

		if (LogUtil.isEnabled(this)) {
			LogUtil.log(this, "~> " + drop.toString());
		}
		_drops.addFirst(drop);
		while (_drops.size() > _size) {
			Drop spill = _drops.removeLast();
			if (LogUtil.isEnabled(this)) {
				LogUtil.log(this, "Spill! " + spill.toString() + " ~> 0");
			}
		}
	}

	final class Entry extends Id {

		Entry(Id qualifier) {
			super("_entry", qualifier);
		}

		Channel getChannel() {
			return Channel.this;
		}

		void push(Drop drop) {
			Channel.this.push(drop);
		}
	}

	synchronized Drop pull() {
		if (_drops.isEmpty())
			return null;
		else {
			Drop drop = _drops.removeLast();
			if (LogUtil.isEnabled(this)) {
				LogUtil.log(this, drop.toString() + " ~>");
			}
			return drop;
		}
	}

	final class Exit extends Id {

		Exit(Id qualifier) {
			super("_exit", qualifier);
		}

		Channel getChannel() {
			return Channel.this;
		}

		Drop pull() {
			return Channel.this.pull();
		}
	}
}
