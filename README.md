wikidata-entity-suggester
=========================

This is a prototype for the Entity Suggester's first and second objectives - suggesting properties and values for a new item in wikidata.

It basically works as follows:

1. The initial model is generated in Myrrix by reading a csv file that contains item-property pairs and item-(property:value) pairs.

2. An anonymous (new/fresh item) item with maybe 1 or two properties (and possibly < property:value >s too) is generated and Myrrix is asked to provide suggestions for properties for the given item.

This can be easily extended to provide recommendations for property:value (that is, suggest values) too.
