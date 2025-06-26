package com.example.capitalDigital.usuario.services;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.capitalDigital.usuario.models.TransactionHistoryModel;

@Service
public class TransactionHistoryService {

    private static final String XML_FILE = "C:\\Users\\Usuario\\Desktop\\proyecto IS\\capitalDigital\\src\\main\\java\\com\\example\\capitalDigital\\Info_bank\\TransactionHistory.xml";

    public boolean guardarTransactionEnXML(String numeroDocumento, TransactionHistoryModel transaction) {
        try {
            System.out.println("Guardando transacción en XML");
            File file = new File(XML_FILE);
            Document doc;
            Element root;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            if (file.exists() && file.length() > 0) {
                System.out.println("Archivo XML existe, cargando...");
                doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();
                root = doc.getDocumentElement();
            } else {
                System.out.println("Creando nuevo archivo XML...");
                doc = dBuilder.newDocument();
                root = doc.createElement("usuarios");
                doc.appendChild(root);
            }

            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode == null) {
                System.out.println("Usuario no existe, creando nuevo...");
                Element usuarioElement = doc.createElement("usuario");
                usuarioElement.setAttribute("numeroDocumento", numeroDocumento);
                root.appendChild(usuarioElement);
                usuarioNode = usuarioElement;
            }

            // Crear nodo <transaccion>
            Element transaccionElement = doc.createElement("transaccion");

            appendElement(doc, transaccionElement, "Beneficiario", transaction.getBeneficiario());
            appendElement(doc, transaccionElement, "Fecha", transaction.getFecha());
            appendElement(doc, transaccionElement, "BancoOrigen", transaction.getBancoOrigen());
            appendElement(doc, transaccionElement, "CuentaOrigen", transaction.getNumCuentaOrigen());
            appendElement(doc, transaccionElement, "BancoDestino", transaction.getBancoDestino());
            appendElement(doc, transaccionElement, "CuentaDestino", transaction.getNumCuentaDestino());
            appendElement(doc, transaccionElement, "Monto", transaction.getMonto());

            ((Element) usuarioNode).appendChild(transaccionElement);
            guardarCambiosEnXML(doc);

            System.out.println("Transacción guardada exitosamente");
            return true;

        } catch (Exception e) {
            System.err.println("Error al guardar transacción: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<TransactionHistoryModel> consultarTransacciones(String numeroDocumento) {
        List<TransactionHistoryModel> transacciones = new ArrayList<>();
        try {
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList listaTransacciones = ((Element) usuarioNode).getElementsByTagName("transaccion");
                for (int i = 0; i < listaTransacciones.getLength(); i++) {
                    Element transaccionElement = (Element) listaTransacciones.item(i);
                    TransactionHistoryModel transaction = new TransactionHistoryModel();
                    
                    transaction.setBeneficiario(getElementTextContent(transaccionElement, "Beneficiario"));
                    transaction.setBancoOrigen(getElementTextContent(transaccionElement, "BancoOrigen"));
                    transaction.setNumCuentaOrigen(getElementTextContent(transaccionElement, "CuentaOrigen"));
                    transaction.setBancoDestino(getElementTextContent(transaccionElement, "BancoDestino"));
                    transaction.setNumCuentaDestino(getElementTextContent(transaccionElement, "CuentaDestino"));
                    transaction.setMonto(getElementTextContent(transaccionElement, "Monto"));

                    // Parsear la fecha del XML y establecerla en el modelo
                    String fechaStr = getElementTextContent(transaccionElement, "Fecha");
                    if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime fechaParseada = LocalDateTime.parse(fechaStr, formatter);
                            transaction.setFechaHora(fechaParseada);
                        } catch (Exception e) {
                            System.err.println("Error al parsear fecha: " + fechaStr + " - " + e.getMessage());
                            // Si hay error parseando la fecha, mantener la fecha actual que ya se estableció en el constructor
                        }
                    }
                    // Si no hay fecha en el XML o está vacía, mantener la fecha actual que ya se estableció en el constructor

                    transacciones.add(transaction);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al consultar transacciones: " + e.getMessage());
            e.printStackTrace();
        }
        return transacciones;
    }

    private Document obtenerDocumentoXML() throws Exception {
        File file = new File(XML_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private Node encontrarNodoUsuario(Document doc, String numeroDocumento) {
        NodeList usuarios = doc.getElementsByTagName("usuario");
        for (int i = 0; i < usuarios.getLength(); i++) {
            Element usuarioElement = (Element) usuarios.item(i);
            if (usuarioElement.getAttribute("numeroDocumento").equals(numeroDocumento)) {
                return usuarioElement;
            }
        }
        return null;
    }

    private String getElementTextContent(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    private void appendElement(Document doc, Element parent, String tagName, String content) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(content));
        parent.appendChild(element);
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
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar cambios en XML", e);
        }
    }
}