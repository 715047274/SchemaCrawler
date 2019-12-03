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
package schemacrawler.test;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.*;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.Main;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class GrepCommandLineTest
{

  private static final String GREP_OUTPUT = "grep_output/";

  @Test
  public void grep(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    clean(GREP_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final String[][] grepArgs = new String[][] {
      new String[] {
        "--grep-columns=.*\\.STREET|.*\\.PRICE", },
      new String[] {
        "--grep-columns=.*\\..*NAME", },
      new String[] {
        "--grep-def=.*book authors.*", },
      new String[] {
        "--tables=", "--routines=.*", "--grep-parameters=.*\\.B_COUNT", },
      new String[] {
        "--tables=", "--routines=.*", "--grep-parameters=.*\\.B_OFFSET", },
      new String[] {
        "--grep-columns=.*\\.STREET|.*\\.PRICE",
        "--grep-def=.*book authors.*", }, };
    for (int i = 0; i < grepArgs.length; i++)
    {
      final String[] grepArgsForRun = grepArgs[i];

      final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.details;
      final InfoLevel infoLevel = InfoLevel.detailed;
      final Path additionalProperties = IOUtility
        .createTempFilePath("hsqldb.INFORMATION_SCHEMA.config", "properties");
      final Writer writer = newBufferedWriter(additionalProperties,
                                              UTF_8,
                                              WRITE,
                                              CREATE,
                                              TRUNCATE_EXISTING);
      final Properties properties = new Properties();
      properties.load(getClass().getResourceAsStream(
        "/hsqldb.INFORMATION_SCHEMA.config.properties"));
      properties.store(writer, getClass().getName());

      final String referenceFile = String.format("grep%02d.txt", i + 1);

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, "data");

      final OutputFormat outputFormat = TextOutputFormat.text;

      final List<String> args = new ArrayList<>(Arrays.asList(
        "--url=" + connectionInfo.getConnectionUrl(),
        "--user=sa",
        "--password=",
        "-g=" + additionalProperties.toString(),
        "--info-level=" + infoLevel,
        "--command=" + schemaTextDetailType,
        "--output-format=" + outputFormat.getFormat(),
        "--output-file=" + testOutputFile.toString(),
        "--no-info"));
      args.addAll(Arrays.asList(grepArgsForRun));

      Main.main(args.toArray(new String[args.size()]));

      failures.addAll(compareOutput(GREP_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.getFormat()));
    }

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }
}
