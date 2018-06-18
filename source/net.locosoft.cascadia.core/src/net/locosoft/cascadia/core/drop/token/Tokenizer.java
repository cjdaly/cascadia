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

import java.util.ArrayList;
import java.util.HashMap;

import net.locosoft.cascadia.core.util.LogUtil;

public class Tokenizer {

	private HashMap<Character, TokenPattern> _initialCharMap = new HashMap<Character, TokenPattern>();

	public void addPattern(TokenPattern pattern) {
		char cMin = pattern.minValidInitialChar();
		char cMax = pattern.maxValidInitialChar();
		for (char c = cMin; c <= cMax; c++) {
			if (pattern.isValidInitialChar(c)) {
				if (_initialCharMap.containsKey(c)) {
					LogUtil.log("Tokenizer has _initialCharMap conflicts!");
				}
				_initialCharMap.put(c, pattern);
			}
		}
	}

	public Token[] tokenize(String text) {
		ArrayList<Token> tokens = new ArrayList<Token>();
		Input input = new Input(text);
		Token token = nextToken(input);
		while (token != null) {
			tokens.add(token);
			token = nextToken(input);
		}
		return (Token[]) tokens.toArray(new Token[tokens.size()]);
	}

	private Token nextToken(Input input) {
		int ic = input.peekChar();
		if (ic == -1)
			return null;

		char c = (char) ic;
		TokenPattern pattern = _initialCharMap.get(c);
		if (pattern == null) {
			return consumeUnknownToken(input);
		} else {
			return pattern.nextToken(input);
		}
	}

	private Token consumeUnknownToken(Input input) {
		StringBuilder sb = new StringBuilder();
		sb.append(input.consumeChar());

		int ic = input.peekChar();
		while ((ic != -1) && (_initialCharMap.get((char) ic) == null)) {
			sb.append(input.consumeChar());
			ic = input.peekChar();
		}

		return new Token(sb.toString(), TokenType._None);
	}

	public class Input {
		private String _text;
		private int _pos = 0;

		Input(String text) {
			_text = text;
		}

		public int peekChar() {
			if (_pos < _text.length())
				return _text.charAt(_pos);
			else
				return -1;
		}

		public int consumeChar() {
			if (_pos < _text.length()) {
				_pos++;
				return _text.charAt(_pos - 1);
			} else
				return -1;
		}
	}

}
