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

import net.locosoft.cascadia.core.Collector;
import net.locosoft.cascadia.core.Conflux;
import net.locosoft.cascadia.core.Connect;

public class RedditCollector extends Collector {

	public RedditCollector(Conflux conflux) {
		super("RedditCollector", conflux);
	}

	protected String[] registerInflowChannelQIds() {
		return new String[] { //
				"Reddit.RedditCascade.postTitle", //
				"Reddit.RedditCascade.postAuthor", //
				"Reddit.RedditCascade.postSub" //
		};
	}

	protected String[] registerOutflowChannelIds() {
		return new String[] { //
				"postTitle_TensorFlow", //
				"postAuthor_TensorFlow", //
				"postSub_TensorFlow" //
		};
	}

	protected Connect[] registerConnects() {
		return new Connect[] { //
				new Connect.Single("postTitle", "postTitle_TensorFlow"), //
				new Connect.Single("postAuthor", "postAuthor_TensorFlow"), //
				new Connect.Single("postSub", "postSub_TensorFlow") //
		};
	}
}
