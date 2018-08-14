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

import java.io.File;
import java.util.Properties;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import net.locosoft.cascadia.core.util.CoreUtil;
import net.locosoft.cascadia.core.util.FileUtil;
import net.locosoft.cascadia.core.util.LogUtil;

public final class Cascadia extends Id {

	final Id _Cascadia_Thing = new Id("thing", this);
	final Id _Cascadia_Thing_Name = new Id("name", _Cascadia_Thing);
	final Id _Cascadia_Thing_Type = new Id("type", _Cascadia_Thing);

	private String _configPropertiesFilePath;
	private File _configPropertiesFile;
	private long _configPropertiesFileModified;
	private Properties _configProperties;
	private TreeMap<String, Conflux> _confluxMap = new TreeMap<String, Conflux>();

	public Cascadia() {
		super("cascadia");
		_configPropertiesFilePath = CoreUtil.getConfigDir() + "/config.properties";
		_configPropertiesFile = new File(_configPropertiesFilePath);
	}

	Id getId(String suffix) {
		if (suffix == null)
			suffix = "";

		switch (suffix) {
		case "thing.name":
			return _Cascadia_Thing_Name;
		case "thing.type":
			return _Cascadia_Thing_Type;
		default:
			return null;
		}
	}

	String getConfig(Id id, String default_) {
		return getConfig(id.getQId(), default_);
	}

	String getConfig(String key, String default_) {
		long mod = _configPropertiesFile.lastModified();
		if ((_configProperties == null) || (mod > _configPropertiesFileModified)) {
			_configProperties = FileUtil.loadPropertiesFile(_configPropertiesFilePath);
			_configPropertiesFileModified = mod;
			LogUtil.log("Loaded: " + _configPropertiesFilePath //
					+ ", size:" + _configProperties.size());
		}
		return _configProperties.getProperty(key, default_);
	}

	Channel findChannel(String qid, boolean isOutflow) {
		String[] ids = Id.split(qid);
		if (ids.length != 3)
			return null;
		Conflux conflux = _confluxMap.get(ids[0]);
		if (conflux == null)
			return null;
		return conflux.getChannel(ids[1], ids[2], isOutflow);
	}

	public void start() {

		String thingName = getConfig(_Cascadia_Thing_Name, "thing1");
		String thingType = getConfig(_Cascadia_Thing_Type, "unknown");
		LogUtil.log("Cascadia Thing name: " + thingName + ", type: " + thingType);

		processExtensionRegistry();

		for (String id : _confluxMap.keySet()) {
			Conflux conflux = _confluxMap.get(id);
			if ("true".equals(getConfig(id + ".skipStartup", "false"))) {
				LogUtil.log("Skipping startup of conflux: " + conflux.getQId());
			} else {
				LogUtil.log("Starting conflux: " + conflux.getQId());
				conflux.init();
				conflux.startCascades();
			}
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

				conflux.init(id, this);
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
			LogUtil.log("Stopping conflux: " + conflux.getQId());
			conflux.stopCascades();
			conflux.fini();
		}
	}

}
