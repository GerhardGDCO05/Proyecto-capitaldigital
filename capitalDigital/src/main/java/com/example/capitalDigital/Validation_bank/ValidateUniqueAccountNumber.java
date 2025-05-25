package com.example.capitalDigital.Validation_bank;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ValidateUniqueAccountNumber {
    public boolean validateUniqueAccountNumber(String accountNumber, String holder,String path ) {
        try{
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(path);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("holder");

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);

                NodeList nameList = element.getElementsByTagName("holderName"); //obtener etiquetas del .xml denominadas holderName
                Element holderName = (Element) nameList.item(0);
                if (holderName.getTextContent().equals(holder)) {

                    NodeList beneficiaries = element.getElementsByTagName("beneficiary");
                    for (int j = 0; j < beneficiaries.getLength(); j++) {
                        Element beneficiary = (Element) beneficiaries.item(j);
                        String existingAccountNumber = beneficiary.getElementsByTagName("accountNumber").item(0).getTextContent();
                        if (existingAccountNumber.equals(accountNumber)) { //si ya hay un beneficiario con el mismo nro de cuenta, es el beneficiario a modificar
                            return false;
                        }

                    }
                }
            }
            return true;

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
