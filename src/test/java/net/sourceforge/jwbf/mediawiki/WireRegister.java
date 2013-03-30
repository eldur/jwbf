package net.sourceforge.jwbf.mediawiki;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.ContentProcessable;

import com.google.common.collect.Maps;

@Singleton
@Slf4j
class WireRegister extends Thread {

  private Map<String, String> cache = Maps.newHashMap();

  public WireRegister() {
    Runtime.getRuntime().addShutdownHook(this);
  }

  public boolean hasContentFor(String url) {
    // TODO Auto-generated method stub
    log.debug("try to read cache for " + url);
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
    log.debug("store to file");
    // TODO Auto-generated method stub
  }
}
