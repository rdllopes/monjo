package org.monjo.document;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DirtWatcherProxifierTest {

	@Test
	public void shouldProxifyJavaBean() {
		JavaBean javaBean = new JavaBean();
		JavaBean proxy = DirtWatcherProxifier.proxify(javaBean);
		proxy.setName("a name");
		if (proxy instanceof DirtFieldsWatcher) {
			DirtFieldsWatcher dirtFieldsWatcher = (DirtFieldsWatcher) proxy;
			assertTrue(dirtFieldsWatcher.dirtFields().contains("setName"));
		}
	}
	
	@Test
	public void shouldWatchInternalCalls() {
		JavaBean javaBean = new JavaBean();
		JavaBean proxy = DirtWatcherProxifier.proxify(javaBean);
		proxy.setDescription("special");
		if (proxy instanceof DirtFieldsWatcher) {
			DirtFieldsWatcher dirtFieldsWatcher = (DirtFieldsWatcher) proxy;
			assertTrue(dirtFieldsWatcher.dirtFields().contains("setDescription"));
		}
		
	}
	
	public static class JavaBean {
		private String name;
		private String description;
		String getName() {
			return name;
		}
		void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			if ("special".equals(description)){
				setName("special");
			}
			this.description = description;
		}
		
		
		
		
	}

}
