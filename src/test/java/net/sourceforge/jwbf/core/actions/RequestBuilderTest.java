package net.sourceforge.jwbf.core.actions;

import static net.sourceforge.jwbf.core.actions.RequestBuilder.HashCodeEqualsMemoizingSupplier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.junit.Test;

public class RequestBuilderTest {

  @Test
  public void testBuild() {
    assertEquals("a", new RequestBuilder("a") //
        .build());
    assertEquals("/a?b=c", new RequestBuilder("/a") //
        .param("b", "c") //
        .build());
    assertEquals("/a?b=c&b=e", new RequestBuilder("/a") //
        .param("b", "e") //
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

  @Test
  public void testRegenerateNewBuilderGet() {
    // GIVEN
    Get get = RequestBuilder.of("/index.html") //
        .param("a", "b") //
        .buildGet();

    // WHEN
    RequestBuilder builder = get.toBuilder();
    Get newGet = builder.buildGet();

    // THEN
    assertEquals("/index.html?a=b UTF-8", get.toString());
    assertEquals(get, newGet);
  }

  @Test
  public void testRegenerateNewBuilderPost() {
    // GIVEN
    Post post = RequestBuilder.of("/index.html") //
        .param("a", "b") //
        .buildPost();

    // WHEN
    RequestBuilder builder = post.toBuilder();
    Post newPost = builder.buildPost();

    // THEN
    assertEquals(post, newPost);
  }

  @Test
  public void testRegenerateNewBuilderPostWithParams() {
    // GIVEN
    Post post = RequestBuilder.of("/index.html") //
        .param("a", "b") //
        .postParam("c", "d") //
        .buildPost() //
        ;

    // WHEN
    RequestBuilder builder = post.toBuilder();
    Post newPost = builder.buildPost();

    // THEN
    assertEquals("/index.html?a=b UTF-8 {c=[d]}", post.toString());
    assertEquals(post, newPost);

    assertEquals("/index.html?a=b&c=e UTF-8 {c=[d]}",
        builder.param("c", "e").buildPost().toString());
  }

  @Test
  public void testRegenerateNewBuilderPostWithOtherParams() {
    // GIVEN
    Post post = RequestBuilder.of("/index.php") //
        .param("c", "d") //
        .postParam("e", "f") //
        .buildPost() //
        ;

    // WHEN
    RequestBuilder builder = post.toBuilder();
    Post newPost = builder.buildPost();

    // THEN
    assertEquals("/index.php?c=d UTF-8 {e=[f]}", post.toString());
    assertEquals(post, newPost);
  }

  @Test
  public void testBuildPostWithParams() {
    // GIVEN
    Post post = RequestBuilder.of("/index.html") //
        .param("a", "b") //
        .buildPost() //
        .postParam("c", "d") //
        ;

    Post post2 = RequestBuilder.of("/index.html") //
        .param("a", "b") //
        .postParam("c", "d") //
        .buildPost() //
        ;

    // WHEN / THEN
    assertEquals(post, post2);
  }

  @Test(expected = IllegalStateException.class)
  public void testMemoizer_only_compare_with_same_type() {
    // GIVEN
    Supplier<String> supplier = Suppliers.ofInstance("A");
    HashCodeEqualsMemoizingSupplier<String> a = new HashCodeEqualsMemoizingSupplier<>(supplier);

    // WHEN // THEN
    a.equals(new Object());
    fail();
  }

}
