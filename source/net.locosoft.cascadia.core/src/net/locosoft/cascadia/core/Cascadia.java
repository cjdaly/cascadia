/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import net.locosoft.cascadia.core.util.LogUtil;

public class Cascadia {

	private HashMap<String, Conflux> _confluxMap = new HashMap<String, Conflux>();

	public void start() {
		processExtensionRegistry();
	}

	private void processExtensionRegistry() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = extensionRegistry
				.getConfigurationElementsFor("net.locosoft.cascadia.core.Conflux");
		for (IConfigurationElement configurationElement : configurationElements) {
			try {
				String id = configurationElement.getAttribute("id");
				Object extension = configurationElement.createExecutableExtension("implementation");
				Conflux conflux = (Conflux) extension;
				conflux.init(id);
				_confluxMap.put(id, conflux);

				LogUtil.log("Registered conflux: " + id);
			} catch (ClassCastException | CoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void stop() {

	}

}
