wikidata-entity-suggester
=========================

This is a prototype for the Entity Suggester's first and second objectives - suggesting properties and values for a new item in wikidata.

This initial prototype uses php and [bcc/myrrix](https://github.com/michelsalib/bcc-myrrix). I might have to switch to the Myrrix Java Client and call it from PHP or something. It basically works as follows:

1. The initial model is generated in Myrrix by reading a csv file that contains item-property pairs and item-(property:value) pairs.

2. An anonymous (new/fresh item) item with maybe 1 or two properties (and possibly < property:value >s too) is generated and Myrrix is asked to provide suggestions for properties for the given item.

This can be easily extended to provide recommendations for property:value (that is, suggest values) too.

TODO:

1. Use TranslatingClientRecommender to hash property-value pairs into numeric keys.

2. Use IDRescorer and IDRescorerProvider to fetch property suggestions or value suggestions according to user input.

------------------------------------------------------------------------------------------------

Byrial has written a few C programs that have turned out to be really helpful to me. Please check out [this link](http://www.wikidata.org/wiki/User:Byrial).

Using the pages-meta-current.xml dump from [here](http://dumps.wikimedia.org/wikidatawiki/20130417/) I've generated a csv file with item-property pairs and item-<property-value> pairs to use with the recommendation engine. I've written a simple php file to accept a few parameters and call the engine. I tried to host this on a remote VPS that I currently have access to, but unfortunately, it's Burst RAM goes upto 1GB and the recommendation engine alone is using a heap of 1100MB currently. A few test runs on my own machine gives the following results:
