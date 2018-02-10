/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.AllPoetry.internal;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.BooleanDrop;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.util.LogUtil;

public class AllPoetryReader extends Cascade {

	private PoemReader _poemReader;

	public AllPoetryReader(Conflux conflux) {
		super("AllPoetryReader", conflux);
	}

	protected void init() {
		_poemReader = new PoemReader();
	}

	protected long getCycleSleepMillis() {
		return 1000 * 60 * 5;
	}

	protected Drop localInflow(Id context) {
		if (thisId(context))
			return new BooleanDrop(true);
		else
			return null;
	}

	protected void localOutflow(Drop drop, Id context) {
		if (thisId(context)) {
			Poem[] poems = _poemReader.readLatestPoems();
			for (Poem poem : poems) {
				LogUtil.log("AllPoetry - got poem: " + poem.getTitle() + ", by: " + poem.getAuthorName());
			}
		}
	}

}
