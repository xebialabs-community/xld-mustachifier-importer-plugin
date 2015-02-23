/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class Files2 {
    private static final IOFileFilter TRUE = TrueFileFilter.INSTANCE;
    private static final IOFileFilter FALSE = FalseFileFilter.INSTANCE;
    
    public static boolean delete(@Nullable File file) {
        if (file != null) {
            return file.delete();
        }
        return false;
    }
    
    public static void deleteOnExit(@Nullable File file) {
        if (file != null) {
            file.deleteOnExit();
        }
    }
    
    /**
     * Returns the path to an (almost certainly) non-existent file in the default temporary 
     * directory. The file itself is <em>not</em> created.
     * 
     * Due to the small possibility that another process claims the name before it can
     * be used, callers may wish to check for existence using {@link File#exists()} to
     * be sure.
     */
    public static String getTempFilePath(String basename, String ext) throws IOException {
        File tempFile = File.createTempFile(basename, ext);
        String tempFilePath = tempFile.getPath();
        tempFile.delete();
        return tempFilePath;
    }
    
    public static @Nonnull Collection<File> listEntriesRecursively(@Nonnull File directory) {
        return listFiles(directory, TRUE, TRUE, true);
    }
    
    public static @Nonnull Collection<File> listEntries(@Nonnull File directory) {
        return listFiles(directory, TRUE, FALSE, true);    
    }
    
    public static @Nonnull Collection<File> listFilesRecursively(@Nonnull File directory) {
        return listFiles(directory, TRUE, TRUE, false);
    }
    
    public static @Nonnull Collection<File> listFiles(@Nonnull File directory) {
        return listFiles(directory, TRUE, FALSE, false);    
    }
    
    /**
     * A variation on {@link FileUtils#listFiles(File, IOFileFilter, IOFileFilter) listFiles}
     * that also optionally includes directories matched in the result.
     */
    public static Collection<File> listFiles(
            File directory, IOFileFilter fileFilter, IOFileFilter dirFilter, 
            boolean includeDirs) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(
                    "Parameter 'directory' is not a directory");
        }
        if (fileFilter == null) {
            throw new NullPointerException("Parameter 'fileFilter' is null");
        }

        //Setup effective file filter
        IOFileFilter effFileFilter = FileFilterUtils.andFileFilter(fileFilter,
            FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE));

        //Setup effective directory filter
        IOFileFilter effDirFilter;
        if (dirFilter == null) {
            effDirFilter = FalseFileFilter.INSTANCE;
        } else {
            effDirFilter = FileFilterUtils.andFileFilter(dirFilter,
                DirectoryFileFilter.INSTANCE);
        }

        //Find files
        Collection<File> files = new LinkedList<File>();
        innerListFiles(files, directory,
            FileFilterUtils.orFileFilter(effFileFilter, effDirFilter), includeDirs);
        return files;
    }

    private static void innerListFiles(Collection<File> files, File directory,
            IOFileFilter filter, boolean includeDirs) {
        File[] found = directory.listFiles((FileFilter) filter);
        if (found != null) {
            for (int i = 0; i < found.length; i++) {
                boolean isDir = found[i].isDirectory();
                if (!isDir || includeDirs) {
                    files.add(found[i]);
                }
                if (isDir) {
                    innerListFiles(files, found[i], filter, includeDirs);
                }
            }
        }
    }
}
