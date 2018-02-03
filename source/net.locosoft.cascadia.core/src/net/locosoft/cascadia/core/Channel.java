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

public final class Channel extends Id {

	Channel(String id) {
		super(id);
	}

	private int _size = 16;
	private LinkedList<Drop> _drops = new LinkedList<Drop>();
	private Entry _entry = new Entry();
	private Exit _exit = new Exit();

	public Entry getEntry() {
		return _entry;
	}

	public Exit getExit() {
		return _exit;
	}

	public final class Entry implements IEntry {

		public String getId() {
			return Channel.this.getId();
		}

		public synchronized void pushOutflow(Drop drop, IId exitId, IId cascadeId) {
			if (drop == null)
				return;

			_drops.addFirst(drop);
			while (_drops.size() > _size) {
				_drops.removeLast();
			}
		}

	}

	public final class Exit implements IExit {

		public String getId() {
			return Channel.this.getId();
		}

		public synchronized Drop pullInflow(IId entryId, IId cascadeId) {
			if (_drops.isEmpty())
				return null;
			else
				return _drops.removeLast();
		}

	}
}
