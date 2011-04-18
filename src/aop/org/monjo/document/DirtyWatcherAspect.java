package org.monjo.document;

import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareMixin;


@Aspect
public class DirtyWatcherAspect {

	public static class BaseObjectImpl implements DirtFieldsWatcher {
		private Set<String> dirtFields = new HashSet<String>();

		public Set<String> dirtFields() {
			return dirtFields;
		}

		public void addDirtField(String fieldName) {
			dirtFields.add(fieldName);
		}
	}

	@DeclareMixin("(@org.monjo.core.annotations.Entity *)")
	public static DirtFieldsWatcher createMoodyImplementation() {
		return new BaseObjectImpl();
	}

	@Before("call(void (@org.monjo.core.annotations.Entity *).set*(*))")
	public void listOperation(JoinPoint joinPoint) {
		DirtFieldsWatcher entity = (DirtFieldsWatcher) joinPoint.getTarget();
		entity.addDirtField(joinPoint.getSignature().getName());
	}
}
