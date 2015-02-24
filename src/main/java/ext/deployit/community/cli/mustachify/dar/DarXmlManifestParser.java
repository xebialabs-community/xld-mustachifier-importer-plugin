/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.dar;

import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPath;
import org.jdom2.xpath.XPathFactory;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.transform;


public class DarXmlManifestParser implements Supplier<DarManifest> {
    private final DarManifest parsedManifest;

    public DarXmlManifestParser(TFile manifest) {
        parsedManifest = parse(manifest);
    }

    private static DarManifest parse(TFile manifest) {

        TFileInputStream manifestEntryStream = null;

        try {
            manifestEntryStream = new TFileInputStream(manifest);
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(manifestEntryStream);
            final Element rootElement = document.getRootElement();
            Iterable<DarManifestEntry> darEntries = transform(rootElement.getChild("deployables").getChildren(),
                    new Function<Element, DarManifestEntry>() {
                        @Override
                        public DarManifestEntry apply(Element input) {
                            return new DarManifestEntry(input.getName(), input.getAttributeValue("name"), input.getAttributeValue("file"));
                        }
                    });
            return new DarManifest(rootElement.getAttributeValue("application"),
                    rootElement.getAttributeValue("version"), copyOf(darEntries));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (manifestEntryStream != null)
                try {
                    manifestEntryStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }


    @Override
    public
    @Nonnull
    DarManifest get() {
        return parsedManifest;
    }


}