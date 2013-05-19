package org.monjo.core.annotations;

public enum Sort {
	ASC(1), DESC(-1);
	int value;

	private Sort(int i) {
		value = i;
	}

	public int toInt() {
		return value;
	}
}
