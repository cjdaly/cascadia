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

import net.locosoft.cascadia.core.util.CoreUtil;

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
			cascade.start();
		}
	}

	void stopCascades() {
		for (Cascade cascade : _cascades.values()) {
			cascade.stop();
		}
	}

	protected Cascade[] constructCascades() {
		return new Cascade[0];
	}

	Channel getChannel(String cascadeId, String channelId, boolean isOutflow) {
		Cascade cascade = _cascades.get(cascadeId);
		if (cascade == null)
			return null;

		if (isOutflow) {
			return cascade.getOutflowChannel(channelId);
		} else {
			return cascade.getInflowChannel(channelId);
		}
	}

	Channel findChannel(String qid, boolean isOutflow) {
		return _cascadia.findChannel(qid, isOutflow);
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

	final String getConfig(String key, String default_) {
		if (_cascadia == null)
			return default_;
		else
			return _cascadia.getConfig(key, default_);
	}

	protected final String getConfigLocal(String keySuffix, String default_) {
		return getConfig(getQId() + "." + keySuffix, default_);
	}

	protected final String getConfluxPath() {
		return CoreUtil.getConfluxDir() + "/" + getId();
	}

	protected void init() {
	}

	protected void fini() {
	}

}
