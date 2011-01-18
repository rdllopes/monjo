package org.pojongo.test.util;

import static org.hamcrest.Matchers.is;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

public class HamcrestPatch {

	public static Matcher<? super Object> classEqualTo(Class<?> cls) {
		return is(Matchers.<Object>equalTo(cls));
	}

}
