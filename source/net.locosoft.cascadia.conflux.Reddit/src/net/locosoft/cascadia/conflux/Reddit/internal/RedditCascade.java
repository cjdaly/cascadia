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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final Pattern _redditTidbit = Pattern.compile("([0-9]+)/([^:]+):(.*)");

	public RedditCascade(Conflux conflux) {
		super("RedditCascade", conflux);
	}

	protected long getCycleSleepMillis() {
		return 1000 * 60 * 1;
	}

	protected void cycleBegin() throws Exception {
		if (_posts.isEmpty()) {
			if (_waitCycle <= 0) {
				readLatestPosts();
				_waitCycle = 5;
			} else {
				_waitCycle--;
			}
		} else {
			_post = _posts.removeLast();
		}
	}

	private void readLatestPosts() {

		String clientId = getConfigLocal("client_id", "");
		if ("".equals(clientId))
			return;

		String clientSecret = getConfigLocal("client_secret", "");
		if ("".equals(clientSecret))
			return;

		String username = getConfigLocal("username", "");
		if ("".equals(username))
			return;

		String scriptPath = getConfluxPath() + "/test.py";
		String command = "python3 " + scriptPath + " " + //
				clientId + " " + clientSecret + " " + //
				getConfigGlobal("cascadia.thing.type", "unknown") + " " + //
				CoreUtil.getInternalVersion() + " " + //
				username;

		StringBuilder output = new StringBuilder();
		ExecUtil.execCommand(command, output, null);

		BufferedReader reader = new BufferedReader(new StringReader(output.toString()));
		String line;
		String title = null;
		String authorName = null;
		try {
			while ((line = reader.readLine()) != null) {

				Matcher lineMatcher = _redditTidbit.matcher(line);
				if (lineMatcher.find()) {
					String itemNum = lineMatcher.group(1);
					String itemKey = lineMatcher.group(2);
					String itemVal = lineMatcher.group(3);

					switch (itemKey) {
					case "title":
						title = itemVal;
						break;
					case "authorName":
						authorName = itemVal;
						break;
					case "END":
						if ((title != null) && (authorName != null)) {
							_posts.addFirst(new RedditPost(title, authorName, _subreddit));
							if (LogUtil.isEnabled(this)) {
								LogUtil.log(this, "Post (" + itemNum + ") by " + authorName + ": " + title);
							}
						}
						title = null;
						authorName = null;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
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
