Wikidata Entity Suggester
=========================

This is a prototype for the Entity Suggester's first and second objectives - suggesting properties and values for a new item in wikidata. I'll be working on adding this entity suggester to Wikidata and improving the sorting order of the entity selector.

As of now, Myrrix is used to build a basic model. Optimal value of lambda and no. of features that I found from [ParameterOptimizer](http://myrrix.com/tuning-quality/) are not being used currently. I need to do more experimentation for that.

It's an initial prototype completely in Java, using [Myrrix](http://myrrix.com)' Java API. I'll write a PHP wrapper over this, or most probably make a REST-based servlet API to be called from PHP.

## What it does ##

### Command-line usage ###
Check out the usage info it dumps when it's run without any arguments like so:

<pre>
usage: java -jar entity-suggester.jar [-h &lt;hostname/IP> | -p &lt;port> | -i &lt;CSV file name>]   -r &lt;item ID>
       &lt;property|value> | -a &lt;property|value> &lt;p1> [&lt;p2> ...] [&lt;p1----v1> &lt;p2----v2> ...]>  [-c &lt;how many> | -l
       &lt;property list file> | --dbhost &lt;MySQL Database Host> | --dbname &lt;MySQL Database Name> | --dbuser &lt;MySQL Database
       user> | --dbpass &lt;MySQL Database Password>]

detailed usage:
 -h,--host &lt;hostname/IP>                                                            Myrrix serving layer host
 -p,--port &lt;port>                                                                   Myrrix serving layer port
 -i,--ingest &lt;CSV file name>                                                        Ingest CSV file
 -r,--recommend &lt;item ID> &lt;property|value>                                          Recommend properties or
                                                                                    property----value pairs for item
                                                                                    with given id. Type of
                                                                                    recommendation can be either
                                                                                    'property' or 'value'
 -a,--recommend-anon &lt;property|value> &lt;p1> [&lt;p2> ...] [&lt;p1----v1> &lt;p2----v2> ...]>  Recommend properties/values for an
                                                                                    'anonymous' item. A list of
                                                                                    properties and/or property:value
                                                                                    pairs is given as input.
 -c,--count &lt;how many>                                                              Number of recommendations to fetch
 -l,--property-list &lt;property list file>                                            File with list of properties and
                                                                                    property----value pairs
    --dbhost &lt;MySQL Database Host>
    --dbname &lt;MySQL Database Name>
    --dbuser &lt;MySQL Database user>
    --dbpass &lt;MySQL Database Password>
Thanks for using the entity-suggester prototype. Remember to start the Myrrix instance before running this.
</pre>


1. The --ingest option is used to generate the model in Myrrix by reading a csv file that contains item-property pairs and item-(property----value) pairs.<br/><br/>
* An __item-property pair__ looks like this :
<pre>60,373,7</pre>
Here, 60 is Item ID, for [New York City](http://www.wikidata.org/wiki/Q60) in this case. 373 is the Property ID for [Commons Category](http://www.wikidata.org/wiki/Property:P373). 7 is a relative score for this data point.<br/><br/>
Now, about relative scores - temporarily, I have attached different relative scores for the most popular properties. More popular the property, higher this relative value. It signifies "how strong" the attachment is for this item-property pair. Let's take the property [instance of](http://www.wikidata.org/wiki/Property:P31) for example - it's quite popular and is meant to somewhat accurately describe very concisely what the item actually IS. So I figure it should be a valuable metric - hence the higher value for it - 40 - greater than the standard 7. Please check the [build_csv.sql](sql/build_csv.sql) for details.<br/>I will later have to use an algorithm to assign optimal weights according to property popularity and see what works best. Your views/advice/ideas are very much welcome.<br/>
Property popularity can be found out from the database after running the read_items.c script (See [extreme bottom](#how-to-build-the-required-csv-files-from-wikidata-data-dumps)). Please check [this page](http://www.wikidata.org/wiki/User:Byrial/Property_statistics) for some statistics.
* An __item-property-value pair__ looks like this :
<pre>79860,107----618123,50</pre>
OR
<pre>79860,373----Huntsville  Alabama,40</pre><br/>
Both are possible. In both cases 79860 is the Item ID. 618123 is the Value ID for Property 107. The relative weight is 50.

2. --recommend is used to provide suggestions for properties and values for the given item.

3. --recommend-anon is used to provide suggestions for properties and values for a new/not-yet-created item. This has the logic that will be executed in "real time" while the user creates a new item on wikidata. The user needs to provide at least a couple of properties/property-value pairs, ie. only after the author keys in a couple of values on wikidata (of course it needs at least some data), the recommender will start learning and suggesting.

### Examples ###
Please check [this wiki page](https://github.com/nilesh-c/wikidata-entity-suggester/wiki/Examples) for a few command line examples and outputs before trying it out.


## What are those classes for? ##

1. entitysuggester.rescorer.EntityRescorerProvider is given as a parameter to the Myrrix engine at startup. It is used to inject some logic into the recommendation engine to provide differently filtered recommendations for i) properties and ii) property-values

2. entitysuggester.rescorer.ItemListRetriever is a class that is used by the above class to build a map with properties or property:value strings as VALUE, and hashed representations of them as KEY. This is essential as Myrrix works with only LONG IDs. A map like this is loaded in memory to convert between the original string representations and the corresponding hashed LONG IDs.

3. entitysuggester.EntitySuggester is the main class that does the client work.



## TODO ##

1. Expose functionality through a REST-based servlet.

2. I may rewrite read_items.c (please see below) using an xml library and map-reduce to speed it up later. Might not be necessary at the moment, considering it takes 45 mins to parse the xml file and push into MySQL on my machine.


## How to build the required CSV files from wikidata data dumps ##

Byrial has written a few C programs that have turned out to be really helpful to me. Please check out [this link](http://www.wikidata.org/wiki/User:Byrial).

I've used read_items.c to parse the pages-meta-current.xml dump from [here](http://dumps.wikimedia.org/wikidatawiki/20130417/) and store it into a database. I've written a couple of SQL queries (see [build_csv.sql](sql/build_csv.sql)), and generated a csv file with item-property pairs and item-&lt;property:value> pairs for use with the recommendation engine. I am currently managing a remote VPS that I will use to host a Myrrix instance and this client (after I write a REST interface for it) so that anyone can try it out without having to set all this up on their own machines.

## How to set up everything on linux ##
<pre>
sudo apt-get install libmysqlclient-dev
cd /path/to/your/working/directory
git clone https://github.com/nilesh-c/wikidata-entity-suggester.git
ant
wget http://toolserver.org/~byrial/wikidata-programs/read_items.c
wget http://toolserver.org/~byrial/wikidata-programs/wikidatalib.c
wget http://toolserver.org/~byrial/wikidata-programs/wikidatalib.h
wget http://myrrix-recommender.googlecode.com/files/myrrix-serving-0.11.jar
wget http://dumps.wikimedia.org/wikidatawiki/20130417/wikidatawiki-20130417-pages-meta-current.xml.bz2
</pre>
You might need to modify the source of wikidatalib.h to change #include &lt;mysql.h> to #include &lt;mysql/mysql.h><br/>
Change DATABASE_HOST, DATABASE_USER and DATABASE_PASSWD to your respective values.<br/>
Replace &lt;user> and &lt;password> with the your mysql credentials.
<pre>
mysql -u&lt;user> -p&lt;password> -e "CREATE DATABASE wikidatawiki"
gcc read_items.c
./a.out
mysql -u&lt;user> -p&lt;password> wikidatawiki &lt; sql/build_csv.sql
</pre>
Run the commands in [init.sh](init.sh):
<pre>
cat /tmp/item-property.csv > data.csv
cat /tmp/item-property-value.csv >> data.csv
cp data.csv myrrix/
cat data.csv | cut -d',' -f22 | sort | uniq > prop-list
</pre>
Start the myrrix instance like so:
<pre>
java -Xmx2500m -Dproplist=prop-list -XX:NewRatio=12 -cp myrrix-serving-0.11.jar:dist/wikidata-entity-suggester.jar net.myrrix.web.Runner --rescorerProviderClass entitysuggester.rescorer.EntityRescorerProvider --localInputDir myrrix --port 8080
</pre>
Run the client, replacing user and password with the your mysql credentials:
<pre>
java -jar dist/*.jar -p 8080 -h 127.0.0.1 -c 10 -a property 17 107----618123 31----532  -dbhost localhost -dbname wikidatawiki -dbuser user -dbpass password -l prop-list
</pre>
Sample output:
<pre>
Suggested properties:
31 => instance of
61 => discoverer or inventor
107 => main type (GND)
131 => is in the administrative unit
132 => type of administrative division
155 => preceded by
156 => followed by
60 => type of astronomical object
373 => Commons category
374 => INSEE municipality code
</pre>


Keep watching this repo. :)
