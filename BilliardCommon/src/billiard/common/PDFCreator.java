/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 *
 * @author jean
 */
public class PDFCreator {
    private static final Logger LOGGER = Logger.getLogger(PDFCreator.class.getName());
 
    public static void createPDF(String content, String outputFile) throws Exception{
        LOGGER.log(Level.FINEST, "createPDF => outputFile: {0}", outputFile);

        try (OutputStream os = new FileOutputStream(outputFile)) {
            ITextRenderer renderer = new ITextRenderer();
            LOGGER.log(Level.FINEST, "createPDF => step 1");
            renderer.setDocumentFromString(content);
            LOGGER.log(Level.FINEST, "createPDF => step 2");
            renderer.layout();
            LOGGER.log(Level.FINEST, "createPDF => step 2");
            renderer.createPDF(os);
            LOGGER.log(Level.FINEST, "createPDF => step 3");
        }        
    }
}
