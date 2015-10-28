package svg;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Lukasz on 2014-06-08.
 */

public class SVGCreator {

    public static void create(Document doc, String outputPath) throws TransformerException, FileNotFoundException {
        TransformerFactory xformFactory = TransformerFactory.newInstance();
        xformFactory.setAttribute("indent-number", 4);
        Transformer transformer = xformFactory.newTransformer();

        // It sets the standalone property in the XML
        // declaration, which appears as the first line
        // of the output file.
        transformer.setOutputProperty(OutputKeys.STANDALONE,"no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        //Get a DOMSource object that represents the Document object
        DOMSource source = new DOMSource(doc);

        //Get an output stream for the output file.
        PrintWriter outStream = new PrintWriter(outputPath);

        //Get a StreamResult object that points to the output file.  Then transform the DOM sending XML to the file
        StreamResult fileResult = new StreamResult(outStream);
        transformer.transform(source,fileResult);

        System.out.println("Generated SVG file to: '" + outputPath + "'");
    }

}
