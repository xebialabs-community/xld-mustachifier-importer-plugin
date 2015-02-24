/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.dar;

import java.util.jar.Attributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.annotations.VisibleForTesting;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class DarManifestEntry {
    // null object to indicate "no result"
    public static final DarManifestEntry NULL = new DarManifestEntry(EMPTY, null, EMPTY);

    @VisibleForTesting
    public static final String TYPE_ATTRIBUTE_NAME = "Ci-Type";
    private static final String NAME_ATTRIBUTE_NAME = "Ci-Name";

    public final @Nonnull String type;
    public final @Nullable String name;
    public final @Nonnull String jarEntryPath;

    DarManifestEntry(@Nonnull String type, @Nullable String name, @Nonnull String jarEntryPath) {
        this.type = checkNotNull(type, "type");
        this.name = name;
        this.jarEntryPath = checkNotNull(jarEntryPath, "jarEntryPath");
    }

    static boolean isDarEntry(@Nonnull String manifestEntryName,
                              @Nonnull Attributes entryAttributes) {
        return isNotEmpty(entryAttributes.getValue(TYPE_ATTRIBUTE_NAME));
    }

    @VisibleForTesting
    public static
    @Nonnull
    DarManifestEntry fromEntryAttributes(
            @Nonnull String manifestEntryName, @Nonnull Attributes entryAttributes) {
        checkArgument(isDarEntry(manifestEntryName, entryAttributes),
                "'%s' is not a valid DAR entry", manifestEntryName);
        return new DarManifestEntry(entryAttributes.getValue(TYPE_ATTRIBUTE_NAME),
                entryAttributes.getValue(NAME_ATTRIBUTE_NAME), manifestEntryName);
    }

    @Override
    public String toString() {
        return "DarManifestEntry [type=" + type + ", name=" + name
                + ", jarEntryPath=" + jarEntryPath + "]";
    }
}