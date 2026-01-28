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
import org.w3c.dom.NodeList;

import com.example.capitalDigital.usuario.models.BeneficiaryModel;

@Component
public class ModifyBeneficiary {

    public boolean updateBeneficiary(BeneficiaryModel updatedBeneficiary, String holder, String oldAccountNumber, String xmlPath) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlPath);
            doc.getDocumentElement().normalize();

            boolean encontrado = false;
            NodeList usuarios = doc.getElementsByTagName("usuario");

            for (int i = 0; i < usuarios.getLength(); i++) {
                Element usuario = (Element) usuarios.item(i);
                if (usuario.getAttribute("numeroDocumento").equals(holder)) {
                    NodeList beneficiarios = usuario.getElementsByTagName("beneficiary");
                    
                    for (int j = 0; j < beneficiarios.getLength(); j++) {
                        Element beneficiario = (Element) beneficiarios.item(j);
                        String cuentaExistente = beneficiario.getElementsByTagName("accountNumber").item(0).getTextContent();
                        
                        if (cuentaExistente.equals(oldAccountNumber)) {
                            actualizarElementoBeneficiario(beneficiario, updatedBeneficiary);
                            encontrado = true;
                            break;
                        }
                    }
                    if (encontrado) break;
                }
            }

            if (encontrado) {
                guardarDocumento(doc, xmlPath);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error modificando beneficiario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void actualizarElementoBeneficiario(Element beneficiario, BeneficiaryModel model) {
        beneficiario.getElementsByTagName("beneficiaryName").item(0).setTextContent(model.getBeneficiaryName());
        beneficiario.getElementsByTagName("ID").item(0).setTextContent(model.getID());
        beneficiario.getElementsByTagName("accountNumber").item(0).setTextContent(model.getAccountNumber());
        beneficiario.getElementsByTagName("bank").item(0).setTextContent(model.getBank());
    }

    private void guardarDocumento(Document doc, String path) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(path);
        transformer.transform(source, result);
    }
}