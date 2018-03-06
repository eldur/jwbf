package net.sourceforge.jwbf.mediawiki;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

@Singleton
class WireRegister extends Thread {

  private static final Logger log = LoggerFactory.getLogger(WireRegister.class);

  private Map<HttpAction, String> cache = Maps.newHashMap();

  public WireRegister() {
    Runtime.getRuntime().addShutdownHook(this);
  }

  public boolean hasContentFor(String url) {
    // TODO Auto-generated method stub
    log.debug("try to read cache for " + url);
    return false;
  }

  @Override
  public void run() {
    log.debug("store to file");
    // TODO Auto-generated method stub
  }

  @CheckForNull
  public String getResponse(HttpAction httpAction) {
    httpAction.getClass();
    // TODO Auto-generated method stub
    return null;
  }

  public String putResponse(HttpAction httpAction, String response) {
    // TODO Auto-generated method stub
    cache.put(httpAction, response);
    return response;
  }
}
