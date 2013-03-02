package net.sourceforge.jwbf.mediawiki.bots;

import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

/**
 * @deprecated use your own thread framework
 * 
 */
@Deprecated
public class MediaWikiThreadBot extends MediaWikiBot {

  private ThreadGroup tg = null;
  private Vector<Thread> rv = new Vector<Thread>();

  public MediaWikiThreadBot(URL u) {
    super(u);
    prepare();
  }

  public MediaWikiThreadBot(String url) {
    super(url);
    prepare();
  }

  private void prepare() {
    tg = new ThreadGroup("Modules");

  }

  public ThreadGroup getThreadGroup() {
    return tg;
  }

  public void start() {
    Iterator<Thread> ti = rv.iterator();
    while (ti.hasNext()) {
      Thread thread = ti.next();
      if (!thread.isAlive()) {
        thread.start();
      }

    }
  }

  public Thread addRunnable(Runnable r) {
    Thread t = new Thread(tg, r);
    rv.add(t);
    return t;
  }

  public void addRunnableAndStart(Runnable r) {
    Thread t = addRunnable(r);
    t.start();
  }

}
