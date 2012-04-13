package net.sourceforge.jwbf.mediawiki;

import javax.annotation.CheckForNull;

import net.sourceforge.jwbf.core.actions.ContentProcessable;

class WireRegister {

  public boolean hasContentFor(String url) {
    // TODO Auto-generated method stub
    System.out.println("try to read cache for " + url);
    return false;
  }

  @CheckForNull
  public String getResponse(ContentProcessable a) {
    // TODO Auto-generated method stub
    return null;
  }

  public String putResponse(String performAction) {
    // TODO Auto-generated method stub
    return performAction;
  }

  @Override
  protected void finalize() throws Throwable {
    // TODO Auto-generated method stub
    System.out.println("store to file");
    super.finalize();
  }
}
