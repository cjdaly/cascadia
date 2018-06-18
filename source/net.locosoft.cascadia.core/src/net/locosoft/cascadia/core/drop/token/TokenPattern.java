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

import net.locosoft.cascadia.core.drop.token.Tokenizer.Input;

public abstract class TokenPattern {

	public abstract char minValidInitialChar();

	public abstract char maxValidInitialChar();

	public abstract boolean isValidInitialChar(char c);

	public abstract TokenType getTokenType();

	public boolean isValidChar(char c) {
		return isValidInitialChar(c);
	}

	public Token nextToken(Input input) {
		StringBuilder sb = new StringBuilder();
		sb.append(input.consumeChar());

		int ic = input.peekChar();
		while ((ic != -1) && isValidChar((char) ic)) {
			sb.append(input.consumeChar());
			ic = input.peekChar();
		}

		return new Token(sb.toString(), getTokenType());
	}

	public static class Single extends TokenPattern {

		private char _c;
		private boolean _repeats;
		private TokenType _type;

		public Single(char c, boolean repeats, String typeId, String... typeTags) {
			_type = new TokenType(typeId, typeTags);
			_c = c;
		}

		public char minValidInitialChar() {
			return _c;
		}

		public char maxValidInitialChar() {
			return _c;
		}

		public boolean isValidInitialChar(char c) {
			return c == _c;
		}

		public TokenType getTokenType() {
			return _type;
		}

		public Token nextToken(Input input) {
			StringBuilder sb = new StringBuilder();
			sb.append(input.consumeChar());

			if (_repeats) {
				int ic = input.peekChar();
				while ((ic != -1) && isValidChar((char) ic)) {
					sb.append(input.consumeChar());
					ic = input.peekChar();
				}
			}

			return new Token(sb.toString(), getTokenType());
		}

	}

}
