/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.config;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import ext.deployit.community.cli.mustachify.config.ConfigParser;
import ext.deployit.community.cli.mustachify.config.TransformParser;
import ext.deployit.community.cli.mustachify.transform.DarEntryTransformer;
import ext.deployit.community.cli.mustachify.transform.DarEntryTransformer.TransformerFactory;
import ext.deployit.community.cli.mustachify.transform.DarEntryTransformerTest.StubTransformer;

/**
 * Unit tests for the {@link ConfigParser}
 */
public class ConfigParserTest {

    // matches all transformers of type 'stub'
    private static class StubTransformerFactory implements TransformerFactory {
        private static final String TRANSFORM_TYPE = "stub";
        
        @Override
        public String getTransformerType() {
            return TRANSFORM_TYPE;
        }
        
        @Override
        public DarEntryTransformer from(Map<String, String> config) {
            return new StubTransformer(config);
        }
    }
    
    @Test
    public void processesTransformsUpToFirstMissingIndex() {
        Properties config = new Properties();
        // 8 properties could be 4 rules - 'ci.type' and 'type' are the only required values
        config.put("transform.1.type", StubTransformerFactory.TRANSFORM_TYPE);
        config.put("transform.1.ci.type", "Dummy");
        config.put("transform.2.type", StubTransformerFactory.TRANSFORM_TYPE);
        config.put("transform.2.ci.type", "Dummy");
        config.put("transform.3.type", StubTransformerFactory.TRANSFORM_TYPE);
        config.put("transform.3.ci.type", "Dummy");
        config.put("transform.3.other.prop", "Dummy");
        config.put("transform.3.other.prop.2", "Dummy");
        assertEquals(3, new ConfigParser(config, 
                new TransformParser(ImmutableSet.<TransformerFactory>of(new StubTransformerFactory())))
                .get().size());
    }
}
