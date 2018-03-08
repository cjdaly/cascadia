/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.core.internal.conflux;

import net.locosoft.cascadia.core.Collector;
import net.locosoft.cascadia.core.Conflux;

public class CoreCollector extends Collector {

	public CoreCollector(Conflux conflux) {
		super("coreCollector", conflux);
	}

}
