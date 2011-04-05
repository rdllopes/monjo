package org.monjo.document;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class DirtWatcherProxifier {
	private static final Set<Method> OBJECT_METHODS = new HashSet<Method>(Arrays.asList(Object.class.getDeclaredMethods()));
	private static final Set<Method> INTERNAL_METHODS = new HashSet<Method>(Arrays.asList(DirtFieldsWatcher.class.getDeclaredMethods()));
	private static final CallbackFilter IGNORE_BRIDGE_AND_OBJECT_METHODS = new CallbackFilter() {
		public int accept(Method method) {
			if (method.isBridge() || OBJECT_METHODS.contains(method)) return 1;
			if (INTERNAL_METHODS.contains(method)) return 2;
			String name = method.getName();		
			return name.startsWith("set") || name.startsWith("add")  ? 0 : 1;
		}
	};

	public static <T> T proxify(final T pojo) {
		if (pojo instanceof DirtFieldsWatcher) {
			return pojo;
		}
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(pojo.getClass());
		enhancer.setInterfaces(new Class[] { DirtFieldsWatcher.class });
		enhancer.setCallbackTypes(new Class[] { MethodInterceptor.class, Dispatcher.class, MethodInterceptor.class });
		enhancer.setCallbackFilter(IGNORE_BRIDGE_AND_OBJECT_METHODS);
		BaseObject baseObject = new BaseObject();
		enhancer.setCallbacks(new Callback[] { new WatchSetterInterceptor(pojo, baseObject), new Dispatcher() { public Object loadObject() throws Exception {return pojo;}	}, new BypassBaseObjectInterceptor(baseObject) });
		return (T) enhancer.create();
	}

	private static class WatchSetterInterceptor implements MethodInterceptor {
		private BaseObject baseObject;
		private Object target;

		public WatchSetterInterceptor(Object pojo, BaseObject baseObject) {
			this.target = pojo;
			this.baseObject = baseObject;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			baseObject.addDirtField(method.getName());
			return method.invoke(target, args);
		}
	}
	
	private static class BypassBaseObjectInterceptor implements MethodInterceptor {
		private BaseObject baseObject;

		public BypassBaseObjectInterceptor(BaseObject baseObject) {
			this.baseObject = baseObject;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			return method.invoke(baseObject, args);
		}
	}

	private static class BaseObject implements DirtFieldsWatcher {
		private Set<String> dirtFields = new HashSet<String>();

		public Set<String> dirtFields() {
			return dirtFields;
		}

		public void addDirtField(String fieldName) {
			dirtFields.add(fieldName);
		}
	}
}
