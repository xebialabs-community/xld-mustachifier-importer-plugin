/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.dar;

import java.io.File;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.fs.FsSyncException;

/**
 * @author aphillips
 * @since 27 Jul 2011
 *
 */
public class DarWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DarWriter.class);
    
    public static void flush(@Nonnull File dar) throws FsSyncException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Flushing changes to DAR '{}'", dar);
        }
        TFile.umount(new TFile(dar));
    }
}
