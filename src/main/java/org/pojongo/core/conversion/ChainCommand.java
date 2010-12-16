package org.pojongo.core.conversion;

import java.util.ArrayList;
import java.util.List;

public class ChainCommand<T> implements Command<T>{
	private List<Command<T>> list = new ArrayList<Command<T>>(); 

	@Override
	public List<T> execute(List<T> data) {
		for (Command<T> command : list) {
			data = command.execute(data);
		}
		return data;
	}
	
	public void add(Command<T> command){
		list.add(command);
	}

	@Override
	public T execute(T t) {
		for (Command<T> command : list) {
			t = command.execute(t);
		}
		return t;
	}

}
