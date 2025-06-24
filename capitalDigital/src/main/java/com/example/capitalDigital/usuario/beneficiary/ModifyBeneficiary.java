package com.example.capitalDigital.usuario.beneficiary;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.capitalDigital.Validation_bank.ValidateBeneficiaryInfo;
import com.example.capitalDigital.Validation_bank.ValidateUniqueAccountNumber;

@Component
public class ModifyBeneficiary {

    @Autowired
    private ValidateBeneficiaryInfo validateBeneficiaryInfo;

    @Autowired
    private ValidateUniqueAccountNumber validateUniqueAccountNumber;

    // Ruta del archivo XML donde se guardan los beneficiarios
    private static final String XML_FILE = "C:\\Users\\simon\\OneDrive\\Escritorio\\CapitalDigitalISProyect\\Proyecto-capitaldigital\\capitalDigital\\src\\main\\java\\com\\example\\capitalDigital\\Info_bank\\AccountBeneficiary.xml";

    /**
     * Modifica un beneficiario existente en el archivo XML.
     *
     * @param beneficiaryName Nombre del beneficiario
     * @param ID Número de identificación del beneficiario
     * @param accountNumber Número de cuenta bancaria del beneficiario
     * @param bank Banco del beneficiario
     * @param holder Nombre del titular de la cuenta
     * @param oldAccountNumber Número de cuenta anterior (para buscar el beneficiario a modificar)
     * @return true si la modificación fue exitosa, false en caso contrario
     */
    public boolean modifyBeneficiary(String beneficiaryName, String ID, String accountNumber, String bank, String path, String holder, String oldAccountNumber) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(path);
            doc.getDocumentElement().normalize();
        
            NodeList usuarios = doc.getElementsByTagName("usuario");
            boolean found = false;
        
            for (int i = 0; i < usuarios.getLength(); i++) {
                Element usuario = (Element) usuarios.item(i);
            
                // Buscar el usuario por su atributo "numeroDocumento"
                String documentoUsuario = usuario.getAttribute("numeroDocumento");
            
                if (documentoUsuario.equals(holder)) {
                
                    NodeList beneficiaries = usuario.getElementsByTagName("beneficiary");
                
                    for (int j = 0; j < beneficiaries.getLength(); j++) {
                        Element beneficiary = (Element) beneficiaries.item(j);
                    
                        String existingAccount = beneficiary.getElementsByTagName("accountNumber").item(0).getTextContent();
                    
                        if (existingAccount.equals(oldAccountNumber)) {
                            beneficiary.getElementsByTagName("beneficiaryName").item(0).setTextContent(beneficiaryName);
                            beneficiary.getElementsByTagName("ID").item(0).setTextContent(ID);
                            beneficiary.getElementsByTagName("accountNumber").item(0).setTextContent(accountNumber);
                            beneficiary.getElementsByTagName("bank").item(0).setTextContent(bank);
                        
                            found = true;
                            break;
                        }
                    }
                
                    if (found) break;
                }
            }
        
            if (!found) {
                System.out.println("❌ No se encontró el beneficiario con la cuenta: " + oldAccountNumber);
                return false;
            }
        
            // Guardar cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);
        
            return true;
        
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
