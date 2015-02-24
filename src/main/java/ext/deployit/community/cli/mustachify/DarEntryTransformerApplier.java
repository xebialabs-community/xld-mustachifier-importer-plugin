/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.cli.mustachify;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;

import ext.deployit.community.cli.mustachify.dar.DarManifestEntry;
import ext.deployit.community.cli.mustachify.io.TFiles;
import ext.deployit.community.cli.mustachify.transform.DarEntryTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.transformValues;
import static ext.deployit.community.cli.mustachify.collect.Maps2.reduce;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

class DarEntryTransformerApplier {
	private static final Logger LOGGER = LoggerFactory.getLogger(DarEntryTransformerApplier.class);

	Collection<TFile> apply(Map<DarEntry, DarEntryTransformer> entriesToTransform) {
		Map<DarEntryTransformer, Iterable<TFile>> transformerWorkloads =
				transformValues(reduce(entriesToTransform), new ExpandDarEntries());

		Collection<TFile> transformedFiles = newLinkedList();
		for (Entry<DarEntryTransformer, Iterable<TFile>> transformerWorkload
				: transformerWorkloads.entrySet()) {
			// will force iteration of the iterable, but we're about to do that anyway
			Collection<TFile> filesToTransform = copyOf(transformerWorkload.getValue());
			try {
				apply(transformerWorkload.getKey(), filesToTransform);
				transformedFiles.addAll(filesToTransform);
			} catch (IOException exception) {
				LOGGER.warn("Unable to transform '{}' using '{}'", filesToTransform,
						transformerWorkload.getKey());
			}
		}
		return transformedFiles;
	}

	private static void apply(DarEntryTransformer transformer, Iterable<TFile> filesToTransform) throws IOException {
		for (TFile fileToTransform : filesToTransform) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to transform '{}' using '{}'", fileToTransform, transformer);
			}
			if (!transformer.canApply(fileToTransform)) {
				LOGGER.debug("Skip '{}' ", fileToTransform);
				continue;
			}
			apply(transformer, fileToTransform);
		}
	}

	// expand each DAR entry into a set of files and concatenate the results
	private static class ExpandDarEntries implements Function<Set<DarEntry>, Iterable<TFile>> {
		@Override
		public Iterable<TFile> apply(Set<DarEntry> input) {
			return concat(transform(input, new Function<DarEntry, Iterable<TFile>>() {
				@Override
				public Iterable<TFile> apply(DarEntry entry) {
					TFile entryContents = entry.contents;
					return entry.isFolder() ? TFiles.listTFiles(entryContents)
							: ImmutableSet.of(entryContents);
				}
			}));
		}
	}

	private static void apply(DarEntryTransformer transformer, TFile fileToTransform) throws IOException {
		String entryPath = fileToTransform.getInnerEntryName();
		String baseName = getBaseName(entryPath);
		if (baseName == null || baseName.length() < 3) {
			//handle file like .DSTORE
			baseName = "generatedBaseName";
		}
		File newContentBuffer =
				File.createTempFile(baseName, getExtension(entryPath));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created temporary buffer '{}' for transformed content",
					newContentBuffer);
		}
		try {
			InputStream entryContents = new TFileInputStream(fileToTransform);
			try {
				FileOutputStream streamToBuffer = new FileOutputStream(newContentBuffer);
				try {
					transformer.transform(entryContents, streamToBuffer);
				} finally {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Closing output stream '{}' to temporary buffer", streamToBuffer);
					}
					streamToBuffer.close();
				}
			} finally {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("Closing input stream '{}' from source file", entryContents);
				}
				entryContents.close();
			}
			// overwrite untransformed file in target
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Copying transformed content from buffer '{}' to target '{}'",
						newContentBuffer, fileToTransform);
			}
			TFile.cp(newContentBuffer, fileToTransform);
		} finally {
			// cleanup
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Deleted temporary content buffer '{}'", newContentBuffer);
			}
			newContentBuffer.delete();
		}
	}

	static class DarEntry {
		public final DarManifestEntry metadata;
		public final TFile contents;

		DarEntry(@Nonnull DarManifestEntry metadata, @Nonnull TFile contents) {
			this.metadata = metadata;
			this.contents = contents;
		}

		public boolean isFolder() {
			// could add checks of the type, too
			return contents.isDirectory();
		}
	}
}