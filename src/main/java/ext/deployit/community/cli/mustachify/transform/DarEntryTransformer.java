/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.transform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;

import ext.deployit.community.cli.mustachify.dar.DarManifestEntry;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * @author aphillips
 * @since Jul 28, 2011
 */
public abstract class DarEntryTransformer {
    private static final String MATCHED_TYPE_PROPERTY = "ci.type";
    private static final String MATCHED_ENTRY_PATH_PROPERTY = "ci.path.pattern";

    // null object - declared after private statics because it requires the MATCHED_TYPE_PROPERTY
    public static final DarEntryTransformer NULL = new DarEntryTransformer(
            ImmutableMap.of(MATCHED_TYPE_PROPERTY, "null")) {
        @Override
        public void transform(@Nonnull InputStream entryContents,
                              OutputStream newEntryContents) throws IOException {
            throw new UnsupportedOperationException("null object should never be called");
        }
    };

    protected final
    @Nonnull
    String typeToMatch;
    protected final
    @Nullable
    Pattern entryPathPatternToMatch;
    protected final boolean pathIndependentMatch;

    protected DarEntryTransformer(@Nonnull Map<String, String> config) {
        validate(config);
        typeToMatch = config.get(MATCHED_TYPE_PROPERTY);
        entryPathPatternToMatch = config.containsKey(MATCHED_ENTRY_PATH_PROPERTY)
                ? Pattern.compile(config.get(MATCHED_ENTRY_PATH_PROPERTY))
                : null;
        pathIndependentMatch = (entryPathPatternToMatch == null);
    }

    protected void validate(@Nonnull Map<String, String> config) {
        checkConfigProperty(config, MATCHED_TYPE_PROPERTY);
    }

    protected static void checkConfigProperty(@Nonnull Map<String, String> config,
                                              String propertyName) {
        checkArgument(config.containsKey(propertyName),
                "config property '%s' is required", propertyName);
    }

    public boolean matches(@Nonnull DarManifestEntry entry) {
        return (entry.type.equalsIgnoreCase(typeToMatch)
                && (pathIndependentMatch || entryPathPatternToMatch.matcher(entry.jarEntryPath).matches()));
    }


    @Override
    public String toString() {
        return "DarEntryTransformer{" +
                "entryPathPatternToMatch=" + entryPathPatternToMatch +
                ", pathIndependentMatch=" + pathIndependentMatch +
                ", typeToMatch='" + typeToMatch + '\'' +
                '}';
    }

    /**
     * Streams can be used only during invocation of the method; any calls outside
     * may produce inconsistent results and/or result in exceptions.
     */
    public abstract void transform(@Nonnull InputStream entryContents,
                                   @Nonnull OutputStream newEntryContents) throws IOException;

    public boolean canApply(File filesToTransform) {
        return true;
    }

    public static interface TransformerFactory {
        @Nonnull
        String getTransformerType();

        @Nonnull
        DarEntryTransformer from(@Nonnull Map<String, String> config);
    }
}
