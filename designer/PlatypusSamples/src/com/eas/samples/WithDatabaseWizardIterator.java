package com.eas.samples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author mg
 */
public class WithDatabaseWizardIterator extends PlatypusSamplesWizardIterator {
    private static final String DB_RES_FILE = "CustomerCMP-ejb/setup/derby_netPool.sun-resource"; //NOI18N

    @Override
    protected WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[] {
            new PlatypusSamplesWizardPanel(true)
        };
    }
    
    @Override
    public Set<FileObject> instantiate() throws IOException{
        String dbName = (String) wiz.getProperty(PlatypusSamples.DB_NAME);
        Set r = super.instantiate();
        
        File projectDir = (File) wiz.getProperty(PlatypusSamples.PROJ_DIR);
        File dbResource = new File(projectDir, DB_RES_FILE);
        updateDBResource(dbResource, dbName);
        
        return r;
    }
    
    public static WithDatabaseWizardIterator createIterator() {
        return new WithDatabaseWizardIterator();
    }
    
    private void updateDBResource(File dbResource, String dbName) throws IOException {
        String xPathPath = "/resources/jdbc-connection-pool/property[@name='DatabaseName']/@value"; //NOI18N
        setValueInXMLFile(dbResource, xPathPath, dbName);
    }

    private static void setValueInXMLFile(File srcFile, String xPathPath, String value) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(srcFile);
            
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = (Node) xPath.evaluate(xPathPath, document, XPathConstants.NODE);
            node.setTextContent(value);
            
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            
            Transformer aTransformer = tranFactory.newTransformer();
            Source src = new DOMSource(document);
            try (FileOutputStream fos = new FileOutputStream(srcFile)) {
                Result dest = new StreamResult(fos);
                aTransformer.transform(src, dest);
            }
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException | DOMException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
