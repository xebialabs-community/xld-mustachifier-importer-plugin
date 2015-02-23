/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.transform;

import java.io.File;

import javax.annotation.Nonnull;

import com.google.common.base.Function;

import de.schlichtherle.truezip.file.TFile;

/**
 * @author aphillips
 * @since 27 Jul 2011
 *
 */
public class TFiles {
    private static final Function<File, TFile> TO_TFILE = new Function<File, TFile>() {
        @Override
        public TFile apply(File input) {
            checkArgument(input instanceof TFile, "'%s' is not a TFile", input);
            return (TFile) input;
        }
    };
    
    public static @Nonnull Iterable<TFile> listTEntries(@Nonnull TFile folder) {
        return transform(Files2.listEntriesRecursively(folder), TO_TFILE);
    }

    public static @Nonnull Iterable<TFile> listTFiles(@Nonnull TFile folder) {
        return transform(Files2.listFilesRecursively(folder), TO_TFILE);
    }
}
