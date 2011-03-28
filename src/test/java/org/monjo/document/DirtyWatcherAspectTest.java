package org.monjo.document;

import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Test;
import org.monjo.example.SimplePOJO;


public class DirtyWatcherAspectTest {
	
	
	@Test
	public void shouldVerifyFieldCalling(){
		SimplePOJO simplePOJO = new SimplePOJO();
		simplePOJO.setaDoubleField(10.0);
		InternalMonjoObject monjoObject = (InternalMonjoObject) simplePOJO;
		Set<String> dirtFields = monjoObject.getDirtFields();
		assertEquals(1, dirtFields.size());
		assertTrue(dirtFields.contains("setDoubleField"));
	}
}
