package org.apache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.ContentHandlerFactory;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.xml.sax.SAXException;

public class Main {

  public static final String TAR_FILE_ARCHIVED_BY_7z = "7ztar.tar";
  public static final String TAR_FILE_ARCHIVED_BY_BASH = "bash_linux.tar";

  public static void main(String[] args) throws TikaException, SAXException, IOException {
    // first time it's work perfectly and metadata is extracted successful
    String bashTar = Main.class.getClassLoader().getResource(TAR_FILE_ARCHIVED_BY_BASH).getFile();
    recursiveParserWrapperExample(bashTar);

    // for .tar which was archived by 7z we will get an exception
    // TikaException: TIKA-198: Illegal IOException from
    // org.apache.tika.parser.pkg.PackageParser@4d0f2471
    String sevenZtar = Main.class.getClassLoader().getResource(TAR_FILE_ARCHIVED_BY_7z).getFile();
    recursiveParserWrapperExample(sevenZtar);
  }

  public static void recursiveParserWrapperExample(String filePath)
      throws IOException, SAXException, TikaException {

    AutoDetectParser p = new AutoDetectParser();
    ContentHandlerFactory factory =
        new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.IGNORE, 0);

    RecursiveParserWrapper wrapper = new RecursiveParserWrapper(p);
    Metadata metadata = new Metadata();
    File file = new File(filePath);
    metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
    ParseContext context = new ParseContext();
    RecursiveParserWrapperHandler handler = new RecursiveParserWrapperHandler(factory, -1);
    try (InputStream stream = new FileInputStream(file)) {
      wrapper.parse(stream, handler, metadata, context);
    }
    // Just print parse result
    handler.getMetadataList().stream()
        .map(
            m ->
                String.format(
                    "file name=%s mime type=%s",
                    m.get(Metadata.RESOURCE_NAME_KEY), m.get(Metadata.CONTENT_TYPE)))
        .forEach(System.out::println);
  }
}
