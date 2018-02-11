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

import java.util.HashSet;
import java.util.Set;

public class Poem {

	private String _poemTitle;
	private String _poemUrl;
	private String _authorName;
	private String _authorUrl;
	private String[] _poemBodyLines;
	private int _lineIndex = 0;

	public Poem(String poemTitle, String poemUrl, String authorName, String authorUrl, String[] poemBodyLines) {
		_poemTitle = poemTitle;
		_poemUrl = poemUrl;
		_authorName = authorName;
		_authorUrl = authorUrl;
		_poemBodyLines = poemBodyLines;
	}

	public String getTitle() {
		return _poemTitle;
	}

	public String getUrl() {
		return _poemUrl;
	}

	public String getAuthorName() {
		return _authorName;
	}

	public String getAuthorUrl() {
		return _authorUrl;
	}

	public String getNextLine() {
		if (_lineIndex >= _poemBodyLines.length)
			return null;
		else
			return _poemBodyLines[_lineIndex++];
	}

	public int getTotalLineCount() {
		return _poemBodyLines.length;
	}

	public int getLineCount() {
		int lineCount = 0;
		for (String line : _poemBodyLines) {
			if ((line != null) && (!"".equals(line.trim()))) {
				lineCount++;
			}
		}
		return lineCount;
	}

	public int getMaxLineLength() {
		int maxLineLength = _poemTitle.length();
		for (String line : _poemBodyLines) {
			if (line != null) {
				if (line.length() > maxLineLength)
					maxLineLength = line.length();
			}
		}
		return maxLineLength;
	}

	public char[] getAllNonAsciiChars() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < _poemTitle.length(); i++) {
			char c = _poemTitle.charAt(i);
			if (!isAllowedAsciiChar(c)) {
				sb.append(c);
			}
		}
		for (String line : _poemBodyLines) {
			if (line != null) {
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					if (!isAllowedAsciiChar(c)) {
						sb.append(c);
					}
				}
			}
		}
		return sb.toString().toCharArray();
	}

	public char[] getUniqueNonAsciiChars() {
		Set<Character> badChars = new HashSet<Character>();
		for (int i = 0; i < _poemTitle.length(); i++) {
			char c = _poemTitle.charAt(i);
			if (!isAllowedAsciiChar(c)) {
				badChars.add(c);
			}
		}
		for (String line : _poemBodyLines) {
			if (line != null) {
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					if (!isAllowedAsciiChar(c)) {
						badChars.add(c);
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		for (Character c : badChars) {
			sb.append(c);
		}
		return sb.toString().toCharArray();
	}

	public boolean isAllowedAsciiChar(char c) {
		if (c == 9)
			return true;

		if ((c >= 32) && (c <= 126))
			return true;

		return false;
	}

}
