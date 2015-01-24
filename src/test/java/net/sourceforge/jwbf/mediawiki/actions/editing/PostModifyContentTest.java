package net.sourceforge.jwbf.mediawiki.actions.editing;

import static com.google.common.collect.ImmutableSet.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import net.sourceforge.jwbf.core.actions.ParamTuple;
import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PostModifyContentTest {

    private static final String editFailMsg = "editing is not allowed";
    private PostModifyContent testee;
    private MediaWikiBot bot;
    private Userinfo userinfo;
    private static final ImmutableSet<String> rights = of(
            Userinfo.RIGHT_WRITEAPI, Userinfo.RIGHT_EDIT);
    private SimpleArticle simpleArticle;

    @Before
    public void before() {
        bot = mock(MediaWikiBot.class);
        when(bot.getVersion()).thenReturn(Version.DEVELOPMENT);
        userinfo = mock(Userinfo.class);
        when(bot.getUserinfo()).thenReturn(userinfo);
        simpleArticle = new SimpleArticle();
        simpleArticle.setTitle("Test");
        testee = new PostModifyContent(bot, simpleArticle) {
            @Override
            GetApiToken newTokenRequest() {
                GetApiToken mockToken = mock(GetApiToken.class);
                GetApiToken.TokenResponse tokenResponse = mock(GetApiToken.TokenResponse.class);
                when(tokenResponse.token()).thenReturn(
                        new ParamTuple("token", "!testToken"));
                when(mockToken.get()).thenReturn(tokenResponse);
                return mockToken;
            }
        };
    }

    @Test
    public void testProcessReturningText() {
        testee.processAllReturningText("error");
    }

    @Test
    public void testIsIntersectionEmpty() {
        assertTrue(testee.isIntersectionEmpty(null, null));
        Set<String> a = Sets.newHashSet();
        Set<String> b = Sets.newHashSet();
        assertFalse(testee.isIntersectionEmpty(a, null));
        assertFalse(testee.isIntersectionEmpty(null, b));
        assertTrue(a.containsAll(b));
        assertTrue(testee.isIntersectionEmpty(a, b));
        assertTrue(testee.isIntersectionEmpty(b, a));

        b.add("a");
        assertTrue(testee.isIntersectionEmpty(a, b));
        b.add("c");
        assertTrue(testee.isIntersectionEmpty(a, b));
        assertTrue(testee.isIntersectionEmpty(b, a));
        a.add("a");
        a.add("b");

        assertFalse(testee.isIntersectionEmpty(a, b));
        assertFalse(testee.isIntersectionEmpty(b, a));
        assertTrue(a.size() > 1);
        assertTrue(b.size() > 1);
    }

    @Test
    public void testGetNextMessageMinorEdit() {
        simpleArticle.setMinorEdit(true);
        ImmutableMap<String, Object> params = getParams();
        assertEquals("{summary=, text=, minor=, token=!testToken}",
                params.toString());
    }

    @Test
    public void testGetNextMessageNoMinorEdit() {
        ImmutableMap<String, Object> params = getParams();
        assertEquals("{summary=, text=, notminor=, token=!testToken}",
                params.toString());
    }

    @Test
    public void testGetNextMessageBotEdit() {
        when(userinfo.getGroups()).thenReturn(of("bot", "user"));
        ImmutableMap<String, Object> params = getParams();
        assertEquals(
                "{summary=, text=, bot=, notminor=, token=!testToken}",
                params.toString());
    }

    private ImmutableMap<String, Object> getParams() {
        when(userinfo.getRights()).thenReturn(rights);
        testee.getNextMessage();
        Post message = (Post) testee.getNextMessage();
        ImmutableMap<String, Object> params = message.getParams();
        return params;
    }

    @Test
    public void testGetNextMessageFailConsumeMessages() {
        when(userinfo.getRights()).thenReturn(rights);
        testee.getNextMessage();
        testee.getNextMessage();
        try {
            testee.getNextMessage();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("this action has only two messages", e.getMessage());
        }
    }

    @Test
    public void testGetNextMessageFailNoEditing() {
        try {
            testee.getNextMessage();
            fail();
        } catch (VersionException e) {
            assertEquals(editFailMsg, e.getMessage());
        }
    }

    @Test
    public void testGetNextMessageFailNoEditingRights() {
        when(userinfo.getRights()).thenReturn(of(Userinfo.RIGHT_EDIT));
        try {
            testee.getNextMessage();
            fail();
        } catch (VersionException e) {
            assertEquals(editFailMsg, e.getMessage());
        }
    }

    @Test
    public void testGetNextMessageFailNoEditingRightsApi() {
        when(userinfo.getRights()).thenReturn(of(Userinfo.RIGHT_WRITEAPI));
        try {
            testee.getNextMessage();
            fail();
        } catch (VersionException e) {
            assertEquals(editFailMsg, e.getMessage());
        }
    }
}
