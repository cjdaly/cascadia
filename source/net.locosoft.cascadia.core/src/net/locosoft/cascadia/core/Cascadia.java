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

import java.util.Properties;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import net.locosoft.cascadia.core.util.CoreUtil;
import net.locosoft.cascadia.core.util.FileUtil;
import net.locosoft.cascadia.core.util.LogUtil;

public final class Cascadia {

	private Properties _config;
	private TreeMap<String, Conflux> _confluxMap = new TreeMap<String, Conflux>();

	public String getConfig(Id id, String default_) {
		return getConfig(id.getQId(), default_);
	}

	public String getConfig(String key, String default_) {
		if (_config == null) {
			String configFilePath = CoreUtil.getConfigDir() + "/config.properties";
			_config = FileUtil.loadPropertiesFile(configFilePath);
		}
		return _config.getProperty(key, default_);
	}

	public void start() {

		String thingName = getConfig(Id._Cascadia_Thing_Name, "thing1");
		String thingType = getConfig(Id._Cascadia_Thing_Type, "rock64");
		LogUtil.log("Cascadia Thing name: " + thingName + ", type: " + thingType);

		processExtensionRegistry();

		for (String id : _confluxMap.keySet()) {
			Conflux conflux = _confluxMap.get(id);
			LogUtil.log("Starting conflux: " + id);
			conflux.init();
			conflux.startCascades();
		}
	}

	private void processExtensionRegistry() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = extensionRegistry
				.getConfigurationElementsFor("net.locosoft.cascadia.core.conflux");

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
			conflux.stopCascades();
			conflux.fini();
		}
	}

}
