package org.monjo.document;

import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareMixin;


@Aspect
public class DirtyWatcherAspect {

	public static class BaseObjectImpl implements InternalMonjoObject {
		private Set<String> dirtFields = new HashSet<String>();

		public Set<String> getDirtFields() {
			return dirtFields;
		}

		public void addDirtField(String fieldName) {
			dirtFields.add(fieldName);
		}
	}

	@DeclareMixin("(@org.monjo.core.annotations.Entity *)")
	public static InternalMonjoObject createMoodyImplementation() {
		return new BaseObjectImpl();
	}

	@Before("call(void (@org.monjo.core.annotations.Entity *).set*(*))")
	public void listOperation(JoinPoint joinPoint) {
		InternalMonjoObject entity = (InternalMonjoObject) joinPoint.getTarget();
		entity.addDirtField(joinPoint.getSignature().getName());
	}
}
