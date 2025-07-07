package com.example.capitalDigital.usuario.services;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
        // Validación usando los métodos reales que tienes implementados
        if (!validateBeneficiaryInfo.validateInfo(beneficiaryName, id, bank, accountNumber)) {
            System.out.println("Error: Datos del beneficiario no válidos");
            return false;
        }

        if (!validateUniqueAccountNumber.validateUniqueAccountNumber(accountNumber, holder, XML_FILE)) {
            System.out.println("Error: El número de cuenta ya existe");
            return false;
        }

        BeneficiaryModel beneficiary = new BeneficiaryModel(beneficiaryName, id, accountNumber, bank);
        return addBeneficiary.persistBeneficiary(beneficiary,holder,XML_FILE);
    }

    public boolean modifyBeneficiary(String beneficiaryName, String id, String accountNumber, String bank, String holder, String oldAccountNumber) {
        // Validación usando los métodos reales
        if (!validateBeneficiaryInfo.validateInfo(beneficiaryName, id, bank, accountNumber)) {
            return false;
        }

        // Solo validar unicidad si el número de cuenta cambió
        if (!accountNumber.equals(oldAccountNumber)) {
            if (!validateUniqueAccountNumber.validateUniqueAccountNumber(accountNumber, holder, XML_FILE)) {
                return false;
            }
        }
        BeneficiaryModel Nuevobeneficiary = new BeneficiaryModel(beneficiaryName, id, accountNumber, bank);
        return modifyBeneficiary.updateBeneficiary(Nuevobeneficiary, holder, oldAccountNumber,XML_FILE);
    }
    
    public boolean deleteBeneficiary(String holder, String accountNumber) {
        try {
            System.out.println("=== INICIANDO ELIMINACIÓN ===");
            System.out.println("Holder recibido: " + holder);
            System.out.println("Account Number recibido: " + accountNumber);
            
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(XML_FILE);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("usuario");
            System.out.println("Total de usuarios encontrados: " + nList.getLength());

            boolean beneficiaryFound = false;

            for (int i = 0; i < nList.getLength(); i++) {
                Element usuarioElement = (Element) nList.item(i);
                String usuarioNumeroDocumento = usuarioElement.getAttribute("numeroDocumento");

                System.out.println("Comparando numeroDocumento XML: '" + usuarioNumeroDocumento + "' con holder: '" + holder + "'");

                if (usuarioNumeroDocumento.equals(holder)) {
                    System.out.println("¡Usuario encontrado!");
                    NodeList beneficiaries = usuarioElement.getElementsByTagName("beneficiary");
                    System.out.println("Beneficiarios encontrados para este usuario: " + beneficiaries.getLength());

                    for (int j = 0; j < beneficiaries.getLength(); j++) {
                        Element beneficiary = (Element) beneficiaries.item(j);
                        String existingAccountNumber = beneficiary.getElementsByTagName("accountNumber").item(0).getTextContent();

                        System.out.println("Comparando accountNumber XML: '" + existingAccountNumber + "' con: '" + accountNumber + "'");

                        if (existingAccountNumber.equals(accountNumber)) {
                            System.out.println("¡Beneficiario encontrado! Eliminando...");
                            usuarioElement.removeChild(beneficiary);
                            beneficiaryFound = true;
                            break;
                        }
                    }
                    
                    if (beneficiaryFound) {
                        break;
                    }
                }
            }

            if (beneficiaryFound) {
                System.out.println("Guardando cambios en el XML...");
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(XML_FILE);
                transformer.transform(source, result);
                System.out.println("¡Cambios guardados exitosamente!");
            } else {
                System.out.println("ERROR: Beneficiario no encontrado");
            }

            return beneficiaryFound;
        } catch (Exception e) {
            System.out.println("ERROR en deleteBeneficiary: " + e.getMessage());
            e.printStackTrace();
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

    public BeneficiaryModel getBeneficiary(String holder, String accountNumber) {
        try {
            System.out.println("=== BUSCANDO BENEFICIARIO ===");
            System.out.println("Holder: " + holder);
            System.out.println("Account Number: " + accountNumber);
            
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(XML_FILE);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("usuario");

            for (int i = 0; i < nList.getLength(); i++) {
                Element usuarioElement = (Element) nList.item(i);
                String usuarioNumeroDocumento = usuarioElement.getAttribute("numeroDocumento");

                System.out.println("Comparando numeroDocumento: " + usuarioNumeroDocumento + " con holder: " + holder);

                if (usuarioNumeroDocumento.equals(holder)) {
                    System.out.println("Usuario encontrado, buscando beneficiario...");
                    NodeList beneficiaryList = usuarioElement.getElementsByTagName("beneficiary");
                    
                    for (int j = 0; j < beneficiaryList.getLength(); j++) {
                        Element beneficiary = (Element) beneficiaryList.item(j);
                        String existingAccountNumber = beneficiary.getElementsByTagName("accountNumber").item(0).getTextContent();

                        System.out.println("Comparando account: " + existingAccountNumber + " con: " + accountNumber);

                        if (existingAccountNumber.equals(accountNumber)) {
                            String beneficiaryName = beneficiary.getElementsByTagName("beneficiaryName").item(0).getTextContent();
                            String ID = getElementTextContent(beneficiary, "ID");
                            String bank = beneficiary.getElementsByTagName("bank").item(0).getTextContent();

                            System.out.println("¡Beneficiario encontrado!");
                            return new BeneficiaryModel(beneficiaryName, ID, accountNumber, bank);
                        }
                    }
                }
            }
            
            System.out.println("Beneficiario no encontrado");
            return null;
        } catch (Exception e) {
            System.out.println("ERROR en getBeneficiary: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener beneficiario: " + e.getMessage(), e);
        }
    }

    private String getElementTextContent(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
            return nodes.item(0).getFirstChild().getNodeValue();
        }
        return "";
    }
}