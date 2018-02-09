/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.vitals.internal;

import java.util.Date;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.LongDrop;

public class TimeVitals extends Cascade {

	public TimeVitals(Conflux conflux) {
		super("timeVitals", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 10;
	}

	private Date _date;

	protected void cycleBegin() {
		_date = new Date();
	}

	@SuppressWarnings("deprecation")
	protected Drop localInflow(Id context) {
		switch (context.getId()) {
		case "hour":
			return new LongDrop(_date.getHours());
		case "minute":
			return new LongDrop(_date.getMinutes());
		case "second":
			return new LongDrop(_date.getSeconds());
		default:
			return null;
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "hour", "minute", "second" };
	}

}
