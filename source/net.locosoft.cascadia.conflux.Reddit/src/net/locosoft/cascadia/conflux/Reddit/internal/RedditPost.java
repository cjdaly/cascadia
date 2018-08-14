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

public class RedditPost {

	public final String Title;
	public final String AuthorName;
	public final String SubReddit;

	public RedditPost(String title, String authorName, String subReddit) {
		Title = title;
		AuthorName = authorName;
		SubReddit = subReddit;
	}

}
