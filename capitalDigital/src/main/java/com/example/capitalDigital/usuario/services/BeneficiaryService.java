package com.example.capitalDigital.usuario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.capitalDigital.Validation_bank.ValidateBeneficiaryInfo;
import com.example.capitalDigital.Validation_bank.ValidateUniqueAccountNumber;
import com.example.capitalDigital.usuario.beneficiary.AddBeneficiary;
import com.example.capitalDigital.usuario.beneficiary.ModifyBeneficiary;

@Service
public class BeneficiaryService {

    public BeneficiaryService() {
        System.out.println("BeneficiaryService ha sido inicializado correctamente.");
    }

    @Autowired
    private AddBeneficiary addBeneficiary;

    @Autowired
    private ModifyBeneficiary modifyBeneficiary;

    @Autowired
    private ValidateUniqueAccountNumber validateUniqueAccountNumber;

    @Autowired
    private ValidateBeneficiaryInfo validateBeneficiaryInfo;


    public boolean addBeneficiary(String beneficiaryName, String id, String accountNumber, String bank, String holder) {
        if (addBeneficiary == null) {
            System.out.println("Error: `AddBeneficiary` no está correctamente inyectado en `BeneficiaryService`.");
            return false;
        }
        System.out.println("Ejecutando addBeneficiary...");
        
        boolean infoValida = validateBeneficiaryInfo.validateInfo(beneficiaryName, id, bank, accountNumber);
        System.out.println("Validación de datos: " + infoValida);

        if (!infoValida) {
            System.out.println("Error: Datos inválidos detectados en validateInfo()");
            return false;
        }

        boolean cuentaUnica = validateUniqueAccountNumber.validateUniqueAccountNumber(accountNumber, holder, XML_FILE);
        System.out.println("Validación de número de cuenta único: " + cuentaUnica);

        if (!cuentaUnica) {
            System.out.println("Error: La cuenta ya existe en el XML.");
            return false;
        }

        boolean agregado = addBeneficiary.addBeneficiary(beneficiaryName, id, accountNumber, bank, XML_FILE, holder);
        System.out.println("Resultado de la persistencia en XML: " + agregado);

        return agregado;
    }

    public boolean modifyBeneficiary(String beneficiaryName, String id, String accountNumber, String bank, String holder, String oldAccountNumber) {
        if (!validateBeneficiaryInfo.validateInfo(beneficiaryName, id, bank, accountNumber)) {
            return false;
        }

        if (!validateUniqueAccountNumber.validateUniqueAccountNumber(accountNumber, holder, XML_FILE)) {
            return false;
        }

        return modifyBeneficiary.modifyBeneficiary(beneficiaryName, id, accountNumber, bank, XML_FILE, holder, oldAccountNumber);
    }

    public boolean deleteBeneficiary(String holder, String accountNumber) {
        try {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(XML_FILE);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("holder");

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);

                NodeList nameList = element.getElementsByTagName("holderName");
                Element holderName = (Element) nameList.item(0);
                if (holderName.getTextContent().equals(holder)) {

                    NodeList beneficiaries = element.getElementsByTagName("beneficiary");
                    for (int j = 0; j < beneficiaries.getLength(); j++) {
                        Element beneficiary = (Element) beneficiaries.item(j);
                        String existingAccountNumber = beneficiary.getElementsByTagName("accountNumber").item(0).getTextContent();

                        if (existingAccountNumber.equals(accountNumber)) {
                            element.removeChild(beneficiary);
                            break;
                        }
                    }
                }
            }

            // Guardar cambios en el archivo XML
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(XML_FILE);
            transformer.transform(source, result);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar beneficiario: " + e.getMessage(), e);
        }
    }

        try {
            doc.getDocumentElement().normalize();


                    for (int j = 0; j < beneficiaryList.getLength(); j++) {


                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener beneficiarios: " + e.getMessage(), e);
        }
        return beneficiaries;
    }

        try {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(XML_FILE);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("holder");

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);

                NodeList nameList = element.getElementsByTagName("holderName");
                Element holderName = (Element) nameList.item(0);
                if (holderName.getTextContent().equals(holder)) {

                    NodeList beneficiaryList = element.getElementsByTagName("beneficiary");
                    for (int j = 0; j < beneficiaryList.getLength(); j++) {
                        Element beneficiary = (Element) beneficiaryList.item(j);
                        String existingAccountNumber = beneficiary.getElementsByTagName("accountNumber").item(0).getTextContent();

                        if (existingAccountNumber.equals(accountNumber)) {
                            String beneficiaryName = beneficiary.getElementsByTagName("beneficiaryName").item(0).getTextContent();
                            String ID = beneficiary.getElementsByTagName("ID").item(0).getTextContent();
                            String bank = beneficiary.getElementsByTagName("bank").item(0).getTextContent();

                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener beneficiario: " + e.getMessage(), e);
        }
        return null;
    }
}