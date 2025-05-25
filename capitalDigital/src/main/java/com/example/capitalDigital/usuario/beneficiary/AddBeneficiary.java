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

public class AddBeneficiary {
    /**
     * Verifica si un número de cuenta sigue el patrón de alguno de los bancos soportados
     * @param beneficiaryName Nombre del beneficiario
     * @param ID Número de identificacion del beneficiario
     * @param accountNumber Numero de cuenta bancaria del beneficiario
     * @param Bank Banco de la cuenta bancaria
     * @param path  Ruta RELATIVA del archivo .xml
     * @param holder Nombre del usuario de la cuenta bancaria a guardar el beneficiario
     * @return true si se agrego el beneficiario exitosamente en el xml, false en caso contrario
     */
    public boolean addBeneficiary(String beneficiaryName, String ID, String accountNumber,String Bank, String path,String holder) {
        ValidateBeneficiaryInfo validateBeneficiaryInfo = new ValidateBeneficiaryInfo();
        if (!validateBeneficiaryInfo.validateInfo(beneficiaryName,ID,Bank,accountNumber)) return false; //si los datos no son validos entonces no guarda el beneficiario

        try{
            //Abrir y parsear el archivo .xml
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(path);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("holder"); //obtener etiquetas del .xml denominadas holder
            boolean found = false;

            //Buscar el holder a guardar el beneficiario
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);

                NodeList nameList = element.getElementsByTagName("holderName"); //obtener etiquetas del .xml denominadas holderName
                Element holderName = (Element) nameList.item(0);
                if (holderName.getTextContent().equals(holder)){

                    NodeList beneficiaries = holderName.getElementsByTagName("beneficiary");
                    for (int j = 0; j < beneficiaries.getLength(); j++) {
                        Element beneficiary = (Element) beneficiaries.item(j);
                        String existingAccountNumber = beneficiary.getElementsByTagName("accountNumber").item(0).getTextContent();

                        if (existingAccountNumber.equals(accountNumber)) { //si ya hay un beneficiario con el mismo nro de cuenta, no agregar beneficiario al xml
                            return false;
                        }

                    }
                    //guardar nuevo beneficiario
                    Element newBeneficiary = doc.createElement("beneficiary");

                    Element beneficiaryNametxt = doc.createElement("beneficiaryName");
                    beneficiaryNametxt.appendChild(doc.createTextNode(beneficiaryName));
                    newBeneficiary.appendChild(beneficiaryNametxt);

                    Element IDtxt = doc.createElement("ID");
                    IDtxt.appendChild(doc.createTextNode(ID));
                    newBeneficiary.appendChild(IDtxt);

                    Element accountNumbertxt = doc.createElement("accountNumber");
                    accountNumbertxt.appendChild(doc.createTextNode(accountNumber));
                    newBeneficiary.appendChild(accountNumbertxt);

                    Element Banktxt = doc.createElement("bank");
                    Banktxt.appendChild(doc.createTextNode(Bank));
                    newBeneficiary.appendChild(Banktxt);

                    nList.item(i).appendChild(newBeneficiary);

                    found = true;
                    break;
                }

            }

            if (!found) return false;
            //actualizar archivo .xml
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);
            return true;

            //manejo de excepciones
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
