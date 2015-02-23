/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.transform;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author aphillips
 * @since Jul 31, 2011
 *
 */
public class StringReplaceTransformer extends RegexReplaceTransformer {
    private static final String STRING_TO_FIND_PROPERTY = "find";
    
    protected StringReplaceTransformer(Map<String, String> config) {
        /*
         * Have to validate in order to prepare the args for super, so the call sequence
         * is configForRegex -> prevalidate -> super rather than 
         * configForRegex -> super -> (overridden) validate
         */
        super(toRegexConfig(config));
    }

    private static Map<String, String> toRegexConfig(Map<String, String> config) {
        prevalidate(config);
        Map<String, String> regexConfig = newHashMap(config);
        // need to treat the search and replacement strings as *literals*
        regexConfig.put(PATTERN_TO_FIND_PROPERTY, 
                Pattern.quote(config.get(STRING_TO_FIND_PROPERTY)));
        regexConfig.put(REPLACEMENT_STRING_PROPERTY, 
                Matcher.quoteReplacement(config.get(REPLACEMENT_STRING_PROPERTY)));
        return regexConfig;
    }

    // the superclass will carry out its own validation as part of its constructor
    private static void prevalidate(Map<String, String> config) {
        checkConfigProperty(config, STRING_TO_FIND_PROPERTY);
        checkConfigProperty(config, REPLACEMENT_STRING_PROPERTY);
    }
    
    public static class StringReplaceTransformerFactory implements TransformerFactory {
        public static final String TRANSFORMER_TYPE = "string-replace";

        @Override
        public String getTransformerType() {
            return TRANSFORMER_TYPE;
        }

        public DarEntryTransformer from(Map<String, String> config) {
            return new StringReplaceTransformer(config);
        }
    }

}
