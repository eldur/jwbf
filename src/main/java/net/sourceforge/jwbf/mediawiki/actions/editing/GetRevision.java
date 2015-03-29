/*
 * Copyright 2007 Thomas Stock.
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
package net.sourceforge.jwbf.mediawiki.actions.editing;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mapper.JsonMapper;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads the content of a given article.
 *
 * @author Thomas Stock
 */
public class GetRevision extends MWAction {

  private static final Logger log = LoggerFactory.getLogger(GetRevision.class);

  private List<SimpleArticle> articles = Lists.newArrayList();
  private List<Optional<SimpleArticle>> articlesOpt = Lists.newArrayList();
  private final ImmutableList<String> names;

  public static final int CONTENT = 1 << 1;
  public static final int TIMESTAMP = 1 << 2;
  public static final int USER = 1 << 3;
  public static final int COMMENT = 1 << 4;
  public static final int IDS = 1 << 5;
  public static final int FLAGS = 1 << 6;

  public static final int FIRST = 1 << 30;
  public static final int LAST = 1 << 31;

  private final int properties;

  private final Get msg;

  private JsonMapper mapper = new JsonMapper();

  /**
   * TODO follow redirects. TODO change constructor field ordering; bot
   */
  public GetRevision(Version v, String articlename, int properties) {
    this(ImmutableList.of(articlename), properties);
  }

  public GetRevision(ImmutableList<String> names, int properties) {
    this.properties = properties;
    this.names = names;
    // TODO continue=-||
    msg = new ApiRequestBuilder() //
        .action("query") //
        .formatJson() //
        .param("prop", "revisions") //
        .param("titles", MediaWiki.urlEncode(MediaWiki.pipeJoined(names))) //
        .param("rvprop", getDataProperties(properties) + getReversion(properties)) //
            //.param("rvlimit", "1") TODO: when passing multiples pages we can't pass rvlimit
        .buildGet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processReturningText(final String s, HttpAction ha) {
    if (msg.getRequest().equals(ha.getRequest())) {
      parse(s);
    }
    return "";
  }

  @VisibleForTesting
  static String getDataProperties(final int property) {
    ImmutableList.Builder<String> builder = ImmutableList.builder();

    if (hasMarker(property, CONTENT)) {
      builder.add("content");
    }
    if (hasMarker(property, COMMENT)) {
      builder.add("comment");
    }
    if (hasMarker(property, TIMESTAMP)) {
      builder.add("timestamp");
    }
    if (hasMarker(property, USER)) {
      builder.add("user");
    }
    if (hasMarker(property, IDS)) {
      builder.add("ids");
    }
    if (hasMarker(property, FLAGS)) {
      builder.add("flags");
    }

    return MediaWiki.urlEncode(MediaWiki.pipeJoined(builder.build()));
  }

  private static boolean hasMarker(int property, int marker) {
    return (property & marker) > 0;
  }

  private String getReversion(final int property) {
    if (hasMarker(property, FIRST)) {
      return "&rvdir=newer";
    } else {
      return "&rvdir=older";
    }
  }

  private void parse(final String json) {
    JsonNode node = mapper.toJsonNode(json);
    JsonNode pages = node.path("query").path("pages");
    if (!node.isMissingNode()) {
      for (JsonNode page : pages) {
        SimpleArticle sa = new SimpleArticle();
        sa.setTitle(page.get("title").asText());
        if (!page.has("missing")) {
          sa.setPageId(page.get("pageid").asInt());
          JsonNode rev = page.path("revisions");
          rev = rev.get(0);
          sa.setText(rev.get("*").asText());
          sa.setRevisionId(Optional.of(rev.path("revid").asText()).or(""));
          sa.setEditSummary(Optional.of(rev.path("comment").asText()).or(""));
          sa.setEditor(Optional.of(rev.path("user").asText()).or(""));
          if (hasMarker(properties, TIMESTAMP)) {
            sa.setEditTimestamp(Optional.of(rev.path("timestamp").asText()).or(""));
          }
          if (hasMarker(properties, FLAGS)) {
            if (rev.has("minor")) {
              sa.setMinorEdit(true);
            } else {
              sa.setMinorEdit(false);
            }
          }
          articlesOpt.add(Optional.of(sa));
        } else {
          log.warn("Article '{}' is missing", sa.getTitle());
          articlesOpt.add(Optional.<SimpleArticle>absent());
        }
        articles.add(sa);
      }
    }
  }

  public SimpleArticle getArticle() {
    return Iterables.getOnlyElement(asList());
  }

  public ImmutableList<Optional<SimpleArticle>> asListOpt() {
    return ImmutableList.copyOf(articlesOpt);
  }

  public Optional<SimpleArticle> getArticleOpt() {
    return Iterables.getOnlyElement(asListOpt());
  }

  public ImmutableList<SimpleArticle> asList() {
    return ImmutableList.copyOf(articles);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }

}
