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
package net.sourceforge.jwbf.mediawiki.contentRep;

import javax.annotation.Nullable;
import java.util.Objects;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

public class CategoryItem {

  public static final Function<CategoryItem, String> TO_TITLE_STRING_F =
      new Function<CategoryItem, String>() {
        @Nullable
        @Override
        public String apply(@Nullable CategoryItem input) {
          return input.getTitle();

        }
      };

  private final String title;
  private final int namespace;
  private final int pageid;

  public CategoryItem(String title, int namespace, int pageid) {
    this.title = Preconditions.checkNotNull(title);
    this.namespace = namespace;
    this.pageid = pageid;
  }

  @Override
  public String toString() {
    return com.google.common.base.Objects.toStringHelper(this) //
        .add("title", title) //
        .add("namespace", namespace) //
        .add("pageid", pageid) //
        .toString();
  }

  public String getTitle() {
    return title;
  }

  public int getNamespace() {
    return namespace;
  }

  public int getPageid() {
    return pageid;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CategoryItem) {
      CategoryItem that = (CategoryItem) obj;
      return Objects.equals(that.getTitle(), this.getTitle()) && //
          Objects.equals(that.getPageid(), this.getPageid()) && //
          Objects.equals(that.getNamespace(), this.getNamespace());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, pageid, namespace);
  }
}
