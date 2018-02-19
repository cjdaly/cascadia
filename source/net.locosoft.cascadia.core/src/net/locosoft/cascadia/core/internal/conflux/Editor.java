/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core.internal.conflux;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;

public class Editor extends Cascade {

	private String _edit;

	public Editor(Conflux conflux) {
		super("editor", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 20;
	}

	protected String[] registerInflowChannelIds() {
		return new String[] { "fromJournal" };
	}

	protected void fill(Drop drop, Id context) throws Exception {
		switch (context.getId()) {
		case "fromJournal":
			String reflection = drop.asString();
			String markup = _Edits[random(_Edits.length)];
			_edit = markup + ": " + reflection;
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "toJournal" };
	}

	protected Drop spill(Id context) throws Exception {
		switch (context.getId()) {
		case "toJournal":
			if (_edit != null) {
				StringDrop edit = new StringDrop(_edit);
				_edit = null;
				return edit;
			}
		}
		return null;
	}

	private final String[] _Edits = { "hed", "dek", "lede", "graf", "???" };

}
