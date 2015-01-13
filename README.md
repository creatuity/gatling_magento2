# gatling_magento2
Gatling stress / performance tests for Magento 2.

### Status
This is very much a work in progress. Currently only checkout as guest is implemented.

Currently built against Gatling 2.1.2, but will probably work fine in 2.1.x and possibly newer (depending on what changes in newer Gatling releases)

### Availability
gatling_magento2 is available under the MIT license.

### Requirements
* Java JDK, for Gatling ([as of Gatling 2.1.3, at least JDK7u6](http://gatling.io/docs/2.1.3/quickstart.html#installing))
* Gatling 2.1.x+ : http://gatling.io/download/ 
* Rapture.IO core, data, and json libraries, used for parsing the JSON for configurable products : [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg:%22com.propensive%22)
* Scala Parser Combinators, to support rapture-json : [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22scala-parser-combinators_2.11%22)

### Installation
To properly set up your Gatling installation for these tests, you'll need to install not just Gatling but some additional libraries.

1. Extract latest Gatling release from http://gatling.io/download/, it will create it's own top level directory.
2. Download and place the following JAR libraries in the `lib/` subdirectory of Gatling's top level directory:
  * [rapture-core_2.11](http://search.maven.org/#search|ga|1|a%3A%22rapture-core_2.11%22)
  * [rapture-data_2.11](http://search.maven.org/#search|ga|1|a%3A%22rapture-data_2.11%22)
  * [rapture-json_2.11](http://search.maven.org/#search|ga|1|a%3A%22rapture-json_2.11%22)
  * [scala-parser-combinators_2.11](http://search.maven.org/#search|ga|1|a%3A%22scala-parser-combinators_2.11%22)
  
### Usage
To run tests, you can do so using the following command structure:
```
JAVA_OPTS="-Dbaseurl=http://[URL of website]" [if gatling not in PATH, path to gatling install dir/bin/]gatling.sh -m -sf [path to Magento 2 simulation] -s magento.MagentoSimulation
```
For example, this might be:
```
JAVA_OPTS="-Dbaseurl=http://localhost" ./gatling-charts-highcharts-bundle-2.1.2/bin/gatling.sh -m -sf ./gatling_magento2 -s magento.MagentoSimulation
```

Where options for Gatling are:

`-m` : Mute, doesn't interactively ask for anything

`-sf` : Simulation Folder, specifies where the simulation Scala files are

`-s` : Simulation to run (Scala class name for the simulation, in `namespace.class` format)

Additionally, `JAVA_OPTS` passes settings to the simulation itself, using `-D[option name]=[option value]`:

`baseurl` : Base URL of website, i.e. http://www.example.com. Default is http://localhost.

`realtimeratio` : Realtime ratio for simulated user pauses between actions. 1 is for normal delays, 0 is no delays - this is used as a multiplier for pauses so you can actually use values such as 0.5 or 2, etc. Default is 1.

`atonceusers` : The number of simulated users that spawn immediately (at once). Default is 1.

`rampusers` : The number of simulated users that spawn over the ramp time. Default is 10 users.

`rampseconds` : The duration of the ramp time in seconds. Default is 30 seconds.

(more to come as additional features are implemented)

Note: Gatling may complain about the `-m` and `-s` options, that is the Gatling Scala compiler that is run first before running the Gatling test itself, the options are for the test, this is a known issue as of 2.1.2.

### Customizing Tests
Customizing tests can be done by editing the Scala files that make up the simulation. This can be done with any text editor. Depending on the level of modification you may need to learn a little bit of Scala programming, which is sort of like Java on the surface but not really once you get past the surface - it’s a functional language built on the Java JVM. Less extensive changes can be easily performed by simply looking at the code and changing things - even without knowing Scala, most programmers should find that most of the code is self explanatory, so for example changing the details of the shipping / billing address during checkout should be trivial.

### Creating New Tests
Creating a test from scratch will definitely require learning some Scala, though you can perform more basic tests without much Scala knowledge (by just chaining together Gatling DSL). You’ll also need to consult the Gatling documentation as well. However, while you may have some stumbling blocks with Scala due to making wrong assumptions based on your knowledge of other languages, overall it can be an easier experience vs jMeter since you aren’t trying to decide whether to use the various built in pieces of jMeter via XML (even if created / configured by GUI) or write your own with BeanScript as in jMeter, you do everything with Scala, so you only have to learn one thing.

For more information on writing tests from scratch, Gatling has some excellent documentation to get you started at http://gatling.io/docs/2.1.3/quickstart.html and http://gatling.io/docs/2.1.3/. Additionally, the Gatling User Group is super helpful : https://groups.google.com/forum/#!forum/gatling
