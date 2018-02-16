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
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.LongDrop;
import net.locosoft.cascadia.core.util.CoreUtil;
import net.locosoft.cascadia.core.util.ExecUtil;
import net.locosoft.cascadia.core.util.FileUtil;
import net.locosoft.cascadia.core.util.ParseUtil;

public abstract class ProcStatusVitals extends Cascade {

	private static final Pattern _VmPeakPattern = Pattern.compile("VmPeak:\\s+(\\d+)\\s+kB");
	private static final Pattern _VmSizePattern = Pattern.compile("VmSize:\\s+(\\d+)\\s+kB");

	private long _vmPeak = -1;
	private long _vmSize = -1;

	public ProcStatusVitals(String id, Conflux conflux) {
		super(id, conflux);
	}

	public abstract int getPID();

	protected long getCycleSleepMillis() {
		return 1000 * 60;
	}

	protected void cycleBegin() {
		int pid = getPID();
		if (pid == -1) {
			_vmPeak = -1;
			_vmSize = -1;
			return;
		}

		String procStatus = FileUtil.readFileToString("/proc/" + pid + "/status");

		Matcher matcher;
		matcher = _VmPeakPattern.matcher(procStatus);
		if (matcher.find()) {
			String vmPeakText = matcher.group(1);
			_vmPeak = ParseUtil.asLong(vmPeakText, -1);
		}

		matcher = _VmSizePattern.matcher(procStatus);
		if (matcher.find()) {
			String vmSizeText = matcher.group(1);
			_vmSize = ParseUtil.asLong(vmSizeText, -1);
		}
	}

	protected Drop localInflow(Id context) {
		switch (context.getId()) {
		case "VmPeak_kB":
			return new LongDrop(_vmPeak);
		case "VmSize_kB":
			return new LongDrop(_vmSize);
		default:
			return null;
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "VmPeak_kB", "VmSize_kB" };
	}

	//

	public static class Cascadia extends ProcStatusVitals {
		public Cascadia(Conflux conflux) {
			super("procStatus_Cascadia", conflux);
		}

		public int getPID() {
			return CoreUtil.getPID();
		}
	}

	public static class Neo4j extends ProcStatusVitals {

		private static final Pattern _Neo4jPidPattern = Pattern.compile("^neo4j\\s+(\\d+)\\s+", Pattern.MULTILINE);
		private int _pid = -1;

		public Neo4j(Conflux conflux) {
			super("procStatus_Neo4j", conflux);
		}

		public int getPID() {
			if (_pid == -1) {
				StringBuilder processOut = new StringBuilder();
				StringBuilder processErr = new StringBuilder();
				String psCommand = "ps -f -u neo4j";
				ExecUtil.execCommand(psCommand, processOut, processErr);

				Matcher matcher = _Neo4jPidPattern.matcher(processOut);
				if (matcher.find()) {
					String pidText = matcher.group(1);
					_pid = ParseUtil.asInt(pidText, -1);
				}
			}
			return _pid;
		}
	}

}
