package schemacrawler.tools.options;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.getFileExtension;
import static sf.util.Utility.isBlank;

import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.tools.iosource.ConsoleOutputResource;
import schemacrawler.tools.iosource.FileOutputResource;
import schemacrawler.tools.iosource.OutputResource;
import schemacrawler.tools.iosource.WriterOutputResource;

public final class OutputOptionsBuilder
  implements OptionsBuilder<OutputOptionsBuilder, OutputOptions>
{

  private static final String SC_INPUT_ENCODING =
    "schemacrawler.encoding.input";
  private static final String SC_OUTPUT_ENCODING =
    "schemacrawler.encoding.output";

  public static OutputOptionsBuilder builder()
  {
    return new OutputOptionsBuilder();
  }

  public static OutputOptionsBuilder builder(final OutputOptions outputOptions)
  {
    return new OutputOptionsBuilder().fromOptions(outputOptions);
  }

  public static OutputOptions newOutputOptions()
  {
    return new OutputOptionsBuilder().toOptions();
  }

  public static OutputOptions newOutputOptions(final OutputFormat outputFormat,
                                               final Path outputFile)
  {
    return OutputOptionsBuilder
      .builder()
      .withOutputFormat(outputFormat)
      .withOutputFile(outputFile)
      .toOptions();
  }

  public static OutputOptions newOutputOptions(final String outputFormatValue,
                                               final Path outputFile)
  {
    return OutputOptionsBuilder
      .builder()
      .withOutputFormatValue(outputFormatValue)
      .withOutputFile(outputFile)
      .toOptions();
  }

  private OutputResource outputResource;
  private String outputFormatValue;
  private Charset inputEncodingCharset;
  private Charset outputEncodingCharset;
  private String title;

  private OutputOptionsBuilder()
  {
    // Default values are set at the time of building options
    // All values are set to null, and corrected at the time of
    // converting to options
  }

  public OutputOptionsBuilder title(final String title)
  {
    if (isBlank(title))
    {
      this.title = "";
    }
    else
    {
      this.title = title;
    }
    return this;
  }

  @Override
  public OutputOptionsBuilder fromConfig(final Config config)
  {
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = config;
    }

    withInputEncoding(configProperties.getStringValue(SC_INPUT_ENCODING,
                                                      UTF_8.name())).withOutputEncoding(
      configProperties.getStringValue(SC_OUTPUT_ENCODING, UTF_8.name()));

    return this;
  }

  @Override
  public OutputOptionsBuilder fromOptions(final OutputOptions options)
  {
    if (options == null)
    {
      return this;
    }

    withInputEncoding(options.getInputCharset())
      .withOutputEncoding(options.getOutputCharset())
      .withOutputFormatValue(options.getOutputFormatValue())
      .title(options.getTitle());
    outputResource = options.getOutputResource();

    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = new Config();
    config.setStringValue(SC_INPUT_ENCODING, inputEncodingCharset.name());
    config.setStringValue(SC_OUTPUT_ENCODING, outputEncodingCharset.name());
    return config;
  }

  @Override
  public OutputOptions toOptions()
  {
    withInputEncoding(inputEncodingCharset);
    withOutputResource(outputResource);
    withOutputEncoding(inputEncodingCharset);

    // If there is an output format specified, use it
    // Otherwise, infer the output format from the extension of the file
    // Otherwise, assume text output
    if (isBlank(outputFormatValue))
    {
      final String fileExtension;
      if (outputResource instanceof FileOutputResource)
      {
        fileExtension =
          getFileExtension(((FileOutputResource) outputResource).getOutputFile());
      }
      else
      {
        fileExtension = null;
      }

      outputFormatValue = isBlank(fileExtension)?
                          TextOutputFormat.text.getFormat():
                          fileExtension;
    }

    if (isBlank(title))
    {
      title = "";
    }

    return new OutputOptions(inputEncodingCharset,
                             outputResource,
                             outputEncodingCharset,
                             outputFormatValue,
                             title);
  }

  public OutputOptionsBuilder withConsoleOutput()
  {
    outputResource = new ConsoleOutputResource();
    return this;
  }

  public OutputOptionsBuilder withInputEncoding(final Charset inputCharset)
  {
    if (inputCharset == null)
    {
      inputEncodingCharset = UTF_8;
    }
    else
    {
      inputEncodingCharset = inputCharset;
    }
    return this;
  }

  /**
   * Set character encoding for input files, such as scripts and templates.
   *
   * @param inputEncoding
   *   Input encoding
   * @return Builder
   */
  public OutputOptionsBuilder withInputEncoding(final String inputEncoding)
  {
    try
    {
      inputEncodingCharset = Charset.forName(inputEncoding);
    }
    catch (final IllegalArgumentException e)
    {
      inputEncodingCharset = UTF_8;
    }
    return this;
  }

  public OutputOptionsBuilder withOutputEncoding(final Charset outputCharset)
  {
    if (outputCharset == null)
    {
      outputEncodingCharset = UTF_8;
    }
    else
    {
      outputEncodingCharset = outputCharset;
    }
    return this;
  }

  /**
   * Set character encoding for output files.
   *
   * @param outputEncoding
   *   Output encoding
   * @return Builder
   */
  public OutputOptionsBuilder withOutputEncoding(final String outputEncoding)
  {
    try
    {
      outputEncodingCharset = Charset.forName(outputEncoding);
    }
    catch (final IllegalArgumentException e)
    {
      outputEncodingCharset = UTF_8;
    }
    return this;
  }

  /**
   * Sets the name of the output file. It is important to note that the output
   * encoding should be available at this point.
   *
   * @param outputFile
   *   Output path.
   * @return Builder
   */
  public OutputOptionsBuilder withOutputFile(final Path outputFile)
  {
    requireNonNull(outputFile, "No output file provided");
    outputResource = new FileOutputResource(outputFile);
    return this;
  }

  /**
   * Sets output format.
   *
   * @param outputFormat
   *   Output format
   * @return Builder
   */
  public OutputOptionsBuilder withOutputFormat(final OutputFormat outputFormat)
  {
    outputFormatValue =
      requireNonNull(outputFormat, "No output format provided").getFormat();
    return this;
  }

  /**
   * Sets output format value.
   *
   * @param outputFormatValue
   *   Output format value
   * @return Builder
   */
  public OutputOptionsBuilder withOutputFormatValue(final String outputFormatValue)
  {
    this.outputFormatValue =
      requireNonNull(outputFormatValue, "No output format value provided");
    return this;
  }

  public OutputOptionsBuilder withOutputResource(final OutputResource outputResource)
  {
    if (outputResource == null)
    {
      if (outputFormatValue == null || TextOutputFormat.text
        .name()
        .equals(outputFormatValue))
      {
        this.outputResource = new ConsoleOutputResource();
      }
      else
      {
        final String extension;
        if ("htmlx".equals(outputFormatValue))
        {
          // Tacky hack for htmlx format
          extension = "svg.html";
        }
        else if (outputFormatValue.matches("[A-Za-z]+"))
        {
          extension = outputFormatValue;
        }
        else
        {
          extension = "out";
        }

        final Path randomOutputFile = Paths
          .get(".",
               String.format("schemacrawler-%s.%s",
                             UUID.randomUUID(),
                             extension))
          .normalize()
          .toAbsolutePath();
        this.outputResource = new FileOutputResource(randomOutputFile);
      }
    }
    else
    {
      this.outputResource = outputResource;
    }
    return this;
  }

  public OutputOptionsBuilder withOutputWriter(final Writer writer)
  {
    requireNonNull(writer, "No output writer provided");
    outputResource = new WriterOutputResource(writer);
    return this;
  }

}
