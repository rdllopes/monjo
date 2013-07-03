# Monjo!

[![Build Status](https://travis-ci.org/rdllopes/monjo.png?branch=master)](https://travis-ci.org/rdllopes/monjo)

Monjo is a lightweight type-safe library for mapping Java objects to/from MongoDB:

* Easy to use, and very lightweight
* Type-safe Query support
* Annotations describe mapping behavior; there are no XML files.
* Extensions: Validation (jsr330), and SLF4J Logging

* Standard Mapping: use a set of rules for determining the physical fields and collections 
 names given the information in the mapping  document (ex: naming strategy prefers
 embedded underscores to mixed case names)
* Javabeans: accessors and mutators are always used to infer fields 

        @Entity("professionals")
        class Employee implements IdentificableDocument<ObjectId>{
          ObjectId id; // auto-generated, if not set (see ObjectId)
          String firstName, lastName; // value types are automatically persisted
          Long salary = null; // only non-null values are stored 

          Address address; // by default fields are embedded

          @ReferenceKey<Employee> manager; //references can be saved without automatic loading

          List<Employee> underlings = new ArrayList<Employee>(); 
             // by default, use DBList to persist inner collections

          @Indexed boolean active = false; //fields can be indexed for better performance
          @Transient int notStored; //fields can be ignored (no load/save)
          transient boolean stored = true; //not @Transient, will be ignored by Serialization/GWT for example.
        }

Special features
----------------

* Find By Example (find a object using any field, maybe an inner inner inner ... object )

    	PojoWithListInnerObject pojo = PojoBuilder.createMegaZordePojo();
    	Monjo<ObjectId, PojoWithListInnerObject> monjo = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(), PojoWithListInnerObject.class);
    	monjo.removeAll();
    	monjo.insert(createMegaZordePojo);
    	pojo.setId(null);
    	List<Category> categories = pojo.getCategories();
    	Category category = categories.get(0);
    	category.setName(null);
    	PojoWithListInnerObject result = pojongo.findByExample(pojo.toList().get(0);

* Cursor (select page of selection, sort and so on) 

        List<SimplePOJO> list = monjo.find().limit(5).toList();
 
* Update inner Objects
 
     	 monjo.<Category> updateInnerObject("categories", category, pojo);


* Does not store Null/Empty values (by default).
* GWT support (entities are just POJOs) -- (GWT ignores annotations)


Note: @Reference will not save objects, just a reference to them; You must save them yourself. 

This library is currently at a very early development stage, but already being used in production in some really big applications.

