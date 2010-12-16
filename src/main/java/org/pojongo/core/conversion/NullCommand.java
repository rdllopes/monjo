package org.pojongo.core.conversion;

import java.util.List;

public class NullCommand<T> implements Command<T>{

	@Override
	public List<T> execute(List<T> list) {
		return list;
	}

	@Override
	public T execute(T t) {
		return t;
	}

}
