package org.monj.example;

public enum Status {
	  Alpha(3),
	  Beta(6),
	  Delta(4) {
	    @Override public int getValue() { return -1; }

	    @Override public String toString() { return "Delta"; }
	  },
	  Epsilon(9), NEW(10);

	  private int value;

	  Status(int value) { this.value = value; }

	  public int getValue() { return this.value; }

	  public static boolean isEnumWorkaround(Class enumClass) {
	    while ( enumClass.isAnonymousClass() ) {
	      enumClass = enumClass.getSuperclass();
	    }
	    return enumClass.isEnum();
	  }
}