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

public class ProtoTokenizer extends Tokenizer {

	public ProtoTokenizer() {
		addPattern(Space);
		addPattern(Word);
		addPattern(Number);

		addSingle('!', true, "bang", "symbol", "proto"); // 33
		addSingle('"', false, "dquote", "double", "quote", "symbol", "proto"); // 34
		addSingle('#', true, "hash", "symbol", "proto"); // 35
		addSingle('$', true, "dollar", "symbol", "proto"); // 36
		addSingle('%', true, "percent", "symbol", "proto"); // 37
		addSingle('&', true, "amp", "symbol", "proto"); // 38
		addSingle('\'', false, "quote", "single", "quote", "symbol", "proto"); // 39
		addSingle('(', false, "lparen", "left", "paren", "symbol", "proto"); // 40
		addSingle(')', false, "rparen", "right", "paren", "symbol", "proto"); // 41
		addSingle('*', true, "asterisk", "star", "symbol", "proto"); // 42
		addSingle('+', true, "plus", "symbol", "proto"); // 43
		addSingle(',', false, "comma", "symbol", "proto"); // 44
		addSingle('-', true, "minus", "hyphen", "symbol", "proto"); // 45
		addSingle('.', true, "period", "dot", "symbol", "proto"); // 46
		addSingle('/', true, "slash", "forward", "symbol", "proto"); // 47

		addSingle(':', true, "colon", "symbol", "proto"); // 58
		addSingle(';', true, "semi", "semicolon", "symbol", "proto"); // 59
		addSingle('<', true, "lt", "left", "symbol", "proto"); // 60
		addSingle('=', true, "eq", "equals", "symbol", "proto"); // 61
		addSingle('>', true, "gt", "right", "symbol", "proto"); // 62
		addSingle('?', true, "qmark", "question", "symbol", "proto"); // 63
		addSingle('@', true, "at", "symbol", "proto"); // 64

		addSingle('[', false, "lsquare", "left", "square", "bracket", "symbol", "proto"); // 91
		addSingle('\\', true, "backslash", "backward", "slash", "symbol", "proto"); // 92
		addSingle(']', false, "rsquare", "right", "square", "bracket", "symbol", "proto"); // 93
		addSingle('^', true, "caret", "symbol", "proto"); // 94
		addSingle('_', true, "underscore", "symbol", "proto"); // 95
		addSingle('`', false, "backtick", "backquote", "symbol", "proto"); // 96

		addSingle('{', false, "lcurly", "left", "curly", "bracket", "symbol", "proto"); // 123
		addSingle('|', false, "pipe", "symbol", "proto"); // 124
		addSingle('}', false, "rcurly", "right", "curly", "bracket", "symbol", "proto"); // 125
		addSingle('~', true, "tilde", "symbol", "proto"); // 126
	}

	private void addSingle(char c, boolean repeats, String typeId, String... typeTags) {
		addPattern(new TokenPattern.Single(c, repeats, typeId, typeTags));
	}

	//

	public static TokenPattern Space = new TokenPattern() {

		public final TokenType _Type = new TokenType("space", "proto");

		public char minValidInitialChar() {
			return 9;
		}

		public char maxValidInitialChar() {
			return 32;
		}

		public boolean isValidInitialChar(char c) {
			switch (c) {
			case 9: // tab
			case 10: // line feed
			case 11: // vertical tab
			case 12: // form feed
			case 13: // carriage return
			case 32: // space
				return true;
			default:
				return false;
			}
		}

		public TokenType getTokenType() {
			return _Type;
		}

	};

	public static TokenPattern Word = new TokenPattern() {

		public final TokenType _Type = new TokenType("word", "proto");

		public char minValidInitialChar() {
			return 65;
		}

		public char maxValidInitialChar() {
			return 122;
		}

		public boolean isValidInitialChar(char c) {
			return (((c >= 65) && (c <= 90)) || ((c >= 97) && (c <= 122)));
		}

		public TokenType getTokenType() {
			return _Type;
		}

	};

	public static TokenPattern Number = new TokenPattern() {

		public final TokenType _Type = new TokenType("number", "proto");

		public char minValidInitialChar() {
			return 48;
		}

		public char maxValidInitialChar() {
			return 57;
		}

		public boolean isValidInitialChar(char c) {
			return ((c >= 48) && (c <= 57));
		}

		public TokenType getTokenType() {
			return _Type;
		}

	};

}
