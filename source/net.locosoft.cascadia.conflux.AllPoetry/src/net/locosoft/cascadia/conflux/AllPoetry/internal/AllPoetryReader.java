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

import java.util.LinkedList;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;
import net.locosoft.cascadia.core.util.LogUtil;

public class AllPoetryReader extends Cascade {

	private PoemReader _poemReader;
	private LinkedList<Poem> _poems = new LinkedList<Poem>();
	private boolean _newPoems = false;
	private Poem _poem;
	private String _poemLine;
	private int _waitCycle = 2;

	public AllPoetryReader(Conflux conflux) {
		super("AllPoetryReader", conflux);
	}

	protected void init() {
		_poemReader = new PoemReader();
	}

	protected long getCycleSleepMillis() {
		return 1000 * 60 * 1;
	}

	protected void cycleBegin() throws Exception {
		if (_poem == null) {
			if (_poems.isEmpty()) {
				if (_waitCycle <= 0) {
					for (Poem poem : _poemReader.readLatestPoems()) {
						_poems.addFirst(poem);
						_newPoems = true;
					}
				} else {
					_waitCycle--;
				}
			} else {
				_poem = _poems.removeLast();
			}
		} else {
			_poemLine = _poem.getNextLine();
			if (_poemLine == null) {
				_poem = null;
				if (_poems.isEmpty()) {
					_waitCycle = random(400) + 240;
					LogUtil.log(this, "waiting for " + _waitCycle + " cycles...");
				}
			}
		}
	}

	protected Drop spill(Id context) {
		if (thisId(context)) {
			if (_newPoems) {
				for (Poem poem : _poems) {
					LogUtil.log(this, "read poem: " + poem.getTitle() + ", by: " + poem.getAuthorName());
				}
				_newPoems = false;
			}
		} else if ((_poem != null) && (_poemLine != null)) {
			switch (context.getId()) {
			case "poemTitle":
				return new StringDrop(_poem.getTitle());
			case "poemAuthor":
				return new StringDrop(_poem.getAuthorName());
			case "poemLine":
				return new StringDrop(_poemLine);
			}
		}
		return null;
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "poemTitle", "poemAuthor", "poemLine" };
	}

}
