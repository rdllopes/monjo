package org.pojongo.core.conversion;

import org.hibernate.cfg.NamingStrategy;

public interface PojongoConverter extends ObjectToDocumentConverter,
		DocumentToObjectConverter {

	void setNamingStrategy(NamingStrategy namingStrategy);

}
