package com.is.websocket;

public class name {
	private static name source;

	private name() {
		if (source != null)
			throw new Error();
	}

	public static synchronized name getInstance() {
		if (source == null) {
			source = new name();
		}
		return source;
	}
}
