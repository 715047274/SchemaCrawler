/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package us.fatehi.utility.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static us.fatehi.utility.Utility.commonPrefix;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.isClassAvailable;
import static us.fatehi.utility.Utility.toSnakeCase;

import org.junit.jupiter.api.Test;

public class UtilityTest
{

  @Test
  public void commonPrefixTest()
  {
    assertThat(commonPrefix("preTest", null), is(""));
    assertThat(commonPrefix(null, "preCompile"), is(""));
    assertThat(commonPrefix("preTest", "preCompile"), is("pre"));
    assertThat(commonPrefix("something", "nothing"), is(""));
    assertThat(commonPrefix("preTest", ""), is(""));
  }

  @Test
  public void isBlankTest()
  {
    assertThat(isBlank(null), is(true));
    assertThat(isBlank(""), is(true));
    assertThat(isBlank(" "), is(true));
    assertThat(isBlank("   "), is(true));
    assertThat(isBlank("\t"), is(true));
    assertThat(isBlank("\n"), is(true));
    assertThat(isBlank("\r"), is(true));
    assertThat(isBlank(" \t "), is(true));
    assertThat(isBlank("\t\t"), is(true));

    assertThat(!isBlank("a"), is(true));
    assertThat(!isBlank("©"), is(true));
    assertThat(!isBlank(" a"), is(true));
    assertThat(!isBlank("a "), is(true));
    assertThat(!isBlank("a b"), is(true));
  }

  @Test
  public void isClassAvailableTest()
  {
    assertThat(isClassAvailable("java.lang.String"), is(true));
    assertThat(isClassAvailable("com.example.Unknown"), is(false));
  }

  @Test
  public void snakeCaseTest()
  {
    assertThat(toSnakeCase(null), nullValue());
    assertThat(toSnakeCase("a b"), equalTo("a b"));
    assertThat(toSnakeCase("ab"), equalTo("ab"));
    assertThat(toSnakeCase("abI"), equalTo("ab_i"));
    assertThat(toSnakeCase("Ab"), equalTo("_ab"));
    assertThat(toSnakeCase("abIj"), equalTo("ab_ij"));
    assertThat(toSnakeCase("ABC"), equalTo("_a_b_c"));
  }

}
