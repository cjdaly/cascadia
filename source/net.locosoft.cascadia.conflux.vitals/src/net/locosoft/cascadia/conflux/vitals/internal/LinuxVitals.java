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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.DoubleDrop;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.FloatDrop;
import net.locosoft.cascadia.core.drop.IntDrop;
import net.locosoft.cascadia.core.drop.LongDrop;
import net.locosoft.cascadia.core.util.CoreUtil;
import net.locosoft.cascadia.core.util.ExecUtil;
import net.locosoft.cascadia.core.util.FileUtil;
import net.locosoft.cascadia.core.util.ParseUtil;

public class LinuxVitals extends Cascade {

	public LinuxVitals(Conflux conflux) {
		super("linuxVitals", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 30;
	}

	private double _cpuTemp;
	private long _memFree_kB;
	private int _diskUsePercent;
	private float _loadAvg1Min;
	private float _loadAvg5Min;
	private float _loadAvg15Min;

	private Pattern _memFreePattern = Pattern.compile("MemFree:\\s+(\\d+)\\s+kB");
	private Pattern _dfPattern = Pattern.compile("/dev/\\S+\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)%");

	protected void cycleBegin() {
		Matcher matcher;

		// cpuTemp
		String cpuTemp = FileUtil.readFileToString("/sys/class/thermal/thermal_zone0/temp");
		int cpuTemp1000 = ParseUtil.asInt(cpuTemp.trim(), -1);
		_cpuTemp = cpuTemp1000 == -1 ? -1 : (double) cpuTemp1000 / 1000.0;

		// memInfo
		String procMemInfo = FileUtil.readFileToString("/proc/meminfo");
		matcher = _memFreePattern.matcher(procMemInfo);
		_memFree_kB = -1;
		if (matcher.find()) {
			String memFreeText = matcher.group(1);
			_memFree_kB = ParseUtil.asLong(memFreeText, -1);
		}

		// diskUsePercent
		StringBuilder processOut = new StringBuilder();
		StringBuilder processErr = new StringBuilder();
		String dfCommand = "/bin/df -k " + CoreUtil.getHomeDir();
		ExecUtil.execCommand(dfCommand, processOut, processErr);
		String diskUsePercent = processOut.toString();
		matcher = _dfPattern.matcher(diskUsePercent);
		_diskUsePercent = -1;
		if (matcher.find()) {
			String dfUsePercentText = matcher.group(4);
			_diskUsePercent = ParseUtil.asInt(dfUsePercentText, -1);
		}

		// load averages
		String procLoadAvg = FileUtil.readFileToString("/proc/loadavg");
		String[] loadAvgs = procLoadAvg.split("\\s+");
		if ((loadAvgs != null) && (loadAvgs.length > 0)) {
			_loadAvg1Min = ParseUtil.asFloat(loadAvgs[0], -1);
			_loadAvg5Min = ParseUtil.asFloat(loadAvgs[1], -1);
			_loadAvg15Min = ParseUtil.asFloat(loadAvgs[2], -1);
		} else {
			_loadAvg1Min = -1;
			_loadAvg5Min = -1;
			_loadAvg15Min = -1;
		}
	}

	protected Drop spill(Id context) {
		switch (context.getId()) {
		case "cpuTemp":
			return new DoubleDrop(_cpuTemp);
		case "memFree_kB":
			return new LongDrop(_memFree_kB);
		case "diskUsePercent":
			return new IntDrop(_diskUsePercent);
		case "loadAvg1Min":
			return new FloatDrop(_loadAvg1Min);
		case "loadAvg5Min":
			return new FloatDrop(_loadAvg5Min);
		case "loadAvg15Min":
			return new FloatDrop(_loadAvg15Min);
		default:
			return null;
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "cpuTemp", "memFree_kB", "diskUsePercent", "loadAvg1Min", "loadAvg5Min", "loadAvg15Min" };
	}

}
