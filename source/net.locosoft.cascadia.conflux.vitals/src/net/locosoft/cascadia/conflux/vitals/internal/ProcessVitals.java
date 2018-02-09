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
import net.locosoft.cascadia.core.util.ParseUtil;

public class ProcessVitals extends Cascade {

	public ProcessVitals(Conflux conflux) {
		super("processVitals", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 15;
	}

	protected Drop localInflow(Id context) {
		int pid;
		int vmPeak;
		switch (context.getId()) {
		case "cascadia_vmPeak_kB":
			pid = CoreUtil.getPID();
			vmPeak = ExecUtil.getProcessVmPeak(pid);
			return new LongDrop(vmPeak);
		case "neo4j_vmPeak_kB":
			pid = getNeo4jPID();
			vmPeak = ExecUtil.getProcessVmPeak(pid);
			return new LongDrop(vmPeak);
		default:
			return null;
		}
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "cascadia_vmPeak_kB", "neo4j_vmPeak_kB" };
	}

	// Neo4j process

	private static final Pattern _Neo4jPidPattern = Pattern.compile("^neo4j\\s+(\\d+)\\s+", Pattern.MULTILINE);

	private int getNeo4jPID() {
		StringBuilder processOut = new StringBuilder();
		StringBuilder processErr = new StringBuilder();
		String psCommand = "ps -f -u neo4j";
		ExecUtil.execCommand(psCommand, processOut, processErr);

		Matcher matcher = _Neo4jPidPattern.matcher(processOut);
		if (matcher.find()) {
			String pidText = matcher.group(1);
			return ParseUtil.asInt(pidText, -1);
		}

		return -1;
	}

}
