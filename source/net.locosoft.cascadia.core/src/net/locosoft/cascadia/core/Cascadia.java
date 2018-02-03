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

import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import net.locosoft.cascadia.core.util.LogUtil;

public class Cascadia {

	private TreeMap<String, Conflux> _confluxMap = new TreeMap<String, Conflux>();

	public void start() {
		processExtensionRegistry();

		for (String id : _confluxMap.keySet()) {
			Conflux conflux = _confluxMap.get(id);
			LogUtil.log("Starting conflux: " + id);
			conflux.preInit();
			conflux.startCascades();
			conflux.postInit();
		}
	}

	private void processExtensionRegistry() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = extensionRegistry
				.getConfigurationElementsFor("net.locosoft.cascadia.core.Conflux");

		LogUtil.log("Registering confluxes: ", false);
		boolean first = true;
		for (IConfigurationElement configurationElement : configurationElements) {
			try {
				String id = configurationElement.getAttribute("id");
				Object extension = configurationElement.createExecutableExtension("implementation");
				Conflux conflux = (Conflux) extension;

				conflux.init(id);
				_confluxMap.put(id, conflux);
				if (first)
					first = false;
				else
					LogUtil.log(", ", false);
				LogUtil.log(id, false);
			} catch (ClassCastException | CoreException ex) {
				ex.printStackTrace();
			}
		}
		LogUtil.log(".");
	}

	public void stop() {
		for (String id : _confluxMap.keySet()) {
			Conflux conflux = _confluxMap.get(id);
			LogUtil.log("Stopping conflux: " + id);
			conflux.preFini();
			conflux.stopCascades();
			conflux.postFini();
		}
	}

}
