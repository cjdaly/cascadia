/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core.internal.conflux.journal;

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

	public Journal(Conflux conflux) {
		super("Journal", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 30;
	}

	protected void init() {
		_thingName = getCascadiaId("thing.name");
		_thingType = getCascadiaId("thing.type");
	}

	protected void cycleBegin() throws Exception {
		if (random(10) == 3) {
			String reflection = _ROM[random(_ROM.length)];
			String name = getConfig(_thingName, "?");
			reflection = reflection.replace("{NAME}", name);
			String type = getConfig(_thingType, "?");
			reflection = reflection.replace("{TYPE}", type);
			_reflection = reflection;
		} else {
			_reflection = null;
		}
	}

	protected Drop localInflow(Id context) throws Exception {
		if (_reflection != null) {
			if (thisId(context)) {
				LogUtil.log(this, _reflection);
			} else {
				switch (context.getId()) {
				case "reflections":
					return new StringDrop(_reflection);
				}
			}
		}
		return null;
	}

	protected String[] registerInflowChannelIds() {
		return new String[] { "reflections" };
	}

	private static final String[] _ROM = { //
			"My name is {NAME}.", //
			"I am a {TYPE}.", //
			"All work and no play makes {NAME} a dull bot.", //
			"My name is {NAME}, and I care what you think.", //
			"..." //
	};
}
