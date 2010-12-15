package org.pojongo.example;

import java.util.ArrayList;
import java.util.List;

import org.pojongo.core.conversion.DocumentToObjectConverter;
import org.pojongo.core.conversion.ObjectToDocumentConverter;
import org.pojongo.core.conversion.PojongoConverterFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class ConversionExample {
	
	public static void main(String[] args) throws Exception {
		Mongo mongo = new Mongo();
		DB exampleDB = mongo.getDB("pojongoExample");
		DBCollection examples = exampleDB.getCollection("Example");
		
		saveNewExamples(examples);
		listExistingExamples(examples);
	}

	private static void listExistingExamples(DBCollection examples) throws IllegalArgumentException, Exception {
		DocumentToObjectConverter converter = PojongoConverterFactory.getInstance().getDefaultDocumentConverter();;
		List<Example> exampleList = new ArrayList<Example>();
		DBCursor cursor = examples.find();
		
		while (cursor.hasNext()) {
			DBObject document = cursor.next();
			exampleList.add(converter.from(document).to(Example.class));
		}
		
		for (Example example : exampleList) {
			System.out.println(example);
		}
	}

	private static void saveNewExamples(DBCollection examples) {
		ObjectToDocumentConverter converter = PojongoConverterFactory
			.getInstance()
			.getDefaultObjectConverter();
		
		Example oneExample = new Example("first example", 1);
		examples.save(converter.from(oneExample).toDocument());
		
		Example anotherExample = new Example("second example", 2);
		examples.save(converter.from(anotherExample).toDocument());
	}

}
