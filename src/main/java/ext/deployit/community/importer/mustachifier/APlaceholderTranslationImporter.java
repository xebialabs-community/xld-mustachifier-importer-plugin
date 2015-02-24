/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.mustachifier;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.server.api.importer.*;

public class APlaceholderTranslationImporter implements ListableImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(APlaceholderTranslationImportSource.class);

    static final String DELEGATED_SERVICE = "com.xebialabs.deployit.service.importer.XmlManifestDarImporter";
    public static final String REPOSITORY_SERVICE_HOLDER = "com.xebialabs.deployit.repository.RepositoryServiceHolder";

    private ListableImporter service;
    private APlaceholderTranslationImportSource translationImportSource;

    public APlaceholderTranslationImporter() {
    }

    public List<String> list(File directory) {
        return Collections.emptyList();
    }

    public boolean canHandle(ImportSource source) {
        //only dar file are supported.
        File file = source.getFile();
        LOGGER.debug("can handle ? " + file);
        return isDarPackage(file);
    }

    public PackageInfo preparePackage(ImportSource source, ImportingContext context) {
        LOGGER.debug("source = {}", source);
        translationImportSource = new APlaceholderTranslationImportSource(source);
        return getService().preparePackage(translationImportSource, context);
    }

    public ImportedPackage importEntities(PackageInfo packageInfo, ImportingContext context) {
        return getService().importEntities(packageInfo, context);
    }

    public void cleanUp(PackageInfo packageInfo, ImportingContext context) {
        getService().cleanUp(packageInfo, context);
        translationImportSource.cleanUp();
    }

    static boolean isDarPackage(final File f) {
        return f.isFile() && f.getName().toLowerCase().endsWith(".dar");
    }

    public ListableImporter getService() {
        if (service == null) {
            try {
                final Class<?> aClass = this.getClass().getClassLoader().loadClass(DELEGATED_SERVICE);
                final Class<?> rshClass = this.getClass().getClassLoader().loadClass(REPOSITORY_SERVICE_HOLDER);
                final Method getRepositoryServiceMethod = rshClass.getMethod("getRepositoryService");
                Object repositoryService = getRepositoryServiceMethod.invoke(null);
                //final Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(RepositoryService.class);
                final Constructor<?> declaredConstructor = aClass.getDeclaredConstructors()[0];
                service = (ListableImporter) declaredConstructor.newInstance(repositoryService);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot instance class " + DELEGATED_SERVICE, e);
            }
        }
        return service;
    }
}
