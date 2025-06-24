package com.example.capitalDigital.usuario.beneficiary;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.capitalDigital.Validation_bank.ValidateBeneficiaryInfo;

@Component
public class AddBeneficiary {

    @Autowired
    private ValidateBeneficiaryInfo validateBeneficiaryInfo;

    public boolean addBeneficiary(String beneficiaryName, String id, String accountNumber, String bank, String xmlPath, String holder) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlPath);

            doc.getDocumentElement().normalize();

            // Buscar el nodo <usuario> por numeroDocumento
            Node usuarioNode = encontrarNodoUsuario(doc, holder);
            if (usuarioNode == null) {
                Element root = doc.getDocumentElement();

                Element usuarioElement = doc.createElement("usuario");
                usuarioElement.setAttribute("numeroDocumento", holder);
                root.appendChild(usuarioElement);
                usuarioNode = usuarioElement;
            }

            // Crear el nuevo beneficiario
            Element beneficiaryElement = doc.createElement("beneficiary");

            beneficiaryElement.appendChild(crearElemento(doc, "beneficiaryName", beneficiaryName));
            beneficiaryElement.appendChild(crearElemento(doc, "ID", id));
            beneficiaryElement.appendChild(crearElemento(doc, "accountNumber", accountNumber));
            beneficiaryElement.appendChild(crearElemento(doc, "bank", bank));

            // Agregar al usuario encontrado o creado
            usuarioNode.appendChild(beneficiaryElement);

            // Guardar cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlPath));
            transformer.transform(source, result);

            System.out.println("✅ Beneficiario agregado exitosamente al XML");
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error al agregar beneficiario: " + e.getMessage());
            e.printStackTrace();
            return false;
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

    private Element crearElemento(Document doc, String nombreTag, String valor) {
        Element elemento = doc.createElement(nombreTag);
        elemento.setTextContent(valor != null ? valor : "");
        return elemento;
    }
}