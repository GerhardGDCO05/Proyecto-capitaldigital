package com.example.capitalDigital.usuario.beneficiary;


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
            
            
            }
            
            // Crear el nuevo beneficiario
            Element beneficiaryElement = doc.createElement("beneficiary");
            
            
            
            // Guardar cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}