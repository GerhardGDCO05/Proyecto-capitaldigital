package com.example.capitalDigital.usuario.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private static final String XML_FILE = "C:\\Users\\Usuario\\Desktop\\proyecto IS\\capitalDigital\\src\\main\\java\\com\\example\\capitalDigital\\Info_bank\\Metas.xml";

    // Guardar meta
    public boolean guardarMetaEnXML(String numeroDocumento, MetaFinanciera meta) {
    try {
        System.out.println("=== GUARDANDO META EN XML ===");
        System.out.println("Número de documento: " + numeroDocumento);
        System.out.println("Nombre meta: " + meta.getNombre());
        
        File file = new File(XML_FILE);
        Document doc;
        Element root;

        if (file.exists() && file.length() > 0) {
            System.out.println("Archivo XML existe, cargando...");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            root = doc.getDocumentElement();
        } else {
            System.out.println("Creando nuevo archivo XML...");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
            root = doc.createElement("metas");
            doc.appendChild(root);
        }

        Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);
        if (usuarioNode == null) {
            System.out.println("Usuario no existe, creando nuevo...");
            Element usuarioElement = doc.createElement("usuario");
            usuarioElement.setAttribute("numeroDocumento", numeroDocumento);
            root.appendChild(usuarioElement);
            usuarioNode = usuarioElement;
        } else {
            System.out.println("Usuario encontrado");
        }

        // Verificar si la meta ya existe
        if (metaExiste(usuarioNode, meta.getNombre())) {
            System.out.println("ERROR: Ya existe una meta con ese nombre");
            return false;
        }

        // Crear nodo <meta>
        Element metaElement = doc.createElement("meta");

        appendElement(doc, metaElement, "nombre", meta.getNombre());
        appendElement(doc, metaElement, "fechaInicio", meta.getFechaInicio());
        appendElement(doc, metaElement, "fechaFin", meta.getFechaFin());
        appendElement(doc, metaElement, "montoRequerido", meta.getMontoRequerido().toString());
        appendElement(doc, metaElement, "montoActual", "0.0");

        usuarioNode.appendChild(metaElement);
        guardarCambiosEnXML(doc);
        
        System.out.println("Meta guardada exitosamente");
        return true;
        
    } catch (Exception e) {
        System.err.println("ERROR al guardar meta: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    // Verificar si ya existe una meta con ese nombre
    public boolean metaExiste(Node usuarioNode, String nombreMeta) {
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

    // Obtener todas las metas de un usuario
    public List<MetaFinanciera> obtenerMetasPorUsuario(String numeroDocumento) {
        List<MetaFinanciera> metas = new ArrayList<>();
        try {
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList listaMetas = ((Element) usuarioNode).getElementsByTagName("meta");
                for (int i = 0; i < listaMetas.getLength(); i++) {
                    Element metaElement = (Element) listaMetas.item(i);
                    String nombre = getElementTextContent(metaElement, "nombre");
                    String fechaInicio = getElementTextContent(metaElement, "fechaInicio");
                    String fechaFin = getElementTextContent(metaElement, "fechaFin");
                    Double montoRequerido = Double.parseDouble(getElementTextContent(metaElement, "montoRequerido"));

                    metas.add(new MetaFinanciera(nombre, fechaInicio, fechaFin, montoRequerido));
                }
            }

            return metas;

        } catch (Exception e) {
            System.err.println("Error al obtener metas: " + e.getMessage());
            return metas;
        }
    }

    // Métodos auxiliares

   // Método corregido para obtenerDocumentoXML
    private Document obtenerDocumentoXML() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(XML_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        if (file.exists() && file.length() > 0) {
            System.out.println("Cargando documento XML existente");
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            return doc;
        } else {
            System.out.println("Creando nuevo documento XML");
            Document doc = dBuilder.newDocument();
            Element root = doc.createElement("metas");
            doc.appendChild(root);
            return doc;
        }
    }

    private void guardarCambiosEnXML(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(XML_FILE));

            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException("Error al guardar cambios en XML", e);
        }
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

    private void appendElement(Document doc, Element parent, String tagName, String content) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(content));
        parent.appendChild(element);
    }

    private String getElementTextContent(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    public boolean modificarMetaEnXML(String numeroDocumento, String nombreMeta, MetaFinanciera nuevaMeta) {
        try {
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);
        
            if (usuarioNode == null) {
                System.out.println("Usuario no encontrado");
                return false;
            }
        
            NodeList metasList = ((Element) usuarioNode).getElementsByTagName("meta");
            for (int i = 0; i < metasList.getLength(); i++) {
                Element metaElement = (Element) metasList.item(i);
                String nombreExistente = getElementTextContent(metaElement, "nombre");
            
                if (nombreExistente.equals(nombreMeta)) {
                    // Actualizar datos
                    setElementTextContent(doc, metaElement, "fechaInicio", nuevaMeta.getFechaInicio());
                    setElementTextContent(doc, metaElement, "fechaFin", nuevaMeta.getFechaFin());
                    setElementTextContent(doc, metaElement, "montoRequerido", nuevaMeta.getMontoRequerido().toString());
                
                    guardarCambiosEnXML(doc);
                    return true;
                }
            }
        
            System.out.println("Meta no encontrada");
            return false;
        
        } catch (Exception e) {
            System.err.println("Error al modificar meta: " + e.getMessage());
            return false;
        }
    }

    // Método auxiliar
    private void setElementTextContent(Document doc, Element parent, String tagName, String content) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            nodes.item(0).setTextContent(content != null ? content : "");
        } else {
            Element element = doc.createElement(tagName);
            element.appendChild(doc.createTextNode(content != null ? content : ""));
            parent.appendChild(element);
        }
    }

    public boolean eliminarMeta(String numeroDocumento, String nombreMeta) {
        try {
            System.out.println("Eliminando meta: " + nombreMeta + " para documento: " + numeroDocumento);
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList metas = ((Element) usuarioNode).getElementsByTagName("meta");
                for (int i = 0; i < metas.getLength(); i++) {
                    Element metaElement = (Element) metas.item(i);
                    String nombreMetaActual = getElementTextContent(metaElement, "nombre"); // Cambiado de "nombreMeta" a "nombre"

                    if (nombreMeta.equals(nombreMetaActual)) {
                        usuarioNode.removeChild(metaElement);
                        guardarCambiosEnXML(doc);
                        System.out.println("Meta eliminada exitosamente");
                        return true;
                    }
                }
                System.out.println("Meta no encontrada para eliminar");
            } else {
                System.out.println("Usuario no encontrado para eliminar meta");
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar meta: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
}