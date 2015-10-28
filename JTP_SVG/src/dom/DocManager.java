package dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;

/**
 * Created by Lukasz on 2014-06-08.
 */

public class DocManager {

    private Document doc;
    private Element g;
    private Element svg;
    private ArrayList<Element> elements;

    public DocManager() throws ParserConfigurationException {
        elements = new ArrayList<Element>();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document document = docBuilder.newDocument();
        document.setXmlStandalone(false);
        svg = document.createElement("svg");
        document.appendChild(svg);

        svg.setAttribute("width","800");
        svg.setAttribute("height","600");
        svg.setAttribute("version","1.1");
        svg.setAttribute("xmlns","http://www.w3.org/2000/svg");

        // g node
        g = document.createElement("g");
        svg.appendChild(g);

        this.doc = document;

    }

    public Document getDoc() { return doc; }

    public void addFigure(Element child) {
        g.appendChild(child);
        elements.add(child);
    }
    public void setStrokeAndRadius(double width, double radius) {
        for(Element element : elements) {
            element.setAttribute("stroke-width", String.valueOf(width));
            if(element.getNodeName().equals("circle") && element.getAttribute("r").equals("0.0")) {
                element.setAttribute("r", String.valueOf(radius));
            }
        }
    }
    public void setViewBox(String cmd) { svg.setAttribute("viewBox", cmd); }
    public void printDOM() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(System.out);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }

}
