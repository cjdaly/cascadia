/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.Reddit.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Id;
import net.locosoft.cascadia.core.drop.Drop;
import net.locosoft.cascadia.core.drop.StringDrop;
import net.locosoft.cascadia.core.util.CoreUtil;
import net.locosoft.cascadia.core.util.ExecUtil;
import net.locosoft.cascadia.core.util.LogUtil;

public class RedditCascade extends Cascade {

	private String _subreddit = "news";
	private LinkedList<RedditPost> _posts = new LinkedList<RedditPost>();
	private RedditPost _post;
	private int _waitCycle = 2;

	public RedditCascade(Conflux conflux) {
		super("RedditCascade", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 60 * 1;
	}

	protected void cycleBegin() throws Exception {
		if (_posts.isEmpty()) {
			if (_waitCycle <= 0) {
				for (RedditPost post : readLatestPosts()) {
					_posts.add(post);
				}
				_waitCycle = 5;
			} else {
				_waitCycle--;
			}
		} else {
			_post = _posts.removeLast();
		}
	}

	private RedditPost[] readLatestPosts() {

		RedditPost[] none = new RedditPost[0];

		String clientId = getConfigLocal("client_id", "");
		if ("".equals(clientId))
			return none;

		String clientSecret = getConfigLocal("client_secret", "");
		if ("".equals(clientSecret))
			return none;

		String username = getConfigLocal("username", "");
		if ("".equals(username))
			return none;

		String scriptPath = getConfluxPath() + "/test.py";
		String command = "python3 " + scriptPath + " " + //
				clientId + " " + clientSecret + " " + //
				getConfigGlobal("cascadia.thing.type", "unknown") + " " + //
				CoreUtil.getInternalVersion() + " " + //
				username;

		LogUtil.log(this, "EXEC: " + command);

		StringBuilder output = new StringBuilder();
		ExecUtil.execCommand(command, output, null);

		BufferedReader reader = new BufferedReader(new StringReader(output.toString()));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				LogUtil.log(this, line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return none;
	}

	protected Drop spill(Id context) throws Exception {
		if (_post != null) {
			switch (context.getId()) {
			case "postTitle":
				return new StringDrop(_post.Title);
			case "postAuthor":
				return new StringDrop(_post.AuthorName);
			case "postSub":
				return new StringDrop(_subreddit);
			}
		}
		return null;
	}

	protected void cycleEnd() throws Exception {
		_post = null;
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { "postTitle", "postAuthor", "postSub" };
	}

}
