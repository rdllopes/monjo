package org.pojongo.core.conversion;

import java.util.List;

public interface Command<T> {
	
	public List<T> execute(List<T> list);
	
	public T execute(T t);
}
