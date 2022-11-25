PLMultiAnalyzer
===============
A Lucene custom analyzer that allow for indexing multiple tokens for a single term 
it supports storing terms with Mixed-case letters and terms with punctuation. This in theory should produce more accurate results as it causes Lucene to perform a more exact search.

Released under the [MIT License](https://opensource.org/licenses/MIT).

[![CI](https://github.com/digital-preservation/utf8-validator/workflows/CI/badge.svg)](https://github.com/evolvedbinary/PLMultiAnalyzer/actions/workflows/ci.yaml?query=workflow%3ACI)
[![Maven Central]()]()
# Adding a Custom analyzer to exist-db

## 1.Building eXist-db
```bash
$ git clone https://github.com/eXist-db/exist.git
$ cd exist
$ git checkout master
$ mvn -DskipTests package
```
we will refer to the exist-db directory as `$EXIST_HOME`
you can set it using
**Linux/macOS:**
```bash
$ export EXIST_HOME=/your/path/to/eXist-db
```
**Windows:** 
```cmd
$ set EXIST_HOME=C:\your\path\to\eXist-db
```

## Copy the Jar into exist-db directory
**Linux/macOS:**
```shell
$ cp analyzer-jar.jar  $EXIST_HOME/exist-distribution/target/exist-distribution-[version]-dir/lib
```

**Windows:** 
```cmd
$ copy analyzer-jar.jar  %EXIST_HOME%\exist-distribution\target\exist-distribution-[version]-dir\lib
```

## Add the analyzer dependency in exist start up script
in your `$EXIST_HOME/exist-distribution/target/exist-distribution-[version]-dir/etc/startup.xml`
 add to the dependencies 
```xml
<dependencies>
    ... <!-- other dependencies -->
    <dependency>
        <groupId>com.evolvedbinary.lucene.analyzer</groupId>
        <artifactId>ohAnalyzer</artifactId> 
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>analyzer-jar.jar</relativePath> <!-- must be exact match to the jar in lib folder -->
    </dependency>
    ... <!-- other dependencies -->
<dependencies>
```
## Start up exist 
run the start up script
**Linux/macOS:**
```shell
$ $EXIST_HOME/exist-distribution/target/exist-distribution-[version]-dir/bin/startup.sh
```

**Windows:** 
```cmd
$ %EXIST_HOME%\exist-distribution\target\exist-distribution-[version]-dir\bin\startup.bat
```

## Index The data using the custom Analyzer
when creating the index config specify the `Analyzer` as `com.evolvedbinary.lucene.analyzer.OhAnalyzer`
the `Analyzer` needs two parameters
* `minimumTermLength`: the minimum length of any decomposed term, any smaller decomposed terms will be discarded. Set to 0 to indicate no minimum.
* `punctuationDictionary`:  the dictionary of punctuation to use for decomposition.

```xml
<collection xmlns="http://exist-db.org/collection-config/1.0">
    <index xmlns:wiki="http://exist-db.org/xquery/wiki" xmlns:html="http://www.w3.org/1999/xhtml" xmlns:atom="http://www.w3.org/2005/Atom">
        <!-- Lucene index is configured below -->
        <lucene>
	        <analyzer id="cus" class="com.evolvedbinary.lucene.analyzer.OhAnalyzer">
                <param name="minimumTermLength" type="java.lang.Integer" value="2" />
                <param name="punctuationDictionary" type="java.util.Set">
                    <value>'</value>
                    <value>-</value>
                    <value>’</value>
                </param>
            </analyzer>
            <text qname="SAMPLE" analyzer="cus"/>
        </lucene>
    </index>
</collection>
```