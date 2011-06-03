package org.vaadin.appfoundation.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.Queue;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * TranslationSource implementation for TMX files.
 * 
 * @author Kim
 * 
 */
public class TmxSourceReader implements TranslationSource {

    private Queue<TranslationMessage> messages = new LinkedList<TranslationMessage>();

    /**
     * 
     * @param file
     *            The File object of the translation file
     */
    public TmxSourceReader(File file) {
        if (isFileAndExists(file)) {
            throw new IllegalArgumentException("Translation did not exist");
        }

        readAndProcessFile(file);
    }

    /**
     * Verifies that the given file exists and is actually a file and not a
     * folder.
     * 
     * @param file
     * @return
     */
    private boolean isFileAndExists(File file) {
        return file == null || !file.exists() || !file.isFile();
    }

    /**
     * Reads ands processes the given TMX file
     * 
     * @param file
     */
    private void readAndProcessFile(File file) {
        try {
            // Read the translations.xml file to the Document
            Document document = new Builder().build(new FileInputStream(file));
            Elements tu = getTranslationUnits(document);
            processTranslationUnits(tu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches all translation unit elements
     * 
     * @param document
     * @return
     */
    private Elements getTranslationUnits(Document document) {
        Element root = document.getRootElement();
        Elements tu = root.getChildElements("body").get(0).getChildElements(
                "tu");
        return tu;
    }

    /**
     * Processes all translation units
     * 
     * @param tu
     */
    private void processTranslationUnits(Elements tu) {
        // Loop through the tu-elements. Each tu-element is a translation
        // for a single localized string
        for (int i = 0; i < tu.size(); i++) {
            Element element = tu.get(i);
            processTranslationUnit(element);
        }
    }

    /**
     * Processes a single translation unit
     * 
     * @param element
     */
    private void processTranslationUnit(Element element) {
        String identifier = element.getAttributeValue("tuid");

        // Get the tuv-elements. Each tuv-element is the actual
        // translation for one language
        Elements tuv = element.getChildElements("tuv");
        processTranslationUnitVariants(identifier, tuv);
    }

    /**
     * Process all translation unit variants within a translation unit
     * 
     * @param identifier
     * @param tuv
     */
    private void processTranslationUnitVariants(String identifier, Elements tuv) {
        for (int j = 0; j < tuv.size(); j++) {
            Element variant = tuv.get(j);
            addTranslationMessage(identifier, variant);
        }
    }

    /**
     * Creates a translation message of the given translation unit variant and
     * adds it to the queue.
     * 
     * @param identifier
     * @param tuv
     */
    private void addTranslationMessage(String identifier, Element tuv) {
        String language = tuv.getAttributeValue("lang");
        // Get the translated message
        String value = tuv.getChildElements("seg").get(0).getValue();

        // Create translation message
        TranslationMessage message = new TranslationMessage(identifier,
                language, value);
        messages.add(message);
    }

    /**
     * {@inheritDoc}
     */
    public TranslationMessage getNext() {
        return messages.poll();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return messages.peek() != null;
    }

}
