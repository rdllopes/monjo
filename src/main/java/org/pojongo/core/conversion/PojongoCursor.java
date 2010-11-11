package org.pojongo.core.conversion;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PojongoCursor<C> {
	private DBCursor cursor;
	private Class<C> clasz;
	private PojongoConverter converter;

	public PojongoCursor(DBCursor dbCursor, PojongoConverter converter, Class<C> clasz) {
		this.cursor = dbCursor;
		this.clasz = clasz;
		this.converter = converter;
	}
	
	public PojongoCursor<C> sort(DBObject orderBy){
		cursor.sort(orderBy);
		return this;
	}
	
	public List<C> toList() {
		DBObject document;
		List<C> list = new ArrayList<C>();
		while (cursor.hasNext()) {
			document = cursor.next();
			C object  = converter.from(document).to(clasz);
			list.add(object);
		}
		return list;
	}
}
