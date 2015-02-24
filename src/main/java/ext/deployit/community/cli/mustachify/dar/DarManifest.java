package ext.deployit.community.cli.mustachify.dar;

import java.util.Set;

import static com.google.common.collect.ImmutableSet.copyOf;

public class DarManifest {
    public final String appName;
    public final String version;
    public final Set<DarManifestEntry> entries;

    public DarManifest(String appName, String version, Set<DarManifestEntry> entries) {
        this.appName = appName;
        this.version = version;
        this.entries = copyOf(entries);
    }


}