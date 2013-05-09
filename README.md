wikidata-entity-suggester
=========================

This is a prototype for the Entity Suggester's first and second objectives - suggesting properties and values for a new item in wikidata. I'll be working on adding this entity suggester to Wikidata and improve the sorting order of the entity selector, for GSoC 2013.

Please don't try running this with a Myrrix instance as yet. I shall test this on my desktop first, after build the model with an optimal value of lambda and no. of features that I found from [ParameterOptimizer](http://myrrix.com/tuning-quality/); I'll be able to properly test this code on it and report my findings.

It's an initial prototype completely in Java, using [Myrrix](http://myrrix.com)' Java API. The current code is pretty quick-n-dirty, a bunch of static java methods working together. I'll write a PHP wrapper over this, or most probably make a REST-based servlet API to call from PHP.

## What it does ##

Check out the usage info it dumps when it's run without any arguments like so:

<pre>
usage: java -jar entity-suggester.jar
 -c,--count &lt;how many>                     Number of recommendations to fetch
    --dbhost &lt;MySQL Database Host>
    --dbname &lt;MySQL Database Name>
    --dbpass &lt;MySQL Database Password>
    --dbuser &lt;MySQL Database user>
 -h,--host &lt;hostname/IP>                   Myrrix serving layer host
 -i,--ingest &lt;CSV file name>               Ingest CSV file
 -l,--property-list &lt;property list file>   File with list of properties and property:value pairs
 -p,--port &lt;port>                          Myrrix serving layer port
 -r,--recommend &lt;itemID> &lt;property|value>  Recommend properties/values for item with given id. Type of recommendation can be either 'property' or 'value'
</pre>


1. The --ingest option is used to genearte the model in Myrrix by reading a csv file that contains item-property pairs and item-(property:value) pairs.

2. --recommend is used to provide suggestions for properties and values for the given item.

I'm expecting pretty good results on properties, not sure about values - need to experiment and see!

## What are those classes for? ##

1. entitysuggester.rescorer.EntityRescorerProvider is given as a parameter to the Myrrix engine at startup. It is used to inject some logic into the recommendation engine to provide differently filtered recommendations for i) properties and ii) property:value's

2. entitysuggester.rescorer.ItemListRetriever is a class that is used by the above class to build a map with properties or property:value strings as VALUE, and hashed representations of them as KEY. This is essential as Myrrix works with only LONG IDs. A map like this is loaded in memory to convert between the original string representations and the corresponding hashed LONG IDs.

3. entitysuggester.EntitySuggester is the main class that does the client work.

## TODO ##

1. Provide suggestions for absolutely new items (having one or two properties and values if any, freshly entered by the user) - this is the real thing, and quite easy to implement too.

2. Make the code cleaner and expose functionality through a REST-based servlet.

3. Make this README more friendly. The reader should be able to set everything up and test after one read.

4. I may re-write read_items.c (please see below) using an xml library and map-reduce to speed it up later. Might not be necessary at the moment, considering it takes 45 mins to parse the xml file and push into MySQL on my machine.

------------------------------------------------------------------------------------------------

## How to build the required CSV files from wikidata data dumps ##

Byrial has written a few C programs that have turned out to be really helpful to me. Please check out [this link](http://www.wikidata.org/wiki/User:Byrial).

I've used read_items.c to parse the pages-meta-current.xml dump from [here](http://dumps.wikimedia.org/wikidatawiki/20130417/) and store it into a database. Using a couple of SQL queries (see [build_csv.sql](sql/build_csv.sql)), I've generated a csv file with item-property pairs and item-<property-value> pairs for use with the recommendation engine. I've written a simple php file to accept a few parameters and call the engine. I tried to host this on a remote VPS that I currently have access to, but unfortunately, its Burst RAM goes upto 1GB and the recommendation engine alone is using a heap of 1600m + currently.

Keep watching this repo. :)
