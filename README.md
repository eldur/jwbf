JWBF - JavaWikiBotFramework
---
The Java Wiki Bot Framework is a library to maintain Wikis like Wikipedia based on MediaWiki. 

```scala
object WikiReader extends App {
  val wikiBot = new MediaWikiBot("http://en.wikipedia.org/w/")
  val article = wikiBot.getArticle("42")
  println(article.getText().substring(5, 42))
  // HITCHHIKER'S GUIDE TO THE GALAXY FANS
  change(article)
  wikiBot.login("user", "***")
  article.save()

  def change(article: Article) {
    // ...
  }
}
```
