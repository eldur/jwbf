package net.sourceforge.jwbf.core.actions;

import static net.sourceforge.jwbf.core.actions.RequestBuilder.HashCodeEqualsMemoizingSupplier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;

import net.sourceforge.jwbf.GAssert;

public class RequestBuilderTest {

  @Test
  public void testBuild() {
    assertEquals(
        "a",
        new RequestBuilder("a") //
            .build());
    assertEquals(
        "/a?b=c",
        new RequestBuilder("/a") //
            .param("b", "c") //
            .build());
    assertEquals(
        "/a?b=c&b=e",
        new RequestBuilder("/a") //
            .param(null, (String) null) //
            .param("b", "e") //
            .param("b", "c") //
            .build());
    assertEquals(
        "/a?b=c&llun=None&q=None",
        new RequestBuilder("/a") //
            .param("llun", (String) null) //
            .param("b", "c") //
            .param("q", "") //
            .param("", "z") //
            .param("", Suppliers.ofInstance("t")) //
            .buildPost()
            .getRequest());
    assertEquals(
        "/a?b=1&d=e",
        new RequestBuilder("/a") //
            .param("d", "e") //
            .param(null, "null") //
            .param("b", 1) //
            .param("d", "e") //
            .buildGet()
            .getRequest());
    assertEquals(
        "/a?a=true&b=false",
        new RequestBuilder("/a") //
            .param("a", true) //
            .param("b", false) //
            .buildGet()
            .getRequest());
  }

  private String lazyVal = "AAA";

  @Test
  public void testLazyBuild() {
    // GIVEN
    Supplier<String> lazyParamValue =
        new Supplier<String>() {
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
    Supplier<String> lazyParamValue =
        new Supplier<String>() {
          @Override
          public String get() {
            return lazyValMemo;
          }
        };

    // WHEN
    Get result = new RequestBuilder("/a").param("a", lazyParamValue).buildGet();

    // assertEquals("/a?a=bbb UTF-8", result.toString()); // XXX fail

    lazyValMemo = "OOO";

    // THEN
    assertEquals("/a?a=OOO", result.getRequest());
    lazyValMemo = "bbb";
    assertEquals("/a?a=OOO", result.getRequest());
  }

  @Test
  public void testRegenerateNewBuilderGet() {
    // GIVEN
    Get get =
        RequestBuilder.of("/index.html") //
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
    Post post =
        RequestBuilder.of("/index.html") //
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
    Post post =
        RequestBuilder.of("/index.html") //
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

    assertEquals(
        "/index.html?a=b&c=e UTF-8 {c=[d]}", builder.param("c", "e").buildPost().toString());
  }

  @Test
  public void testRegenerateNewBuilderPostWithOtherParams() {
    // GIVEN
    Post post =
        RequestBuilder.of("/index.php") //
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
  public void testRegenerateNewBuilderWithEmptyParams() {
    // GIVEN
    Post post =
        RequestBuilder.of("/index.php") //
            .param("c", "") //
            .postParam("e", "") //
            .param("f") //
            .postParam("g") //
            .postParam(null, (String) null) //
            .postParam(null, "null") //
            .postParam("llun", (String) null) //
            .buildPost() //
        ;

    // WHEN
    RequestBuilder builder = post.toBuilder();
    Post newPost = builder.buildPost();

    // THEN
    assertEquals("/index.php?c=None&f= UTF-8 {e=[], g=[], llun=[]}", post.toString());
    assertEquals(post, newPost);
  }

  @Test
  public void testBuildPostWithParams() {
    // GIVEN
    Post post =
        RequestBuilder.of("/index.html") //
            .param("a", "b") //
            .buildPost() //
            .postParam("c", "d") //
        ;

    Post post2 =
        RequestBuilder.of("/index.html") //
            .param(new ParamTuple("a", "b")) //
            .postParam(new ParamTuple("c", "d")) //
            .buildPost() //
        ;

    // WHEN / THEN
    assertEquals(post, post2);
  }

  @Test
  @Ignore
  public void testBuildWithParamTuples() {
    // GIVEN
    ParamTuple<String> a = new ParamTuple("a", 4);
    ParamTuple b = new ParamTuple("c", 4);

    // WHEN
    Post post2 =
        RequestBuilder.of("/") //
            // .param(a) // TODO test for both
            .postParam(b) // TODO - " -
            .buildPost() //
        ;

    // THEN
    assertEquals("/", post2.getRequest());

    ImmutableMultimap<String, Object> params = post2.getParams();
    ImmutableMultimap<String, Object> expected = ImmutableMultimap.<String, Object>of("c", "4");
    GAssert.assertEquals(expected, params);
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

  @Test(expected = IllegalStateException.class)
  public void testMemoizer_compare_null() {
    // GIVEN
    Supplier<String> supplier = Suppliers.ofInstance("A");
    HashCodeEqualsMemoizingSupplier<String> a = new HashCodeEqualsMemoizingSupplier<>(supplier);

    // WHEN // THEN
    a.equals(null);
    fail();
  }
}
