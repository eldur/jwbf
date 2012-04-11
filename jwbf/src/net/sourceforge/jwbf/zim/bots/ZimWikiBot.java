/*
 * Copyright 2009 Martin Koch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors:
 *
 */

package net.sourceforge.jwbf.zim.bots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.bots.util.CacheHandler;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
/**
 *
 * @author Martin Koch
 *
 */
public class ZimWikiBot implements WikiBot {
  private static final String ZIMEXT = ".txt";
  private static final int DEFAULT = 0;
  private final File rootFolder;
  //	private final String mwFolder;

  /**
   * Constructor for a ZIM wiki-bot.
   * @param zimRootFolder this is the folder on your local machine
   *
   */

  public ZimWikiBot(String zimRootFolder) {
    // specify the path to all zim files
    this(new File(zimRootFolder));
  }


  public ZimWikiBot(File rootFolder) {
    // specify the path to all zim files
    this.rootFolder = rootFolder;
  }
  public void login(String user, String passwd) throws ActionException {
    throw new ActionException(
    "login is not supported because this is a desktopwiki");

  }

  public void delete(String title) throws ActionException,
  ProcessException {
    // TODO Auto-generated method stub

  }

  public Article getArticle(String title) throws ActionException,
  ProcessException {

    return getArticle(title, 0); // FIXME add regular constants
  }

  public Article getArticle(String title, int properties)
  throws ActionException, ProcessException {
    return new Article(this, readData(title, properties));
  }


  /**
   * Set up a simple text paarser
   * some simple formating routines are supplied
   * -> bold letters and images are translated from
   * zimWiki to mediaWiki
   */
  public SimpleArticle readData(String name, int properties)
  throws ActionException, ProcessException {
    File f = new File(getRootFolder(), name + ZIMEXT);
    SimpleArticle sa = new SimpleArticle();
    sa.setTitle(name);
    StringBuffer text = new StringBuffer();
    // create a file reader
    BufferedReader myInput = null;
    try {
      myInput = new BufferedReader(new FileReader(f));

      String line = "";
      String cont = "";

      // if we are reading content, than
      while ((line = myInput.readLine()) != null) {

        // omit the headline
        if (line.startsWith("====== " + name + " ======")) {

          // store every line in 'text' and add a newline
          while ((cont = myInput.readLine()) != null) {

            // zim encapsulates bold letters with **
            // media wiki encapsulates bold letters with '''
            cont = cont.replace("**", "'''");

            // images are written in zim:
            // {{../MatlabSVM_01.png?width=400}}
            // in media wiki:
            // [[MatlabSVM_01.png|45px|none|MatlabSVM_01]]
            cont = cont.replace("{{../", "[[Image:");
            cont = cont.replace("?width=", "|");
            cont = cont.replace("}}", "|none| " + name + "]]");
            text.append( cont + "\n");
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (myInput != null)
          myInput.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    sa.setText(text.toString());
    return sa;
  }

  public void writeContent(SimpleArticle sa) throws ActionException,
  ProcessException {
    // TODO Auto-generated method stub

  }

  public File getRootFolder() {
    return rootFolder;
  }

  public Userinfo getUserinfo() throws ActionException, ProcessException {
    return new Userinfo() {

      public String getUsername() {
        return System.getProperty("user.name");
      }

      public Set<String> getRights() {
        Set<String> v = new HashSet<String>();
        if (rootFolder.canRead()) {
          v.add("read");
        }
        if (rootFolder.canWrite()) {
          v.add("write");
        }
        return v;
      }

      public Set<String> getGroups() {
        return new HashSet<String>();
      }
    };
  }


  public String getWikiType() {
    return "Zim";
  }


  public SimpleArticle readData(String name) throws ActionException,
  ProcessException {
    return readData(name, DEFAULT);
  }


  public boolean hasCacheHandler() {
    // TODO Auto-generated method stub
    return false;
  }


  public void setCacheHandler(CacheHandler ch) {
    // TODO Auto-generated method stub

  }

  //	public String getMWFolder() {
  //		return mwFolder;
  //	}


}
