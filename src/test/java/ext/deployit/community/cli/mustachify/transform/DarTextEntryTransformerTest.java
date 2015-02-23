/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import ext.deployit.community.cli.mustachify.transform.DarTextEntryTransformer;

/**
 * Unit tests for the {@link DarTextEntryTransformer}
 */
public class DarTextEntryTransformerTest {
    private static class StubTextTransformer extends DarTextEntryTransformer {
        private StubTextTransformer(Map<String, String> config) {
            super(config);
        }

        @Override
        protected void transform(Reader entryContents, Writer newEntryContents)
                throws IOException {
            // do nothing
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void requiresAvailableEncoding() {
        // Cp1047 = Latin-1 character set for EBCDIC hosts 
        new StubTextTransformer(ImmutableMap.of("type", "stub", "ci.type", "Dummy",
                "encoding", "unavailable"));
    }
}
