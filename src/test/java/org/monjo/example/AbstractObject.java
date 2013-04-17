package org.monjo.example;

import org.bson.types.ObjectId;
import org.monjo.core.annotations.Entity;
import org.monjo.document.IdentifiableDocument;

@Entity
public abstract class AbstractObject implements IdentifiableDocument<ObjectId>{

}
