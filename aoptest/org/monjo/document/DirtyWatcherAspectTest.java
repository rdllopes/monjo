package org.monjo.document;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.matchers.JUnitMatchers.hasItems;
import java.util.Set;

import org.junit.Test;
import org.monjo.example.SimplePOJO;


public class DirtyWatcherAspectTest {
	
	
	@Test
	public void shouldVerifyFieldCalling(){
		SimplePOJO simplePOJO = new SimplePOJO();
		simplePOJO.setaDoubleField(10.0);
		InternalMonjoObject monjoObject = (InternalMonjoObject) simplePOJO;
		Set<String> dirtFields = monjoObject.dirtFields();
		assertEquals(1, dirtFields.size());
		assertThat(dirtFields, hasItems("setaDoubleField"));
	}
}
