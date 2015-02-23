/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.dar;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;

import javax.annotation.Nonnull;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;

/**
 * @author aphillips
 * @since 27 Jul 2011
 *
 */
public class DarReader {
    public static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
    
    public static @Nonnull Manifest getManifest(@Nonnull File dar) throws IOException {
        TFileInputStream manifestEntryStream = 
            new TFileInputStream(getEntry(dar, MANIFEST_PATH));
        try {
            return new Manifest(manifestEntryStream);
        } finally {
            manifestEntryStream.close();
        }
    }
    
    public static @Nonnull TFile getEntry(@Nonnull File dar, @Nonnull String path) {
        checkArgument(new TFile(dar, path).exists(), 
                "DAR '%s' does not contain an entry at '%s'", dar, path);
        return new TFile(dar, path);
    }
    
    public static void checkValidDar(TFile file) {
        checkArgument(file.exists(), "File '%s' does not exist or cannot be read", file);
        checkArgument(file.isArchive(), "File '%s' is not a valid ZIP archive", file);
        checkArgument(new TFile(file, MANIFEST_PATH).exists(), 
                "Archive '%s' does not contain a manifest at '%s'", file, MANIFEST_PATH);
    }
}
