package com.example.capitalDigital.usuario.beneficiary;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.capitalDigital.usuario.models.BeneficiaryModel;

@Component
public class AddBeneficiary {

    public boolean persistBeneficiary(BeneficiaryModel beneficiary, String holder, String xmlPath) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlPath);

            Node usuario = obtenerONuevoNodoUsuario(doc, holder);
            Element beneficiario = crearElementoBeneficiario(doc, beneficiary);
            usuario.appendChild(beneficiario);

            guardarDocumento(doc, xmlPath);
            return true;
        } catch (Exception e) {
            System.err.println("Error agregando beneficiario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Node obtenerONuevoNodoUsuario(Document doc, String documento) {
        NodeList usuarios = doc.getElementsByTagName("usuario");
        for (int i = 0; i < usuarios.getLength(); i++) {
            Node usuario = usuarios.item(i);
            if (usuario.getAttributes().getNamedItem("numeroDocumento").getNodeValue().equals(documento)) {
                return usuario;
            }
        }

        Element nuevoUsuario = doc.createElement("usuario");
        nuevoUsuario.setAttribute("numeroDocumento", documento);
        doc.getDocumentElement().appendChild(nuevoUsuario);
        return nuevoUsuario;
    }

    private Element crearElementoBeneficiario(Document doc, BeneficiaryModel model) {
        Element beneficiario = doc.createElement("beneficiary");
        
        beneficiario.appendChild(crearElementoConTexto(doc, "beneficiaryName", model.getBeneficiaryName()));
        beneficiario.appendChild(crearElementoConTexto(doc, "ID", model.getID()));
        beneficiario.appendChild(crearElementoConTexto(doc, "accountNumber", model.getAccountNumber()));
        beneficiario.appendChild(crearElementoConTexto(doc, "bank", model.getBank()));
        
        return beneficiario;
    }

    private Element crearElementoConTexto(Document doc, String tag, String valor) {
        Element elemento = doc.createElement(tag);
        elemento.setTextContent(valor != null ? valor : "");
        return elemento;
    }

    private void guardarDocumento(Document doc, String path) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(path);
        transformer.transform(source, result);
    }
}