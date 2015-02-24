/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;

import ext.deployit.community.cli.mustachify.dar.DarManifestEntry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link DarTextEntryTransformer}
 */
public class DarEntryTransformerTest {
    // used in other test classes too
    public static class StubTransformer extends DarEntryTransformer {
        public StubTransformer(Map<String, String> config) {
            super(config);
        }

        @Override
        public void transform(InputStream entryContents,
                              OutputStream newEntryContents) throws IOException {
            // do nothing
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresTypeProperty() {
        new StubTransformer(ImmutableMap.<String, String>of());
    }

    @Test
    public void matchesOnType() {
        DarEntryTransformer transformer =
                new StubTransformer(ImmutableMap.of("ci.type", "Dummy"));
        assertTrue("Expected transformer to match input",
                transformer.matches(newEntry("irrelevant/path", "Dummy")));
    }

    @Test
    public void matchesOnPatternIfSpecified() {
        DarEntryTransformer transformer =
                new StubTransformer(ImmutableMap.of("ci.type", "Dummy",
                        "ci.path.pattern", ".+path"));
        assertTrue("Expected transformer to match input",
                transformer.matches(newEntry("relevant/path", "Dummy")));
    }

    @Test
    public void failsMatchOnNonmatchingPatternIfSpecified() {
        DarEntryTransformer transformer =
                new StubTransformer(ImmutableMap.of("ci.type", "Dummy",
                        "ci.path.pattern", ".+path"));
        assertFalse("Expected transformer not to match input",
                transformer.matches(newEntry("nonmatching/path/entry", "Dummy")));
    }

    private static DarManifestEntry newEntry(String entryPath, String ciType) {
        Attributes attributes = new Attributes(2);
        attributes.put(new Name(DarManifestEntry.TYPE_ATTRIBUTE_NAME), ciType);
        return DarManifestEntry.fromEntryAttributes(entryPath, attributes);
    }
}
