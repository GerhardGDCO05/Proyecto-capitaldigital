package com.example.capitalDigital.usuario.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.capitalDigital.usuario.models.MetaFinanciera;

@Service
public class MetaService {

    private static final String XML_FILE = "C:\\Users\\simon\\OneDrive\\Escritorio\\CapitalDigitalISProyect\\Proyecto-capitaldigital\\capitalDigital\\src\\main\\java\\com\\example\\capitalDigital\\Info_bank\\Metas.xml";

    // Método que faltaba en el controller
    public boolean guardarMeta(String numeroDocumento, Map<String, Object> metaData) {
        try {
            MetaFinanciera meta = new MetaFinanciera(
                (String) metaData.get("nombre"),
                (String) metaData.get("fechaInicio"),
                (String) metaData.get("fechaFin"),
                Double.parseDouble(metaData.get("montoRequerido").toString())
            );
            return guardarMetaEnXML(numeroDocumento, meta);
        } catch (Exception e) {
            System.err.println("Error al crear meta desde map: " + e.getMessage());
            return false;
        }
    }

    public boolean guardarMetaEnXML(String numeroDocumento, MetaFinanciera meta) {
        try {
            File file = new File(XML_FILE);
            Document doc;
            Element root; 

            if (file.exists() && file.length() > 0) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();
                root = doc.getDocumentElement();
            } else {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.newDocument();
                root = doc.createElement("metas");
                doc.appendChild(root);
            }

            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);
            if (usuarioNode == null) {
                Element usuarioElement = doc.createElement("usuario");
                usuarioElement.setAttribute("numeroDocumento", numeroDocumento);
                root.appendChild(usuarioElement);
                usuarioNode = usuarioElement;
            }

            if (metaExiste(usuarioNode, meta.getNombre())) {
                return false;
            }

            Element metaElement = doc.createElement("meta");
            appendElement(doc, metaElement, "nombre", meta.getNombre());
            appendElement(doc, metaElement, "fechaInicio", meta.getFechaInicio());
            appendElement(doc, metaElement, "fechaFin", meta.getFechaFin());
            appendElement(doc, metaElement, "montoRequerido", meta.getMontoRequerido().toString());
            appendElement(doc, metaElement, "montoActual", "0.0");

            usuarioNode.appendChild(metaElement);
            guardarCambiosEnXML(doc);
            return true;
            
        } catch (Exception e) {
            System.err.println("ERROR al guardar meta: " + e.getMessage());
            return false;
        }
    }

    public List<MetaFinanciera> obtenerMetasPorUsuario(String numeroDocumento) {
        List<MetaFinanciera> metas = new ArrayList<>();
        
        try {
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList listaMetas = ((Element) usuarioNode).getElementsByTagName("meta");
                for (int i = 0; i < listaMetas.getLength(); i++) {
                    try {
                        Element metaElement = (Element) listaMetas.item(i);
                        MetaFinanciera meta = new MetaFinanciera();
                        
                        meta.setNombre(getElementTextContent(metaElement, "nombre"));
                        meta.setFechaInicio(getElementTextContent(metaElement, "fechaInicio"));
                        meta.setFechaFin(getElementTextContent(metaElement, "fechaFin"));
                        
                        // Manejo seguro de montos
                        String montoReq = getElementTextContent(metaElement, "montoRequerido");
                        meta.setMontoRequerido(montoReq.isEmpty() ? 0.0 : Double.parseDouble(montoReq));
                        
                        String montoAct = getElementTextContent(metaElement, "montoActual");
                        meta.setMontoActual(montoAct.isEmpty() ? 0.0 : Double.parseDouble(montoAct));
                        
                        metas.add(meta);
                    } catch (Exception e) {
                        System.err.println("Error procesando meta #" + i + ": " + e.getMessage());
                        // Continuar con la siguiente meta
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener documento XML: " + e.getMessage());
            e.printStackTrace();
        }
        
        return metas;
    }

    public boolean modificarMeta(String numeroDocumento, String nombreMeta, Map<String, Object> metaData) {
        try {
            MetaFinanciera nuevaMeta = new MetaFinanciera(
                nombreMeta,
                (String) metaData.get("fechaInicio"),
                (String) metaData.get("fechaFin"),
                Double.parseDouble(metaData.get("montoRequerido").toString())
            );
            return modificarMetaEnXML(numeroDocumento, nombreMeta, nuevaMeta);
        } catch (Exception e) {
            System.err.println("Error al modificar meta desde map: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarMetaEnXML(String numeroDocumento, String nombreMeta, MetaFinanciera nuevaMeta) {
        try {
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);
        
            if (usuarioNode == null) {
                return false;
            }
        
            NodeList metasList = ((Element) usuarioNode).getElementsByTagName("meta");
            for (int i = 0; i < metasList.getLength(); i++) {
                Element metaElement = (Element) metasList.item(i);
                String nombreExistente = getElementTextContent(metaElement, "nombre");
            
                if (nombreExistente.equals(nombreMeta)) {
                    setElementTextContent(metaElement, "fechaInicio", nuevaMeta.getFechaInicio());
                    setElementTextContent(metaElement, "fechaFin", nuevaMeta.getFechaFin());
                    setElementTextContent(metaElement, "montoRequerido", nuevaMeta.getMontoRequerido().toString());
                
                    guardarCambiosEnXML(doc);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al modificar meta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarMeta(String numeroDocumento, String nombreMeta) {
        try {
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList metas = ((Element) usuarioNode).getElementsByTagName("meta");
                for (int i = 0; i < metas.getLength(); i++) {
                    Element metaElement = (Element) metas.item(i);
                    String nombreMetaActual = getElementTextContent(metaElement, "nombre");

                    if (nombreMeta.equals(nombreMetaActual)) {
                        usuarioNode.removeChild(metaElement);
                        guardarCambiosEnXML(doc);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar meta: " + e.getMessage());
        }
        return false;
    }

    // Métodos auxiliares 
    private Document obtenerDocumentoXML() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(XML_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        if (file.exists() && file.length() > 0) {
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            return doc;
        } else {
            Document doc = dBuilder.newDocument();
            Element root = doc.createElement("metas");
            doc.appendChild(root);
            return doc;
        }
    }

    private void guardarCambiosEnXML(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(XML_FILE));
        transformer.transform(source, result);
    }

    private Node encontrarNodoUsuario(Document doc, String documento) {
        NodeList usuarios = doc.getElementsByTagName("usuario");
        for (int i = 0; i < usuarios.getLength(); i++) {
            Element usuarioElement = (Element) usuarios.item(i);
            if (usuarioElement.getAttribute("numeroDocumento").equals(documento)) {
                return usuarioElement;
            }
        }
        return null;
    }

    private boolean metaExiste(Node usuarioNode, String nombreMeta) {
        NodeList metas = ((Element) usuarioNode).getElementsByTagName("meta");
        for (int i = 0; i < metas.getLength(); i++) {
            Element metaElement = (Element) metas.item(i);
            String nombreExistente = getElementTextContent(metaElement, "nombre");
            if (nombreExistente.equals(nombreMeta)) {
                return true;
            }
        }
        return false;
    }

    private void appendElement(Document doc, Element parent, String tagName, String content) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(content));
        parent.appendChild(element);
    }

    // Método unificado para obtener contenido de elementos
    private String getElementTextContent(Element element, String tagName) {
        if (element == null || tagName == null || tagName.isEmpty()) {
            return "";
        }
        
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0 || nodes.item(0) == null) {
            return "";
        }
        
        String content = nodes.item(0).getTextContent();
        return content != null ? content.trim() : "";
    }

    // Método para establecer contenido de elementos
    private void setElementTextContent(Element parentElement, String tagName, String newContent) {
        NodeList nodes = parentElement.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            node.setTextContent(newContent);
        }
    }
}