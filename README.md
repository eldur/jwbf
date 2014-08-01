# JWBF - JavaWikiBotFramework
[![Build Status](https://travis-ci.org/eldur/jwbf.svg)](https://travis-ci.org/eldur/jwbf)
[![Coverage Status](https://img.shields.io/coveralls/eldur/jwbf.svg)](https://coveralls.io/r/eldur/jwbf)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.sourceforge/jwbf/badge.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22net.sourceforge%22%20AND%20a%3A%22jwbf%22)


The Java Wiki Bot Framework is a library to maintain and collect up-to-date 
data from [MediaWiki-based wikis](http://www.mediawiki.org) such as Wikipedia.

## Design goals
* Incomplete by definition (a framework, not a bot or API client)
* Handles basic tasks: login, cookies, encoding, token management, edit, query, ...
* Unit- and integration-tested
* No checked exceptions
* Immutable types
* Fluent interfaces
* Replaces XML with JSON
* Ready for Java 8

## Developer resources
* Repositories
  * SNAPSHOTS: [oss.sonatype.org](https://oss.sonatype.org/content/groups/public/net/sourceforge/jwbf/)
  * RELEASES: [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22net.sourceforge%22%20AND%20a%3A%22jwbf%22)
* JavaDocs: [@sf.net](http://jwbf.sourceforge.net/doc/) (Version specific docs are located [at Maven Central](http://search.maven.org/#search|gav|1|g%3A%22net.sourceforge%22%20AND%20a%3A%22jwbf%22))
* Home Page: [@sf.net](http://jwbf.sourceforge.net/)


## Java 
### Getting started
If you are new to Java development, you will find it easier to use a program 
like Maven to automate project builds and manage dependencies for you. To 
use Maven to start a new project, follow the 
[Maven in Five Minutes](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)
tutorial. For a more detailed introduction, see Maven's 
[Getting Started](http://maven.apache.org/guides/getting-started/index.html) 
guide.

The Java Wiki Bot Framework is available from two repositories. For a more 
stable version of JWBF, use the most recent version in the 
[RELEASES repository](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22net.sourceforge%22%20AND%20a%3A%22jwbf%22) 
at Maven Central. For the development version, which will be most up-to-date, 
use the most recent version in the 
[SNAPSHOTS repository](https://oss.sonatype.org/content/groups/public/net/sourceforge/jwbf/) 
at oss.sonatype.org.


### Dependencies
Once you have started your project in Maven and have a `pom.xml` file for your 
bot's project, add the appropriate JWBF dependency to the `<dependencies>` 
section. When you build your project, JWBF and its own dependencies will be 
downloaded automatically.

* From [RELEASES](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22net.sourceforge%22%20AND%20a%3A%22jwbf%22):

```xml
<dependency>
    <groupId>net.sourceforge</groupId>
    <artifactId>jwbf</artifactId>
    <version>2.0.0</version>
</dependency>
```

If you want a different version, find the appropriate `<version>` entry in
the 
[Maven metadata file](http://search.maven.org/remotecontent?filepath=net/sourceforge/jwbf/maven-metadata.xml) in the [Maven Central repository](http://search.maven.org/#browse|1359683689).



* From [SNAPSHOTS](https://oss.sonatype.org/content/groups/public/net/sourceforge/jwbf/):

Add this to your `<repositories>` section: 

```xml
    <repository>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
```

Add this to your `<dependencies>` section: 

```xml
<dependency>
    <groupId>net.sourceforge</groupId>
    <artifactId>jwbf</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

If you want a different version, find the appropriate `<version>` entry in 
the [Maven metadata file](https://oss.sonatype.org/content/groups/public/net/sourceforge/jwbf/maven-metadata.xml) in the sonatype repository.


### Sample code
```java
  public static void main(String[] args) {
    MediaWikiBot wikiBot = new MediaWikiBot("http://en.wikipedia.org/w/");
    Article article = wikiBot.getArticle("42");
    System.out.println(article.getText().substring(5, 42));
    // HITCHHIKER'S GUIDE TO THE GALAXY FANS
    applyChangesTo(article);
    wikiBot.login("user", "***");
    article.save();
  }

  static void applyChangesTo(Article article) {
    // ...
  }
```

More Java examples e.g. for queries can be found at
 [unit-](https://github.com/eldur/jwbf/tree/master/src/test/java/net/sourceforge/jwbf) and
 [integration-test packages](https://github.com/eldur/jwbf/tree/master/src/integration-test/java/net/sourceforge/jwbf).

##Scala

### Dependencies
```scala
libraryDependencies += "net.sourceforge" % "jwbf" % "3.0.0-SNAPSHOT"
```
### Sample code
```scala
object WikiReader extends App {
  val wikiBot = new MediaWikiBot("http://en.wikipedia.org/w/")
  val article = wikiBot.getArticle("42")
  println(article.getText().substring(5, 42))
  // HITCHHIKER'S GUIDE TO THE GALAXY FANS
  applyChangesTo(article)
  wikiBot.login("user", "***")
  article.save()

  def applyChangesTo(article: Article) {
    // ...
  }
}
```

## Working with Wikimedia
If you are working with Wikimedia sites, set an informative User-Agent header,
 because [all Wikimedia sites require a HTTP User-Agent header for all requests](http://meta.wikimedia.org/wiki/User-Agent_policy).

```java
    //Creating a new MediaWikiBot with an informative user agent
    HttpActionClient client = HttpActionClient.builder() //
        .withUrl("http://en.wikipedia.org/w/") //
        .withUserAgent("User name/your email/jwbf/...") //
        .withRequestsPerUnit(10, TimeUnit.MINUTES) //
        .build();
    MediaWikiBot wikiBot = new MediaWikiBot(client);
```


## See also
* [en.Wikipedia Creating a Bot](http://en.wikipedia.org/wiki/Wikipedia:Creating_a_bot#Java)
* [fr.Wikipedia Créer un bot](http://fr.wikipedia.org/wiki/Wikip%C3%A9dia:Cr%C3%A9er_un_bot#Java)
* [de.Wikipedia Bots](http://de.wikipedia.org/wiki/Wikipedia:Bots#Ressourcen)
* [Mediawiki API Documentation](http://www.mediawiki.org/wiki/API)
* [Evaluation of JWBF](https://www.mediawiki.org/wiki/API:Client_code/Evaluations/Java_Wiki_Bot_Framework_(JWBF))
* Wikidata Integration
 * [Fast forwarded test branch](https://github.com/eldur/jwbf/tree/wikidata)
 * [WikiData-Toolkit](https://github.com/Wikidata/Wikidata-Toolkit/issues/11)


## System requirements
* JRE 1.7
