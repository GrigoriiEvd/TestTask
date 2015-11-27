package packege;// таже аннотация, что и при описании интерфейса,

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@WebService(endpointInterface = "packege.FileWebService")
public class WebServiceImpl implements FileWebService {
    private List<NameIdentifier> listFile;
    private String directory;

    public WebServiceImpl() {
        super();
        directory = "";
        try {
            File fXml = new File("config.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fXml);

            doc.getDocumentElement().normalize();

            NodeList nodeLst = doc.getElementsByTagName("conf");
            Node fstNode = nodeLst.item(0);
            int i1 = 0;
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elj = (Element) fstNode;
                directory = elj.getAttribute("directory");
            }
            listFile = new ArrayList<NameIdentifier>();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка при открытии Xml файла");
        }
        updateList();
    }

    @Override
    public ListNameIdentifier search(String name) {
        ListNameIdentifier list = new ListNameIdentifier();
        for (int i = 0; i < listFile.size(); i++) {
            if (listFile.get(i).getName().contains(name)) {
                list.getList().add(listFile.get(i));
            }
        }
        return list;
    }

    @Override
    public boolean delete(String identifier) {
        int j = 0;
        for (int i = 0; i < listFile.size(); i++) {
            if (identifier.equals(listFile.get(i).getIdentifier())) {
                j = i;
                break;
            }
        }
        try {
            File fXml = new File("config.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fXml);

            doc.getDocumentElement().normalize();

            NodeList nodeLst = doc.getElementsByTagName("file");
            for (int i = 0; i < nodeLst.getLength(); i++) {
                Node fstNode = nodeLst.item(i);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elj = (Element) fstNode;
                    String pName = elj.getAttribute("identifier");
                    if (pName.equals(identifier)) {
                        elj.getParentNode().removeChild(elj);
                    }
                }
            }
            writeXML(doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(directory + "/" + listFile.get(j).getName());
        if (!file.delete()) {
            return false;
        }
        listFile.remove(j);
        return true;
    }

    @Override
    public DataHandler downloadFile(String identifier) {
        int j = 0;
        for (int i = 0; i < listFile.size(); i++) {
            if (identifier.equals(listFile.get(i).getIdentifier())) {
                j = i;
                break;
            }
        }
        FileDataSource dataSource = new FileDataSource(directory + "/" + listFile.get(j).getName());
        DataHandler fileDataHandler = new DataHandler(dataSource);
        return fileDataHandler;
    }

    @Override
    public boolean loadFile(DataHandler file, String nameFile) {
        Boolean f = true;
        String identifier = null;
        try {
            identifier = Utils.getIdentifier(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < listFile.size(); i++) {
            if (identifier.equals(listFile.get(i).getIdentifier())) {
                f = false;
            }
        }
        if (f) {
            try {
                try (FileOutputStream outputStream = new FileOutputStream(directory + "/" + nameFile)) {
                    file.writeTo(outputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            listFile.add(new NameIdentifier());
            listFile.get(listFile.size() - 1).setName(nameFile);
            listFile.get(listFile.size() - 1).setIdentifier(identifier);
            try {
                File fXml = new File("config.xml");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(fXml);

                Element root = doc.getDocumentElement();
                root.normalize();

                Element newElement = doc.createElement("file");
                newElement.setAttribute("identifier", identifier);
                newElement.setAttribute("name", nameFile);

                root.appendChild(newElement);

                writeXML(doc);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getName(String identifier) {
        int j = 0;
        for (int i = 0; i < listFile.size(); i++) {
            if (identifier.equals(listFile.get(i).getIdentifier())) {
                j = i;
                break;
            }
        }
        return listFile.get(j).getName();
    }

    private void updateList() {
        listFile.clear();
        try {
            File fXml = new File("config.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fXml);

            doc.getDocumentElement().normalize();

            NodeList nodeLst = doc.getElementsByTagName("file");
            for (int i = 0; i < nodeLst.getLength(); i++) {
                Node fstNode = nodeLst.item(i);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elj = (Element) fstNode;
                    listFile.add(new NameIdentifier());
                    listFile.get(listFile.size() - 1).setName(elj.getAttribute("name"));
                    listFile.get(listFile.size() - 1).setIdentifier(elj.getAttribute("identifier"));
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private static void writeXML(Document doc) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("config.xml"));

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}