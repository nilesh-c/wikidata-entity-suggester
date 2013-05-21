Wikidata Entity Suggester
=========================

This is a prototype for the Entity Suggester's first and second objectives - suggesting properties and values for a new item in wikidata. I'll be working on adding this entity suggester to Wikidata and improving the sorting order of the entity selector.

As of now, Myrrix is used to build a basic model. Optimal value of lambda and no. of features that I found from [ParameterOptimizer](http://myrrix.com/tuning-quality/) are not being used currently. I need to do more experimentation for that.

It's an initial prototype written in Java and PHP, using [Myrrix](http://myrrix.com)' Java API and [Guzzle](http://guzzlephp.org). The Java backend is a Myrrix instance, plus a couple of custom wrapper servlets that are used to push data into the Myrrix instance and get recommendations from it. The PHP client is built on top of Guzzle and exposes a neat PHP API that can be used to query the backend.

Setting it up is easy - basically, fire up tomcat with the backend war file, run a few commands. Use the PHP API to reap it. I have included a command line standalone client jar too. After building from source, you can find it here:
<pre>
client/target/entity-suggester-client.jar
</pre>

Also, please check out the PHP client for the Entity Suggester [here](https://github.com/nilesh-c/wes-php-client). Usage examples/details are both on this wiki and on the PHP client's README.md.


## Wiki Pages

Please read these pages in sequence to learn how to set everything up and how it works:
* [How to set everything up on linux](https://github.com/nilesh-c/wikidata-entity-suggester/wiki/How-to-set-everything-up-on-linux) (must read!)
* [CSV file explanation](https://github.com/nilesh-c/wikidata-entity-suggester/wiki/CSV-file-explanation)
* [Using the PHP client](https://github.com/nilesh-c/wikidata-entity-suggester/wiki/Using-the-PHP-client) (also contains examples)
* [Using the command line client](https://github.com/nilesh-c/wikidata-entity-suggester/wiki/Using-the-command-line-client) (also contains examples)
* [Which class does what]()


## Acknowledgements

1. [Byrial]([this link](http://www.wikidata.org/wiki/User:Byrial) for sharing the programs used to generate database tables from the wikidata data dump. The property statistics are also being very helpful. I have written a couple of sql codes to generate CSV files as required by the Entity Suggester.

2. [bcc-myrrix](https://github.com/michelsalib/bcc-myrrix) - it's a PHP client for Myrrix built on top of Guzzle. I used its code and modified it to suit my needs for the Entity Suggester PHP Client.


Keep watching this repo. :)
