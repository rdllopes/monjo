package org.monjo.core;

import java.util.ArrayList;
import java.util.List;

import org.monjo.core.conversion.MonjoConverter;
import org.monjo.document.DirtWatcherProxifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MonjoCursor<C extends Object> {
	
	private static final Logger logger = LoggerFactory.getLogger(MonjoCursor.class);
	
	private DBCursor cursor;
	private MonjoConverter<C> converter;
	private Command<C> command;

	private boolean shouldUseProxy;

	
	public MonjoCursor(DBCursor dbCursor, MonjoConverter<C> converter, Command<C> command) {
		this.cursor = dbCursor;
		this.converter = converter;
		if (command == null){
			command = new NullCommand<C>();
		}
		this.command = command;
	}

	public MonjoCursor<C> sort(DBObject orderBy){
		cursor.sort(orderBy);
		return this;
	}
	
	public MonjoCursor<C> limit(int n){
		cursor.limit(n);
		return this;
	}
	
	public MonjoCursor<C> skip(int n){
		cursor.skip(n);
		return this;
	}

	public int count(){
		return cursor.count();
	}
	
	public MonjoCursor<C> proxify(){
		this.shouldUseProxy = true;
		return this;
	}
	
	public List<C> toList(){
		DBObject document;
		List<C> list = new ArrayList<C>();
		while (cursor.hasNext()) {
			document = cursor.next();
			logger.debug("document found:{}", document);
			C object  = converter.from(document).to();
			if (shouldUseProxy) {
				object = DirtWatcherProxifier.proxify(object);				
			}
			list.add(object);
		}		
		return command.execute(list);
	}
}
