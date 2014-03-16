# JWBF - JavaWikiBotFramework [![Build Status](https://travis-ci.org/eldur/jwbf.png)](https://travis-ci.org/eldur/jwbf)

The Java Wiki Bot Framework is a library to maintain Wikis like Wikipedia based on MediaWiki.

## Quick Start
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

## Dependency
```xml
<dependency>
    <groupId>net.sourceforge</groupId>
    <artifactId>jwbf</artifactId>
    <version>2.0.0</version>
</dependency>
```
```scala
libraryDependencies += "net.sourceforge" % "jwbf" % "2.0.0"
```
