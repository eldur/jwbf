package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AllUsersTest {

    @Mock
    private MediaWikiBot bot;

    private AllUsers testee;

    @Before
    public void before() {
        boolean editsOnly = true;
        boolean activeOnly = false;
        testee = new AllUsers(bot, editsOnly, activeOnly);
    }

    @Test
    public void testGenerateRequest() {
        // WHEN
        Get allPagesRequest = (Get) testee.prepareNextRequest();

        // THEN
        assertEquals(
                "/api.php?action=query&aulimit=50&auwitheditsonly=true&format=xml&list=allusers", //
                allPagesRequest.getRequest());
    }

    @Test
    public void testParseEmptyList() {
        // GIVEN / WHEN
        ImmutableList<String> result = testee.parseElements(BaseQueryTest.emptyXml());

        // THEN
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void testParseUserList() {
        // GIVEN / WHEN
        String response = TestHelper.anyWikiResponse("allusers.xml");
        ImmutableList<String> result = testee.parseElements(response);
        ImmutableList<String> expected = ImmutableList.of("Y0su", "YKK", "YNWAGT", "YP977");

        // THEN
        assertTrue(result.equals(expected));
        assertTrue(result.size() == 4);
    }

}
