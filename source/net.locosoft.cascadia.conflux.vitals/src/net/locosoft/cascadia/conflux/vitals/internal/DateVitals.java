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
import net.locosoft.cascadia.core.drop.IntDrop;
import net.locosoft.cascadia.core.drop.StringDrop;

public class DateVitals extends Cascade {

	public DateVitals(Conflux conflux) {
		super("dateVitals", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 30;
	}

	private Date _date;

	protected void cycleBegin() {
		_date = new Date();
	}

	@SuppressWarnings("deprecation")
	protected Drop localInflow(Id context) {
		switch (context.getId()) {
		case "year":
			return new IntDrop(_date.getYear() + 1900);
		case "month":
			return new IntDrop(_date.getMonth() + 1);
		case "monthName":
			return new StringDrop(_monthNames[_date.getMonth()]);
		case "dayOfMonth":
			return new IntDrop(_date.getDate());
		case "dayOfWeek":
			return new IntDrop(_date.getDay());
		case "dayOfWeekName":
			return new StringDrop(_dayNames[_date.getDay()]);
		default:
			return null;
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "year", "month", "monthName", "dayOfMonth", "dayOfWeek", "dayOfWeekName" };
	}

	private static String[] _monthNames = { //
			"January", //
			"February", //
			"March", //
			"April", //
			"May", //
			"June", //
			"July", //
			"August", //
			"September", //
			"October", //
			"November", //
			"December" //
	};

	private static String[] _dayNames = { //
			"Sunday", //
			"Monday", //
			"Tuesday", //
			"Wednesday", //
			"Thursday", //
			"Friday", //
			"Saturday" //
	};

}
