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
import net.locosoft.cascadia.core.util.LogUtil;

public class Journal extends Cascade {

	private Id _thingName;
	private Id _thingType;
	private String _reflection;
	private String _edit;

	public Journal(Conflux conflux) {
		super("journal", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 60;
	}

	protected void init() {
		_thingName = getCascadiaId("thing.name");
		_thingType = getCascadiaId("thing.type");
	}

	protected void cycleBegin() throws Exception {
		if (random(10) == 3) {
			String reflection = _ROM[random(_ROM.length)];
			reflection = reflection.replace("{NAME}", getConfig(_thingName, "?"));
			reflection = reflection.replace("{TYPE}", getConfig(_thingType, "?"));
			_reflection = reflection;
		}
	}

	protected void cycleEnd() throws Exception {
		_reflection = null;
		_edit = null;
	}

	protected String[] registerInflowChannelIds() {
		return new String[] { "fromEditor" };
	}

	protected void fill(Drop drop, Id context) throws Exception {
		switch (context.getId()) {
		case "fromEditor":
			_edit = drop.asString();
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "reflections", "toEditor" };
	}

	protected Drop spill(Id context) throws Exception {
		if (_edit != null) {
			if (thisId(context)) {
				LogUtil.log(this, _edit);
			} else {
				switch (context.getId()) {
				case "reflections":
				case "toEditor":
					return new StringDrop(_edit);
				}
			}
		} else if (_reflection != null) {
			if (thisId(context)) {
				LogUtil.log(this, _reflection);
			} else {
				switch (context.getId()) {
				case "reflections":
				case "toEditor":
					return new StringDrop(_reflection);
				}
			}
		}
		return null;
	}

	private static final String[] _ROM = { //
			"My name is {NAME}.", //
			"I am a {TYPE}.", //
			"All work and no play makes {NAME} a dull bot.", //
			"My name is {NAME}, and I care what you think.", //
			"..." //
	};
}
