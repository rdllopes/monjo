package org.monjo.core;

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
