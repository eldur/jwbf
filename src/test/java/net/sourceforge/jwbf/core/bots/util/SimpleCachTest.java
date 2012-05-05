package net.sourceforge.jwbf.core.bots.util;

import lombok.Delegate;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class SimpleCachTest {

  
  @Test
  public void workWithCache() {
    WikiBot cache = new CacheBot();
    SimpleArticle sa = cache.readData("a", 0);
    // TODO read without cache should be slower; try a timeout rule
  }

  
  private static class CacheBot implements WikiBot {
    private Cache<String, SimpleArticle> mapCache = CacheBuilder.newBuilder().build();
  
    private interface A {
      SimpleArticle readData(final String name, final int properties);
    }
    
    @Delegate(excludes = A.class)
    private WikiBot bot = Mockito.mock(WikiBot.class);
    
    public CacheBot() {

      Answer<SimpleArticle> answer = new Answer<SimpleArticle>() {
        public SimpleArticle answer(InvocationOnMock iom) {
        
          return new SimpleArticle();
        }  
        
      };   
     Mockito.when(bot.readData(Mockito.anyString(), Mockito.anyInt())).thenAnswer(answer);
    }
 
    public SimpleArticle readData(final String name, final int properties) {
      SimpleArticle cache = mapCache.getIfPresent(name + properties);
      if ( cache != null) {
        return cache;
      }
      SimpleArticle sa = bot.readData(name, properties);
      mapCache.put(name + properties, sa);
      return sa;
    }

    
  }
}
