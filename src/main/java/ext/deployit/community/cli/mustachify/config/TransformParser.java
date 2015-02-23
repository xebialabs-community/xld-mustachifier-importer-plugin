/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import ext.deployit.community.cli.mustachify.transform.DarEntryTransformer;
import ext.deployit.community.cli.mustachify.transform.DarEntryTransformer.TransformerFactory;

/**
 * @author aphillips
 * @since 22 Jul 2011
 *
 */
public class TransformParser implements Function<Map<String, String>, DarEntryTransformer>{
    private static final String TYPE_PROPERTY = "type";
    
    private final Map<String, TransformerFactory> transformerFactories;
    
    public TransformParser(Collection<TransformerFactory> transformerFactories) {
        this.transformerFactories = Maps.uniqueIndex(transformerFactories, 
                new Function<TransformerFactory, String>() {
                    @Override
                    public String apply(TransformerFactory input) {
                        return input.getTransformerType();
                    }
                });
    }

    @Override
    public @Nonnull DarEntryTransformer apply(@Nonnull Map<String, String> config) {
        String transformerType = config.get(TYPE_PROPERTY);
        checkArgument(transformerType != null, "config property '%s' is required", TYPE_PROPERTY);
        TransformerFactory transformerFactory = transformerFactories.get(transformerType);
        checkArgument(transformerFactory != null, "unknown rule type '%s'", transformerType);
        return transformerFactory.from(config);
    }

}
