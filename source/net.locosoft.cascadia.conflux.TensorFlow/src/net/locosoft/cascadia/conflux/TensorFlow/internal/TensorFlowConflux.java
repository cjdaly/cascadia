/*********************************************************************
* Copyright (c) 2018 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.cascadia.conflux.TensorFlow.internal;

import net.locosoft.cascadia.core.Cascade;
import net.locosoft.cascadia.core.Conflux;

public class TensorFlowConflux extends Conflux {

	protected Cascade[] constructCascades() {
		return new Cascade[] { new TensorFlowCascade(this) };
	}

}
