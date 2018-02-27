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

public abstract class Conflux extends Id {

	private Cascadia _cascadia;
	private TreeMap<String, Cascade> _cascades = new TreeMap<String, Cascade>();

	void init(String id, Cascadia cascadia) {
		initId(id);
		_cascadia = cascadia;
	}

	void startCascades() {
		for (Cascade cascade : constructCascades()) {
			_cascades.put(cascade.getId(), cascade);
			cascade.init();
			cascade.start();
		}
	}

	void stopCascades() {
		for (Cascade cascade : _cascades.values()) {
			cascade.stop();
			cascade.fini();
		}
	}

	protected Cascade[] constructCascades() {
		return new Cascade[0];
	}

	protected final Id getCascadiaId(String suffix) {
		if (_cascadia == null)
			return null;
		else
			return _cascadia.getId(suffix);
	}

	protected final String getConfig(Id id, String default_) {
		if (_cascadia == null)
			return default_;
		else
			return _cascadia.getConfig(id, default_);
	}

	protected void init() {
	}

	protected void fini() {
	}

}
