/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core.drop.token;

import java.util.HashSet;

public class TokenType {

	private final String _id;
	private final String[] _tags;
	private final HashSet<String> _tagSet = new HashSet<String>();

	public TokenType(String id, String... tags) {
		_id = id;
		_tags = tags;
		for (String tag : tags) {
			if (tag != null) {
				_tagSet.add(tag);
			}
		}
	}

	public String getId() {
		return _id;
	}

	public String[] getTags() {
		return _tags;
	}

	public boolean hasTag(String tag) {
		return _tagSet.contains(tag);
	}

	//

	public static final TokenType _None = new TokenType("none");

}
