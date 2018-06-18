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

public class Token {

	public static Token None = new Token("?_?", TokenType._None);

	public final String _Text;
	public final TokenType _Type;

	public Token(String text, TokenType type) {
		_Text = text;
		_Type = type;
	}

}
