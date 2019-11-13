/*
 * Copyright 2016 Marco Ammon <ammon.marco@t-online.de>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.jwbf.mediawiki.actions.editing;


import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PostPurgeTest {

  @Mock
  private Userinfo userinfo;

  private PostPurge testee;

  @Before
  public void before() {
    Set<String> setWithPurge = ImmutableSet.of("purge");
    when(userinfo.getRights()).thenReturn(setWithPurge);
    String[] titlesToPurge = {"Main Page","Nonexistent","Talk:"};
    testee = Mockito.spy(new PostPurge(userinfo, titlesToPurge));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseXml_iae() {
    // GIVEN
    String xml = null;

    // WHEN
    testee.parseXml(xml);

  }

  @Test
  public void testParseXml() {
    // GIVEN
    String xml = TestHelper.anyWikiResponse("purge.xml");

    // WHEN
    testee.parseXml(xml);

    // THEN
    Mockito.verify(testee).logPurge("Main Page", testee.RESULT_PURGED);
    Mockito.verify(testee).logPurge("Nonexistent", testee.RESULT_MISSING);
    Mockito.verify(testee).logPurge("Talk:", testee.RESULT_INVALID);

  }
}
