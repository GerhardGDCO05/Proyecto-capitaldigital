package com.example.capitalDigital.usuario.beneficiary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.capitalDigital.Validation_bank.ValidateBeneficiaryInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
            
            
            NodeList holderList = doc.getElementsByTagName("holder");
            Element holderElement = null;
            
            for (int i = 0; i < holderList.getLength(); i++) {
                Element element = (Element) holderList.item(i);
                NodeList nameList = element.getElementsByTagName("holderName");
                Element holderNameElement = (Element) nameList.item(0);
                
                if (holderNameElement.getTextContent().equals(holder)) {
                    holderElement = element;
                    break;
                }
            }
            
            // Si no existe el holder, crearlo
            if (holderElement == null) {
                holderElement = doc.createElement("holder");
                Element holderNameElement = doc.createElement("holderName");
                holderNameElement.setTextContent(holder);
                holderElement.appendChild(holderNameElement);
                doc.getDocumentElement().appendChild(holderElement);
            }
            
            // Crear el nuevo beneficiario
            Element beneficiaryElement = doc.createElement("beneficiary");
            
            Element nameElement = doc.createElement("beneficiaryName");
            nameElement.setTextContent(beneficiaryName);
            beneficiaryElement.appendChild(nameElement);
            
            Element idElement = doc.createElement("ID");
            idElement.setTextContent(id);
            beneficiaryElement.appendChild(idElement);
            
            Element accountElement = doc.createElement("accountNumber");
            accountElement.setTextContent(accountNumber);
            beneficiaryElement.appendChild(accountElement);
            
            Element bankElement = doc.createElement("bank");
            bankElement.setTextContent(bank);
            beneficiaryElement.appendChild(bankElement);
            
            // Agregar el beneficiario al holder
            holderElement.appendChild(beneficiaryElement);
            
            // Guardar cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlPath);
            transformer.transform(source, result);
            
            System.out.println("Beneficiario agregado exitosamente al XML");
            return true;
            
        } catch (Exception e) {
            System.out.println("Error al agregar beneficiario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}