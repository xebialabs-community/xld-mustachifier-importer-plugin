/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.io.CharStreams;

/**
 * <strong>Use with caution!</strong> Reads the entire entry into memory, so not
 * suitable for large files.
 */
public abstract class TextEntryAsStringTransformer extends DarTextEntryTransformer {

    protected TextEntryAsStringTransformer(Map<String, String> config) {
        super(config);
    }

    @Override
    protected void transform(@Nonnull Reader entryContentStream, 
            @Nonnull Writer newEntryContentStream) throws IOException {
        newEntryContentStream.write(transform(CharStreams.toString(entryContentStream)));
    }
    
    protected abstract @Nonnull String transform(@Nonnull String entryContents);
}
