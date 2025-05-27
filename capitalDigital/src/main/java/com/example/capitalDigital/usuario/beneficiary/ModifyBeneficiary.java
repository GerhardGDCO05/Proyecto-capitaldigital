package com.example.capitalDigital.usuario.beneficiary;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;

import com.example.capitalDigital.Validation_bank.ValidateBeneficiaryInfo;
import com.example.capitalDigital.Validation_bank.ValidateUniqueAccountNumber;

public class ModifyBeneficiary {
    /**
     * modifica el beneficiario asociado a la cuenta de un usuario
     * @param beneficiaryName Nombre del beneficiario
     * @param ID Número de identificacion del beneficiario
     * @param accountNumber Numero de cuenta bancaria del beneficiario
     * @param Bank Banco de la cuenta bancaria
     * @param path  Ruta RELATIVA del archivo .xml
     * @param holder Nombre del usuario de la cuenta bancaria a guardar el beneficiario
     * @param oldAccountNumber Numero antiguo de cuenta del beneficiario
     * @return true si se modifico el beneficiario exitosamente en el xml, false en caso contrario
     */
    public boolean modifyBeneficiary(String beneficiaryName, String ID,String accountNumber,String Bank, String path,String holder, String oldAccountNumber, String holderIdentification) {
        ValidateUniqueAccountNumber validateUniqueAccountNumber = new ValidateUniqueAccountNumber();
        if(!validateUniqueAccountNumber.validateUniqueAccountNumber(accountNumber,holder,path)) return false; //validar que no haya otro beneficiario con el mismo numero de cuenta
        ValidateBeneficiaryInfo validateBeneficiaryInfo = new ValidateBeneficiaryInfo();
        validateBeneficiaryInfo.validateInfo(beneficiaryName,ID,Bank,accountNumber);
        try{
            //Abrir y parsear el archivo .xml
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(path);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("holder"); //obtener etiquetas del .xml denominadas holder
            boolean found = false;

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);

                NodeList holderNameList = element.getElementsByTagName("holderName"); //obtener etiquetas del .xml denominadas holderName
                NodeList holderIDList = element.getElementsByTagName("holderID"); //obtener etiquetas del .xml denominadas holderID
                Element holderName = (Element) holderNameList.item(0);
                Element holderID = (Element) holderIDList.item(0);
                if (holderName.getTextContent().equals(holder) && holderID.getTextContent().equals(holderIdentification)) { //si se encuentra el holder, se procede a buscar el beneficiario a modificar

                    NodeList beneficiaries = element.getElementsByTagName("beneficiary");
                    for (int j = 0; j < beneficiaries.getLength(); j++) {
                        Element beneficiary = (Element) beneficiaries.item(j);
                        String existingAccountNumber = beneficiary.getElementsByTagName("accountNumber").item(0).getTextContent();

                        if (existingAccountNumber.equals(oldAccountNumber)) { //si ya hay un beneficiario con el mismo nro de cuenta, es el beneficiario a modificar
                            element.getElementsByTagName("beneficiaryName").item(j).setTextContent(beneficiaryName);
                            element.getElementsByTagName("ID").item(j).setTextContent(ID);
                            element.getElementsByTagName("accountNumber").item(j).setTextContent(accountNumber);
                            element.getElementsByTagName("bank").item(j).setTextContent(Bank);
                        }

                    }
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(path);
            transformer.transform(source, result);
            return true;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

    }
}
