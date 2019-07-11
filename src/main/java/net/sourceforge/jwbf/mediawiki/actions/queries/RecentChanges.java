package net.sourceforge.jwbf.mediawiki.actions.queries;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.RecentChange;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

/**
 * Gets a list of pages recently changed, ordered by modification timestamp. Parameters: rcfrom
 * (paging timestamp), rcto (flt), rcnamespace (flt), rcminor (flt), rcusertype (dflt=not|bot),
 * rcdirection (dflt=older), rclimit (dflt=10, max=500/5000) F api.php ? action=query &amp;
 * list=recentchanges - List last 10 changes
 */
public class RecentChanges extends BaseQuery<RecentChange> {

  /**
   * value for the bllimit-parameter. *
   */
  private static final int LIMIT = 50;

  private final MediaWikiBot bot;

  private final int[] namespaces;

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param namespace the namespace(s) that will be searched for links, as a string of numbers
   *                  separated by '|'; if null, this parameter is omitted
   * @param rcstart   timestamp
   */
  private HttpAction generateRequest(int[] namespace, String rcstart) {

    RequestBuilder requestBuilder =
        new ApiRequestBuilder() //
            .action("query") //
            .formatXml() //
            .param("list", "recentchanges") //
            .param("rclimit", LIMIT) //
            .param("rcprop", "user%7Cuserid%7Ccomment%7Cflags%7Ctimestamp%7C" + //
                "title%7Cids%7Csizes%7Cflags") //
        ;
    if (namespace != null) {
      requestBuilder.param("rcnamespace", MediaWiki.urlEncode(MWAction.createNsString(namespace)));
    }
    if (rcstart.length() > 0) {
      requestBuilder.param("rcstart", rcstart);
    }

    return requestBuilder.buildGet();
  }

  private HttpAction generateRequest(int[] namespace) {
    return generateRequest(namespace, "");
  }

  /**
   *
   */
  public RecentChanges(MediaWikiBot bot, int... ns) {
    super(bot);
    namespaces = ns;
    this.bot = bot;
  }

  /**
   *
   */
  public RecentChanges(MediaWikiBot bot) {
    this(bot, MediaWiki.NS_ALL);
  }

  /**
   * picks the article name from a MediaWiki api response.
   *
   * @param s text for parsing
   */
  @Override
  protected ImmutableList<RecentChange> parseElements(String s) {
    XmlElement root = XmlConverter.getRootElement(s);
    List<RecentChange> recentChanges = Lists.newArrayList();
    findContent(root, recentChanges);
    return ImmutableList.copyOf(recentChanges);
  }

  private void findContent(XmlElement root, List<RecentChange> recentChanges) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    for (XmlElement xmlElement : root.getChildren()) {
      if (xmlElement.getQualifiedName().equalsIgnoreCase("rc")) {
        Date timestamp = null;
        try {
          timestamp = dateFormat.parse(xmlElement.getAttributeValue("timestamp"));
        } catch (ParseException e) {
          e.printStackTrace();
        }
        RecentChange change = RecentChange.builder()
            .type(RecentChange.ChangeType.parse(xmlElement.getAttributeValue("type")))
            .namespace(toInt(xmlElement.getAttributeValue("ns")))
            .title(xmlElement.getAttributeValue("title"))
            .pageId(toInt(xmlElement.getAttributeValue("pageid")))
            .revisionId(toLong(xmlElement.getAttributeValue("revid")))
            .oldRevisionId(toLong(xmlElement.getAttributeValue("old_revid")))
            .oldRevisionId(toLong(xmlElement.getAttributeValue("old_revid")))
            .rcId(toLong(xmlElement.getAttributeValue("rcid")))
            .user(xmlElement.getAttributeValue("user"))
            .userId(toLong(xmlElement.getAttributeValue("userid")))
            .oldLength(toLong(xmlElement.getAttributeValue("oldlen")))
            .newLength(toLong(xmlElement.getAttributeValue("newlen")))
            .timestamp(timestamp)
            .comment(xmlElement.getAttributeValue("comment"))
            .build();
        recentChanges.add(change);
        setNextPageInfo(xmlElement.getAttributeValue("timestamp"));
      } else {
        findContent(xmlElement, recentChanges);
      }
    }
  }

  @Override
  protected HttpAction prepareNextRequest() {
    if (hasNextPageInfo()) {
      return generateRequest(namespaces, getNextPageInfo());
    } else {
      return generateRequest(namespaces);
    }
  }

  @Override
  protected Iterator<RecentChange> copy() {
    return new RecentChanges(bot, namespaces);
  }

  @Override
  protected Optional<String> parseHasMore(String s) {
    return Optional.absent();
  }
}
