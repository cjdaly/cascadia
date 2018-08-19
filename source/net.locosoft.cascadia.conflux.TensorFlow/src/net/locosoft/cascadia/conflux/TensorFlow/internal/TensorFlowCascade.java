/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.TensorFlow.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;
import net.locosoft.cascadia.core.util.ExecUtil;

public class TensorFlowCascade extends Cascade {

	private String _tfVersion;
	private static final Pattern _tfTidbit = Pattern.compile("([0-9]+)/([^:]+):(.*)");

	public TensorFlowCascade(Conflux conflux) {
		super("TensorFlowCascade", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 60 * 1;
	}

	protected void cycleBegin() throws Exception {

		String scriptPath = getConfluxPath() + "/test.py";
		String command = "python3 " + scriptPath;
		StringBuilder output = new StringBuilder();
		ExecUtil.execCommand(command, output, null);

		BufferedReader reader = new BufferedReader(new StringReader(output.toString()));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				Matcher lineMatcher = _tfTidbit.matcher(line);
				if (lineMatcher.find()) {
					String itemKey = lineMatcher.group(2);
					String itemVal = lineMatcher.group(3);
					if ("tfVersion".equals(itemKey)) {
						_tfVersion = itemVal;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	protected Drop spill(Id context) throws Exception {
		if ("versionTest".equals(context.getId()) && (_tfVersion != null)) {
			return new StringDrop(_tfVersion);
		}

		return null;
	}

	protected void cycleEnd() throws Exception {
		_tfVersion = null;
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "versionTest" };
	}
}
