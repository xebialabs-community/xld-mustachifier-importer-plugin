/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify;

import de.schlichtherle.truezip.file.TFileInputStream;
import ext.deployit.community.cli.mustachify.Mustachifier;

import org.junit.After;
import org.junit.Test;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.google.common.base.Charsets.ISO_8859_1;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.ByteStreams.toByteArray;
import static ext.deployit.community.cli.mustachify.dar.DarReader.getEntry;
import static ext.deployit.community.cli.mustachify.io.Files2.deleteOnExit;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link Mustachifier}
 */
public class MustachifierTest {
    public static final String TEST_ARCHIVE_PATH = "src/test/resources/sample-dar.zip";

    private Mustachifier converter = new Mustachifier();
    private File result;

    @Test(expected = IllegalArgumentException.class)
    public void requiresSourceArchiveToExist() throws IOException {
        converter.convert("non-existent/path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresTargetToNotExist() throws IOException {
        converter.convert(TEST_ARCHIVE_PATH, "src/test/resources/mustachifier.properties");
    }

    @Test
    public void transformsMatchedFileEntries() throws IOException {
        result = converter.convert(TEST_ARCHIVE_PATH);
        // source was "The ${placeholder} should become {{placeholder}}."
        assertEquals("The {{placeholder}} should become {{placeholder}}.",
                getContents(result, "script.sql", UTF_8));
    }

    private static String getContents(File dar, String entryPath, Charset charset) throws IOException {
        TFileInputStream entryBytes = new TFileInputStream(getEntry(dar, entryPath));
        try {
            return new String(toByteArray(entryBytes), charset);
        } finally {
            entryBytes.close();
        }
    }

    @Test
    public void transformsFilesInMatchedFolderEntries() throws IOException {
        result = converter.convert(TEST_ARCHIVE_PATH);
        // source was "The 'foo' here sh\u014Duld be replaced." (note the UTF-8 char)
        assertEquals("The 'bar' here sh\u014Duld be replaced.",
                getContents(result, "config-UTF8/config.txt", UTF_8));
        assertEquals("The 'bar' here sh\u014Duld be replaced.",
                getContents(result, "config-UTF8/config2.txt", UTF_8));
        assertEquals("The 'bar' here sh\u014Duld be replaced.",
                getContents(result, "config-UTF8/subfolder/config3.txt", UTF_8));
    }

    @Test
    public void usesSpecifiedEncoding() throws IOException {
        result = converter.convert(TEST_ARCHIVE_PATH);
        // source was "The 'foo' here should be replaced."
        assertEquals("The 'bar' here should be replaced.",
                getContents(result, "config-ISO8859/config.txt", ISO_8859_1));
        assertEquals("The 'bar' here should be replaced.",
                getContents(result, "config-ISO8859/config2.txt", ISO_8859_1));
        assertEquals("The 'bar' here should be replaced.",
                getContents(result, "config-ISO8859/subfolder/config3.txt", ISO_8859_1));
    }

    @Test
    public void ignoresUnmatchedEntries() throws IOException {
        result = converter.convert(TEST_ARCHIVE_PATH);
	    // source was "Neither the 'foo' nor the ${PLACEHOLDER} here should be replaced."
        assertEquals("Neither the 'foo' nor the ${PLACEHOLDER} here should be replaced.\n",
                getContents(result, "unmatched.ear", UTF_8));
    }

    @Test
    public void ignoresNonDarEntries() throws IOException {
        result = converter.convert(TEST_ARCHIVE_PATH);
        // source was "The 'foo' here should *not* be replaced."
        assertEquals("The 'foo' here should *not* be replaced.",
                getContents(result, "other-text/config.txt", UTF_8));
    }

    @After
    public void removeResult() {
        deleteOnExit(result);
    }
}
