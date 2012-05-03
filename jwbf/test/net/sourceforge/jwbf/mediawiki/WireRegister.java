package net.sourceforge.jwbf.mediawiki;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.inject.Singleton;

import net.sourceforge.jwbf.core.actions.ContentProcessable;

import com.google.common.collect.Maps;

@Singleton
class WireRegister extends Thread {

  private Map<String, String> cache = Maps.newHashMap();

  public WireRegister() {
    Runtime.getRuntime().addShutdownHook(this);
  }

  public boolean hasContentFor(String url) {
    // TODO Auto-generated method stub
    System.out.println("try to read cache for " + url);
    return false;
  }

  @CheckForNull
  public String getResponse(String url, ContentProcessable a) {
    // TODO Auto-generated method stub
    return null;
  }

  public String putResponse(String url, ContentProcessable a, String response) {
    // TODO Auto-generated method stub
    return response;
  }

  @Override
  public void run() {
    System.out.println("store to file");
    // TODO Auto-generated method stub
  }
}
