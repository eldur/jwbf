package net.sourceforge.jwbf.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.sourceforge.jwbf.core.actions.Get;
import org.junit.Test;

public class RequestBuilderTest {

  @Test
  public void testBuild() {
    assertEquals("a", new RequestBuilder("a") //
        .build());
    assertEquals("/a?b=c", new RequestBuilder("/a") //
        .param("b", "c") //
        .build());
    assertEquals("/a?b=c", new RequestBuilder("/a") //
        .param("b", "c") //
        .param("b", "c") //
        .build());
    assertEquals("/a?b=c&q=None", new RequestBuilder("/a") //
        .param("b", "c") //
        .param("q", "") //
        .param("", "z") //
        .param("", Suppliers.ofInstance("t")) //
        .buildPost().getRequest());
    assertEquals("/a?b=1&d=e", new RequestBuilder("/a") //
        .param("d", "e") //
        .param("b", 1) //
        .param("d", "e") //
        .buildGet().getRequest());
  }

  private String lazyVal = "AAA";

  @Test
  public void testLazyBuild() {
    // GIVEN
    Supplier<String> lazyParamValue = new Supplier<String>() {
      @Override
      public String get() {
        return lazyVal;
      }
    };

    // WHEN
    Get result = new RequestBuilder("/a").param("a", lazyParamValue).buildGet();
    lazyVal = "OOO";

    // THEN
    assertEquals("/a?a=OOO", result.getRequest());
  }

  private String lazyValMemo = "bbb";

  @Test
  public void testLazyBuildMemorize() {
    // GIVEN
    Supplier<String> lazyParamValue = new Supplier<String>() {
      @Override
      public String get() {
        return lazyValMemo;
      }
    };

    // WHEN
    Get result = new RequestBuilder("/a").param("a", lazyParamValue).buildGet();
    lazyValMemo = "OOO";

    // THEN
    assertEquals("/a?a=OOO", result.getRequest());
    lazyValMemo = "bbb";
    assertEquals("/a?a=OOO", result.getRequest());
  }

  @Test(expected = IllegalStateException.class)
  public void testMemoizer_only_compare_with_same_type() {
    // GIVEN
    RequestBuilder.HashCodeEqualsMemoizingSupplier<String> a = new RequestBuilder.HashCodeEqualsMemoizingSupplier<>(Suppliers.ofInstance("A"));

    // WHEN // THEN
    a.equals(new Object());
    fail();
  }

}
