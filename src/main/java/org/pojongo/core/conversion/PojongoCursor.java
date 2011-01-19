package org.pojongo.core.conversion;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PojongoCursor<C> {
	
	private static final Logger logger = LoggerFactory.getLogger(PojongoCursor.class);
	
	private DBCursor cursor;
	private Class<C> clasz;
	private PojongoConverter converter;
	private Command<C> command;

	// public PojongoCursor(DBCursor dbCursor, PojongoConverter converter, Class<C> clasz) {}
	
	public PojongoCursor(DBCursor dbCursor, PojongoConverter converter,
			Class<C> clasz, Command<C> command) {
		this.cursor = dbCursor;
		this.clasz = clasz;
		this.converter = converter;
		if (command == null){
			command = new NullCommand<C>();
		}
		this.command = command;
	}

	public PojongoCursor<C> sort(DBObject orderBy){
		cursor.sort(orderBy);
		return this;
	}
	
	public PojongoCursor<C> limit(int n){
		cursor.limit(n);
		return this;
	}
	
	public PojongoCursor<C> skip(int n){
		cursor.skip(n);
		return this;
	}

	public int count(){
		return cursor.count();
	}
	
	public List<C> toList(){
		DBObject document;
		List<C> list = new ArrayList<C>();
		while (cursor.hasNext()) {
			document = cursor.next();
			logger.debug("document found:{}", document);
			C object  = converter.from(document).to(clasz);			
			list.add(object);
		}		
		return command.execute(list);
	}
}
