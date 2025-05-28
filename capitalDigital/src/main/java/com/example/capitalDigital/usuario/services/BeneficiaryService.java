package com.example.capitalDigital.usuario.services;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.capitalDigital.Validation_bank.ValidateBeneficiaryInfo;
import com.example.capitalDigital.Validation_bank.ValidateUniqueAccountNumber;
import com.example.capitalDigital.usuario.beneficiary.AddBeneficiary;
import com.example.capitalDigital.usuario.beneficiary.ModifyBeneficiary;
import com.example.capitalDigital.usuario.models.BeneficiaryModel;

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

    private static final String XML_FILE = "C:\\Users\\simon\\OneDrive\\Escritorio\\CapitalDigitalISProyect\\Proyecto-capitaldigital\\capitalDigital\\src\\main\\java\\com\\example\\capitalDigital\\Info_bank\\AccountBeneficiary.xml";

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

    public List<BeneficiaryModel> getBeneficiariesByDocumento(String documento) {
        List<BeneficiaryModel> beneficiaries = new ArrayList<>();
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(XML_FILE);
            doc.getDocumentElement().normalize();
        
            NodeList usuarios = doc.getElementsByTagName("usuario");
            for (int i = 0; i < usuarios.getLength(); i++) {
                Element usuarioElement = (Element) usuarios.item(i);
                String xmlDocumento = usuarioElement.getAttribute("numeroDocumento");
            
                if (xmlDocumento.equals(documento)) {
                    NodeList beneficiaryList = usuarioElement.getElementsByTagName("beneficiary");
                    for (int j = 0; j < beneficiaryList.getLength(); j++) {
                        Element benef = (Element) beneficiaryList.item(j);
                    
                        String name = getElementTextContent(benef, "beneficiaryName");
                        String id = getElementTextContent(benef, "ID");
                        String account = getElementTextContent(benef, "accountNumber");
                        String bank = getElementTextContent(benef, "bank");
                    
                        beneficiaries.add(new BeneficiaryModel(name, id, account, bank));
                    }
                    break;
                }
            }
        
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener beneficiarios: " + e.getMessage(), e);
        }
    
        return beneficiaries;
    }

    private String getElementTextContent(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
            return nodes.item(0).getFirstChild().getNodeValue();
        }
        return "";
    }

    public BeneficiaryModel getBeneficiary(String holder, String accountNumber) {
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

                            return new BeneficiaryModel(beneficiaryName, ID, accountNumber, bank);
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