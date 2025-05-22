package com.example.capitalDigital.usuario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import com.example.capitalDigital.Validation_bank.BancoService;
import com.example.capitalDigital.usuario.models.CuentaModel;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class CuentaService {

    @Autowired
    private BancoService bancoService;

    // Ruta específica para el archivo XML de cuentas
    private static final String XML_FILE = "C:\\Users\\Usuario\\Desktop\\proyecto IS\\capitalDigital\\src\\main\\java\\com\\example\\capitalDigital\\info_bank\\UserCuentas.xml";

    // Guardar cuenta en XML (POST)
    public boolean guardarCuentaEnXML(String numeroDocumento, CuentaModel cuenta) {
        try {
            System.out.println("Iniciando guardado de cuenta para documento: " + numeroDocumento);
            System.out.println("Datos de cuenta: Banco=" + cuenta.getBanco() + ", Número=" + cuenta.getNumeroCuenta());

            // Validar el número de cuenta según el banco
            if (!bancoService.validarNumeroCuenta(cuenta.getBanco(), cuenta.getNumeroCuenta())) {
                System.out.println("Número de cuenta inválido para el banco especificado");
                return false;
            }

            Document doc = obtenerDocumentoXML();
            Element root = doc.getDocumentElement();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode == null) {
                System.out.println("Usuario no encontrado, creando nuevo usuario");
                Element usuarioElement = doc.createElement("usuario");
                usuarioElement.setAttribute("numeroDocumento", numeroDocumento);
                root.appendChild(usuarioElement);
                usuarioNode = usuarioElement;
            } else {
                System.out.println("Usuario encontrado, agregando cuenta");
            }

            // Verificar si la cuenta ya existe
            if (cuentaExiste(usuarioNode, cuenta.getNumeroCuenta())) {
                System.out.println("La cuenta ya existe para este usuario");
                return false;
            }

            Element cuentaElement = doc.createElement("cuenta");
            cuentaElement.appendChild(crearElemento(doc, "banco", cuenta.getBanco()));
            cuentaElement.appendChild(crearElemento(doc, "numeroCuenta", cuenta.getNumeroCuenta()));

            usuarioNode.appendChild(cuentaElement);
            guardarCambiosEnXML(doc);

            System.out.println("Cuenta guardada exitosamente");
            return true;

        } catch (Exception e) {
            System.err.println("Error al guardar cuenta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Verificar si una cuenta ya existe para un usuario
    private boolean cuentaExiste(Node usuarioNode, String numeroCuenta) {
        if (usuarioNode == null) return false;

        NodeList cuentas = ((Element) usuarioNode).getElementsByTagName("cuenta");
        for (int i = 0; i < cuentas.getLength(); i++) {
            Element cuentaElement = (Element) cuentas.item(i);
            String numCuentaExistente = getElementTextContent(cuentaElement, "numeroCuenta");
            if (numCuentaExistente.equals(numeroCuenta)) {
                return true;
            }
        }
        return false;
    }

    // Obtener cuentas por número de documento (GET)
    public List<CuentaModel> obtenerCuentasPorNumeroDocumento(String numeroDocumento) {
        List<CuentaModel> cuentas = new ArrayList<>();
        try {
            System.out.println("Buscando cuentas para documento: " + numeroDocumento);
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList listaCuentas = ((Element) usuarioNode).getElementsByTagName("cuenta");
                System.out.println("Cuentas encontradas: " + listaCuentas.getLength());

                for (int i = 0; i < listaCuentas.getLength(); i++) {
                    Element cuentaElement = (Element) listaCuentas.item(i);
                    String banco = getElementTextContent(cuentaElement, "banco");
                    String numeroCuenta = getElementTextContent(cuentaElement, "numeroCuenta");
                    cuentas.add(new CuentaModel(banco, numeroCuenta));
                }
            } else {
                System.out.println("Usuario no encontrado");
            }

        } catch (Exception e) {
            System.err.println("Error al obtener cuentas: " + e.getMessage());
            e.printStackTrace();
        }
        return cuentas;
    }

    // Obtener todas las cuentas (GET)
    public List<CuentaModel> obtenerTodasLasCuentas() {
        List<CuentaModel> cuentas = new ArrayList<>();
        try {
            System.out.println("Obteniendo todas las cuentas");
            Document doc = obtenerDocumentoXML();
            NodeList usuarioNodes = doc.getElementsByTagName("usuario");

            for (int i = 0; i < usuarioNodes.getLength(); i++) {
                Element usuarioElement = (Element) usuarioNodes.item(i);
                NodeList listaCuentas = usuarioElement.getElementsByTagName("cuenta");

                for (int j = 0; j < listaCuentas.getLength(); j++) {
                    Element cuentaElement = (Element) listaCuentas.item(j);
                    String banco = getElementTextContent(cuentaElement, "banco");
                    String numeroCuenta = getElementTextContent(cuentaElement, "numeroCuenta");
                    cuentas.add(new CuentaModel(banco, numeroCuenta));
                }
            }

            System.out.println("Total de cuentas encontradas: " + cuentas.size());
            return cuentas;

        } catch (Exception e) {
            System.err.println("Error al obtener todas las cuentas: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Modificar cuenta en XML (PUT)
    public boolean modificarCuentaEnXML(String numeroDocumento, String numeroCuenta, CuentaModel nuevaCuenta) {
        try {
            System.out.println("Modificando cuenta: " + numeroCuenta + " para documento: " + numeroDocumento);

            // Validar el nuevo número de cuenta según el banco
            if (!bancoService.validarNumeroCuenta(nuevaCuenta.getBanco(), nuevaCuenta.getNumeroCuenta())) {
                System.out.println("Número de cuenta inválido para el banco especificado");
                return false;
            }

            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList cuentas = ((Element) usuarioNode).getElementsByTagName("cuenta");

                for (int i = 0; i < cuentas.getLength(); i++) {
                    Element cuentaElement = (Element) cuentas.item(i);
                    String numCuentaActual = getElementTextContent(cuentaElement, "numeroCuenta");

                    if (numCuentaActual.equals(numeroCuenta)) {
                        setElementTextContent(doc, cuentaElement, "banco", nuevaCuenta.getBanco());
                        setElementTextContent(doc, cuentaElement, "numeroCuenta", nuevaCuenta.getNumeroCuenta());
                        guardarCambiosEnXML(doc);
                        System.out.println("Cuenta modificada exitosamente");
                        return true;
                    }
                }
                System.out.println("Cuenta no encontrada para modificar");
            } else {
                System.out.println("Usuario no encontrado para modificar cuenta");
            }
        } catch (Exception e) {
            System.err.println("Error al modificar cuenta: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar cuenta en XML (DELETE)
    public boolean eliminarCuentaEnXML(String numeroDocumento, String numeroCuenta) {
        try {
            System.out.println("Eliminando cuenta: " + numeroCuenta + " para documento: " + numeroDocumento);
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList cuentas = ((Element) usuarioNode).getElementsByTagName("cuenta");

                for (int i = 0; i < cuentas.getLength(); i++) {
                    Element cuentaElement = (Element) cuentas.item(i);
                    String numCuentaActual = getElementTextContent(cuentaElement, "numeroCuenta");

                    if (numCuentaActual.equals(numeroCuenta)) {
                        usuarioNode.removeChild(cuentaElement);
                        guardarCambiosEnXML(doc);
                        System.out.println("Cuenta eliminada exitosamente");
                        return true;
                    }
                }
                System.out.println("Cuenta no encontrada para eliminar");
            } else {
                System.out.println("Usuario no encontrado para eliminar cuenta");
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar cuenta: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Métodos auxiliares
    private Document obtenerDocumentoXML() throws Exception {
        File file = new File(XML_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        if (file.exists() && file.length() > 0) {
            System.out.println("Archivo XML existe, cargando...");
            return dBuilder.parse(file);
        } else {
            System.out.println("Archivo XML no existe o está vacío, creando nuevo documento");
            return crearDocumentoVacio(dBuilder, file);
        }
    }

    private Document crearDocumentoVacio(DocumentBuilder dBuilder, File file) {
        try {
            // Crear directorios si no existen
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                System.out.println("Directorio creado: " + dirCreated);
            }

            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("usuarios");
            doc.appendChild(rootElement);

            // Guardar el documento vacío
            guardarCambiosEnXML(doc);

            System.out.println("Archivo XML creado correctamente en: " + file.getAbsolutePath());
            return doc;
        } catch (Exception e) {
            System.err.println("Error al crear documento vacío: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void guardarCambiosEnXML(Document doc) {
        try {
            File file = new File(XML_FILE);

            // Crear directorios si no existen
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                System.out.println("Directorio creado: " + dirCreated);
            }

            // Crear archivo si no existe
            if (!file.exists()) {
                boolean fileCreated = file.createNewFile();
                System.out.println("Archivo creado: " + fileCreated);
                System.out.println("Ruta completa del archivo: " + file.getAbsolutePath());
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            System.out.println("Archivo XML guardado correctamente en: " + file.getAbsolutePath());
            System.out.println("Tamaño del archivo: " + file.length() + " bytes");

        } catch (Exception e) {
            System.err.println("Error al guardar el archivo XML: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar el archivo XML: " + e.getMessage());
        }
    }

    private Node encontrarNodoUsuario(Document doc, String numeroDocumento) {
        NodeList usuarios = doc.getElementsByTagName("usuario");
        for (int i = 0; i < usuarios.getLength(); i++) {
            Element usuarioElement = (Element) usuarios.item(i);
            if (usuarioElement.getAttribute("numeroDocumento").equals(numeroDocumento)) {
                return usuarioElement;
            }
        }
        return null;
    }

    private String getElementTextContent(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    private void setElementTextContent(Document doc, Element element, String tagName, String value) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            nodes.item(0).setTextContent(value);
        } else {
            element.appendChild(crearElemento(doc, tagName, value));
        }
    }

    private Element crearElemento(Document doc, String nombre, String valor) {
        Element element = doc.createElement(nombre);
        element.appendChild(doc.createTextNode(valor != null ? valor : ""));
        return element;
    }
}
