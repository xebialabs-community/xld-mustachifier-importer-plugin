/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;

import ext.deployit.community.cli.mustachify.transform.StringReplaceTransformer;
import ext.deployit.community.cli.mustachify.transform.StringReplaceTransformer.StringReplaceTransformerFactory;

/**
 * Unit tests for the {@link StringReplaceTransformer}
 */
public class StringReplaceTransformerTest {

    @Test
    public void findsLiteralString() {
        StringReplaceTransformer transformer = get(".*", "long");
        assertEquals("A long long string", transformer.transform("A long .* string"));
    }
    
    @Test
    public void insertsLiteralReplacement() {
        StringReplaceTransformer transformer = get("(long)", "l$1g");
        assertEquals("A long l$1g string", transformer.transform("A long (long) string"));

    }
    
    private static StringReplaceTransformer get(String stringToFind, String replacement) {
        return new StringReplaceTransformer(ImmutableMap.of(
                "type", StringReplaceTransformerFactory.TRANSFORMER_TYPE,
                "ci.type", "Dummy",
                "encoding", Charsets.UTF_8.toString(),
                "find", stringToFind,
                "replacement", replacement));
    }
}
