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

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class PoemReader {

	private static final String _AllPoetry_URL = "http://allpoetry.com/poems";

	public Poem[] readLatestPoems() {
		ArrayList<Poem> poems = new ArrayList<Poem>();

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			CloseableHttpResponse response = httpClient.execute(new HttpGet(_AllPoetry_URL));
			String bodyText = EntityUtils.toString(response.getEntity());

			if (bodyText != null) {
				Matcher matcher = _PoemPattern.matcher(bodyText);
				while (matcher.find()) {
					String authorName = matcher.group(1);
					String authorUrl = matcher.group(2);
					String poemMetadata = matcher.group(3);
					String poemBodyRaw = matcher.group(4);
					Poem poem = constructPoem(authorName, authorUrl, poemMetadata, poemBodyRaw);
					if ((poem != null) && (sanityCheckPoem(poem))) {
						poems.add(poem);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return poems.toArray(new Poem[0]);
	}

	private static final Pattern _PoemPattern = Pattern.compile(
			"<a class=[\"']u[\"'] data-name=[\"'](.*?)[\"'] href=[\"']/(.*?)[\"']><img(.*?)<div class=[\"']preview poem_body[\"']>(.*?)<div class=[\"']copyright[\"']>",
			Pattern.DOTALL);

	private static final Pattern _MetadataPattern = Pattern.compile(
			"<h1 class=[\"']title vcard item[\"'][^>]*><a class=[^>]+href=\"([^\"]*)\">([^<]+)</a>", Pattern.DOTALL);

	private Poem constructPoem(String authorName, String authorUrl, String poemMetadata, String poemBodyRaw) {
		String poemBodyFix = poemBodyRaw.replaceAll("<[^<>]+>", "");
		poemBodyFix = scrubString(poemBodyFix);
		String[] poemBodyLines = poemBodyFix.split("\\r?\\n");

		Matcher matcher = _MetadataPattern.matcher(poemMetadata);
		if (matcher.find()) {
			String poemUrl = matcher.group(1);
			String poemTitle = matcher.group(2);
			poemTitle = scrubString(poemTitle);
			return new Poem(poemTitle, poemUrl, authorName, authorUrl, poemBodyLines);
		}

		return null;
	}

	private String scrubString(String inputText) {
		String scrubText = inputText;
		scrubText = scrubText.replace("&nbsp;", " ");
		scrubText = scrubText.replace("&quot;", "\"");
		scrubText = scrubText.replace("&amp;", "&");
		scrubText = scrubText.replace("&lt;", "<");
		scrubText = scrubText.replace("&gt;", ">");
		scrubText = scrubText.replace("&mdash;", "-");
		scrubText = scrubText.replace((char) 160, ' ');
		scrubText = scrubText.replace((char) 8211, '-');
		scrubText = scrubText.replace((char) 8212, '-');
		scrubText = scrubText.replace((char) 8213, '-');
		scrubText = scrubText.replace((char) 8216, '`');
		scrubText = scrubText.replace((char) 8217, '\'');
		scrubText = scrubText.replace((char) 8220, '"');
		scrubText = scrubText.replace((char) 8221, '"');
		scrubText = scrubText.replace("" + (char) 8230, "...");
		return scrubText;
	}

	private boolean sanityCheckPoem(Poem poem) {
		boolean sanityCheck = true;

		int lineCount = poem.getLineCount();
		int lineCountMin = 2;
		int lineCountMax = 99;
		if ((lineCount < lineCountMin) || (lineCount > lineCountMax)) {
			sanityCheck = false;
		}

		int maxLineLength = poem.getMaxLineLength();
		int maxLineLengthMin = 16;
		int maxLineLengthMax = 99;
		if ((maxLineLength < maxLineLengthMin) || (maxLineLength > maxLineLengthMax)) {
			sanityCheck = false;
		}

		char[] nonAsciiChars = poem.getAllNonAsciiChars();
		if (nonAsciiChars.length > 0) {
			sanityCheck = false;
		}

		return sanityCheck;
	}

}
