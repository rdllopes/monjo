package org.monjo.example;

import java.util.ArrayList;
import java.util.List;

import org.monjo.core.conversion.DocumentToObjectConverter;
import org.monjo.core.conversion.MonjoConverterFactory;
import org.monjo.core.conversion.ObjectToDocumentConverter;

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
		DocumentToObjectConverter<Example> converter = MonjoConverterFactory.getInstance().getDefaultDocumentConverter(Example.class);
		List<Example> exampleList = new ArrayList<Example>();
		DBCursor cursor = examples.find();
		
		while (cursor.hasNext()) {
			DBObject document = cursor.next();
			exampleList.add(converter.from(document).to());
		}
		
		for (Example example : exampleList) {
			System.out.println(example);
		}
	}

	private static void saveNewExamples(DBCollection examples) {
		ObjectToDocumentConverter<Example> converter = MonjoConverterFactory
			.getInstance()
			.getDefaultObjectConverter(Example.class);
		
		Example oneExample = new Example("first example", 1);
		examples.save(converter.from(oneExample).toDocument());
		
		Example anotherExample = new Example("second example", 2);
		examples.save(converter.from(anotherExample).toDocument());
	}

}
