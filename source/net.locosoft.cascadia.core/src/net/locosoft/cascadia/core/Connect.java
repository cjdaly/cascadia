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

import net.locosoft.cascadia.core.drop.Drop;

public class Connect {

	private final String[] _inflowExitIds;
	private final String[] _outflowEntryIds;
	private final Buffer _buffer;

	Connect(String[] inflowExitIds, String[] outflowEntryIds, Buffer buffer) {
		_inflowExitIds = inflowExitIds;
		_outflowEntryIds = outflowEntryIds;
		_buffer = buffer;
	}

	Connect(String[] inflowExitIds, String[] outflowEntryIds) {
		this(inflowExitIds, outflowEntryIds, new SimpleBuffer());
	}

	protected String[] getInflowExitIds() {
		return _inflowExitIds;
	}

	protected String[] getOutflowEntryIds() {
		return _outflowEntryIds;
	}

	protected void cycleBegin() {
		_buffer.cycleBegin();
	}

	public void fill(Drop drop, String inflowExitId) {
		_buffer.fill(drop, inflowExitId);
	}

	public Drop spill(String outflowEntryId) {
		return _buffer.spill(outflowEntryId);
	}

	protected void cycleEnd() {
		_buffer.cycleEnd();
	}

	String getMapping() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < _inflowExitIds.length; i++) {
			if (i > 0)
				sb.append(',');
			sb.append(_inflowExitIds[i]);
		}
		sb.append("}~>{");
		for (int i = 0; i < _outflowEntryIds.length; i++) {
			if (i > 0)
				sb.append(',');
			sb.append(_outflowEntryIds[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	public static class Single extends Connect {

		public Single(String inflowExitId, String outflowEntryId, Buffer buffer) {
			super(new String[] { inflowExitId }, new String[] { outflowEntryId }, buffer);
		}

		public Single(String inflowExitId, String outflowEntryId) {
			super(new String[] { inflowExitId }, new String[] { outflowEntryId });
		}
	}

	public static class FanIn extends Connect {

		public FanIn(String[] inflowExitIds, String outflowEntryId, Buffer buffer) {
			super(inflowExitIds, new String[] { outflowEntryId }, buffer);
		}

		public FanIn(String[] inflowExitIds, String outflowEntryId) {
			super(inflowExitIds, new String[] { outflowEntryId });
		}
	}

	public static class FanOut extends Connect {

		public FanOut(String inflowExitId, String[] outflowEntryIds, Buffer buffer) {
			super(new String[] { inflowExitId }, outflowEntryIds, buffer);
		}

		public FanOut(String inflowExitId, String[] outflowEntryIds) {
			super(new String[] { inflowExitId }, outflowEntryIds);
		}
	}

	public static abstract class Buffer {

		protected void cycleBegin() {
		}

		public abstract void fill(Drop drop, String inflowExitId);

		public abstract Drop spill(String outflowEntryId);

		protected void cycleEnd() {
		}

	}

	public static class SimpleBuffer extends Buffer {

		protected Drop _buffer = null;

		protected void cycleBegin() {
			_buffer = null;
		}

		public void fill(Drop drop, String inflowExitId) {
			_buffer = drop;
		}

		public Drop spill(String outflowEntryId) {
			return _buffer;
		}

		protected void cycleEnd() {
			_buffer = null;
		}

	}

}
