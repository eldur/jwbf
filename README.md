# JWBF - JavaWikiBotFramework
[![Build Status](https://travis-ci.org/eldur/jwbf.svg)](https://travis-ci.org/eldur/jwbf)
[![Coverage Status](https://img.shields.io/coveralls/eldur/jwbf.svg)](https://coveralls.io/r/eldur/jwbf)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.sourceforge/jwbf/badge.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22net.sourceforge%22%20AND%20a%3A%22jwbf%22)

The Java Wiki Bot Framework is a library to maintain Wikis like Wikipedia based on MediaWiki.

## Quick Start
### Java
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
 [Unit-](https://github.com/eldur/jwbf/tree/master/src/test/java/net/sourceforge/jwbf) and
 [Integrationtestpackages](https://github.com/eldur/jwbf/tree/master/src/integration-test/java/net/sourceforge/jwbf).

### Scala
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
 because [all Wikimedia sites require a HTTP User-Agent header for all requests.](http://meta.wikimedia.org/wiki/User-Agent_policy)

```java
    HttpActionClient client = HttpActionClient.builder() //
        .withUrl("http://en.wikipedia.org/w/") //
        .withUserAgent("User name/your email/jwbf/...") //
        .withRequestsPerUnit(10, TimeUnit.MINUTES) //
        .build();
    MediaWikiBot wikiBot = new MediaWikiBot(client);
```

## Dependency
```xml
<dependency>
    <groupId>net.sourceforge</groupId>
    <artifactId>jwbf</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```
```scala
libraryDependencies += "net.sourceforge" % "jwbf" % "3.0.0-SNAPSHOT"
```

* SNAPSHOTS: [oss.sonatype.org](https://oss.sonatype.org/content/groups/public/net/sourceforge/jwbf/)
* RELEASES: [maven central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22net.sourceforge%22%20AND%20a%3A%22jwbf%22)
* Javadocs: [@sf.net](http://jwbf.sourceforge.net/doc/) (Version specific docs are located at Maven central)

## See also
* [en.Wikipedia Creating a Bot](http://en.wikipedia.org/wiki/Wikipedia:Creating_a_bot#Java)
* [fr.Wikipedia Créer un bot](http://fr.wikipedia.org/wiki/Wikip%C3%A9dia:Cr%C3%A9er_un_bot#Java)
* [de.Wikipedia Bots](http://de.wikipedia.org/wiki/Wikipedia:Bots#Ressourcen)
* [Mediawiki API](http://www.mediawiki.org/wiki/API:Client_code)
 * [Evaluation](https://www.mediawiki.org/wiki/API:Client_code/Evaluations/Java_Wiki_Bot_Framework_(JWBF))
* Wikidata-Integration
 * [Fast forwared test branch](https://github.com/eldur/jwbf/tree/wikidata)
 * [WikiData-Toolkit](https://github.com/Wikidata/Wikidata-Toolkit/issues/11)

## Design goals - Product Vision
* incomplete by definition (Framework)
* Handle base tasks like: login, cookies, encoding, token management, edit, query, ...
* Unit- and Integration tested
* no checked exceptions
* immutable types
* fluent interfaces
* replace xml with json
* ready for java 8

## System requirements
* JRE 1.7
