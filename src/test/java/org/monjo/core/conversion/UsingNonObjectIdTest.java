package org.monjo.core.conversion;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.ConvertUtils;
import org.junit.Before;
import org.junit.Test;
import org.monjo.core.Monjo;
import org.monjo.example.IntegerId;
import org.monjo.example.Status;
import org.monjo.example.StatusConverter;
import org.monjo.test.util.MongoDBTest;

import contrib.org.hibernate.cfg.DefaultNamingStrategy;


public class UsingNonObjectIdTest extends MongoDBTest {

	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance().configure(new DefaultNamingStrategy());
		ConvertUtils.register(new StatusConverter(), Status.class);
	}
	
	@Test
	public void shouldCrud() throws Exception {
		Monjo<Integer, IntegerId> monjo = new Monjo<Integer, IntegerId>(getMongoDB(), IntegerId.class);
		monjo.removeAll();
		IntegerId integerId = new IntegerId();
		integerId.setId(1);
		integerId.setName("Teste");
		monjo.insert(integerId);
		
		IntegerId integerId2 = monjo.find().toList().get(0);
		assertEquals(integerId.getId(), integerId2.getId());
		assertEquals(integerId.getName(), integerId2.getName());
		
	}
}
