/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExecUtil {

	public static int execCommand(String command, StringBuilder processOut, StringBuilder processErr) {
		int status = -1;
		if (processOut == null)
			processOut = new StringBuilder();
		if (processErr == null) {
			processErr = new StringBuilder();
		}

		try {
			Process process = Runtime.getRuntime().exec(command);

			ProcessStreamReader outReader = new ProcessStreamReader(process.getInputStream(), processOut);
			ProcessStreamReader errReader = new ProcessStreamReader(process.getErrorStream(), processErr);

			outReader.start();
			errReader.start();

			status = process.waitFor();

			outReader.join();
			errReader.join();

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return status;
	}

	public static class ProcessStreamReader extends Thread {
		private InputStream _inputStream;
		private StringBuilder _outputBuffer;

		ProcessStreamReader(InputStream inputStream, StringBuilder outputBuffer) {
			_inputStream = inputStream;
			_outputBuffer = outputBuffer;
		}

		public void run() {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(_inputStream))) {

				char[] buffer = new char[1024];

				int bytesRead;
				while ((bytesRead = reader.read(buffer)) != -1) {
					_outputBuffer.append(buffer, 0, bytesRead);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
