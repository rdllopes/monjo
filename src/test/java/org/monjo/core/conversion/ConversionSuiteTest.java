package org.monjo.core.conversion;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.internal.builders.JUnit4Builder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.monjo.test.util.MongoDBUtil;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ComplexSaveTest.class,
		DefaultDocumentToObjectConverterTest.class,
		DefaultObjectToDocumentConverterTest.class, FinbdByExampleTest.class,
		ImprovedNamingConverterTest.class, MonjoCursorTest.class,
		MonjoTest.class, SimplePojoTest.class, UpdateTest.class,
		UsingNonObjectIdTest.class })
public class ConversionSuiteTest {

	public static void main(String[] args) throws Exception {
		JUnit4Builder builder = new JUnit4Builder();
		Suite suite = new Suite(builder, new Class[] { ComplexSaveTest.class,
				DefaultDocumentToObjectConverterTest.class,
				DefaultObjectToDocumentConverterTest.class,
				FinbdByExampleTest.class, ImprovedNamingConverterTest.class,
				MonjoCursorTest.class, MonjoTest.class, SimplePojoTest.class,
				UpdateTest.class, UsingNonObjectIdTest.class });
		// teste de desempenho
		JUnitCore c = new JUnitCore();
		setup();
		for (int i =0; i < 100; i++)
			c.run(Request.runner(suite));
		tearDown();

	}
	
	@BeforeClass
	public static void setup() throws Exception {
		MongoDBUtil.connectToMongo();
	}

	@AfterClass
	public static void tearDown() {
		MongoDBUtil.tearDown();
	}

}
