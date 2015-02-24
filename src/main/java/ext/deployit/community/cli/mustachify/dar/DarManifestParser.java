/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.dar;

import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import javax.annotation.Nonnull;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;


public class DarManifestParser implements Supplier<DarManifest> {
    private static final String PACKAGE_FORMAT_VERSION_ATTRIBUTE_NAME = "Deployit-Package-Format-Version";
    private static final String SUPPORTED_PACKAGE_FORMAT_VERSION = "1.3";
    private static final String APPLICATION_ATTRIBUTE_NAME = "Ci-Application";
    private static final String VERSION_ATTRIBUTE_NAME = "Ci-Version";

    private final DarManifest parsedManifest;

    public DarManifestParser(Manifest manifest) {
        parsedManifest = parse(manifest);
    }

    private static DarManifest parse(Manifest manifest) {
        Attributes mainAttributes = manifest.getMainAttributes();
        validate(mainAttributes);

        Iterable<DarManifestEntry> darEntries = filter(transform(manifest.getEntries().entrySet(),
                        new Function<Entry<String, Attributes>, DarManifestEntry>() {
                            @Override
                            public DarManifestEntry apply(Entry<String, Attributes> input) {
                                String entryName = input.getKey();
                                Attributes entryAttributes = input.getValue();
                                return (DarManifestEntry.isDarEntry(entryName, entryAttributes)
                                        ? DarManifestEntry.fromEntryAttributes(entryName, entryAttributes)
                                        : DarManifestEntry.NULL);
                            }
                        }),
                not(new Predicate<Object>() {
                    @Override
                    public boolean apply(Object input) {
                        return (input == DarManifestEntry.NULL);
                    }
                }));
        return new DarManifest(mainAttributes.getValue(APPLICATION_ATTRIBUTE_NAME),
                mainAttributes.getValue(VERSION_ATTRIBUTE_NAME), copyOf(darEntries));
    }

    private static void validate(Attributes mainAttributes) {
        checkArgument(mainAttributes.containsKey(new Name(PACKAGE_FORMAT_VERSION_ATTRIBUTE_NAME)),
                "manifest does not contain required DAR attribute '%s'", PACKAGE_FORMAT_VERSION_ATTRIBUTE_NAME);
        checkArgument(mainAttributes.getValue(PACKAGE_FORMAT_VERSION_ATTRIBUTE_NAME).equals(SUPPORTED_PACKAGE_FORMAT_VERSION),
                "unsupported package format version '%s', only '%s' supported",
                mainAttributes.getValue(PACKAGE_FORMAT_VERSION_ATTRIBUTE_NAME), SUPPORTED_PACKAGE_FORMAT_VERSION);
        checkArgument(mainAttributes.containsKey(new Name(APPLICATION_ATTRIBUTE_NAME)),
                "manifest does not contain required DAR attribute '%s'", APPLICATION_ATTRIBUTE_NAME);
        checkArgument(mainAttributes.containsKey(new Name(VERSION_ATTRIBUTE_NAME)),
                "manifest does not contain required DAR attribute '%s'", VERSION_ATTRIBUTE_NAME);
    }

    @Override public @Nonnull DarManifest get() {
        return parsedManifest;
    }


}