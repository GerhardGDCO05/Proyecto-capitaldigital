package com.example.capitalDigital.usuario.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.capitalDigital.Validation_bank.BancoService;
import com.example.capitalDigital.usuario.models.CuentaModel;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class CuentaService {

    @Autowired
    private BancoService bancoService;
    @Autowired
    private Validator validator;

    private static final String XML_FILE = "C:\\Users\\simon\\OneDrive\\Escritorio\\CapitalDigitalISProyect\\Proyecto-capitaldigital\\capitalDigital\\src\\main\\java\\com\\example\\capitalDigital\\Info_bank\\UserCuentas.xml";
    private static final String TXT_FILE = "C:\\Users\\simon\\OneDrive\\Escritorio\\CapitalDigitalISProyect\\Proyecto-capitaldigital\\proyecto-bank\\public\\data\\ListaCuentasDetalladasBancos.txt";

    // Guardar cuenta en XML (POST)
    public boolean guardarCuentaEnXML(String numeroDocumento, Map<String, Object> datosCuenta) {
        try {
            System.out.println("Iniciando guardado de cuenta para documento: " + numeroDocumento);
            System.out.println("Datos de cuenta recibidos: " + datosCuenta);

            // Crear y mapear la instancia de CuentaModel
            CuentaModel cuenta = new CuentaModel();
            
            // Mapear los datos del Map al objeto CuentaModel
            if (datosCuenta.get("banco") != null) {
                cuenta.setBanco((String) datosCuenta.get("banco"));
            }
            if (datosCuenta.get("numeroCuenta") != null) {
                cuenta.setNumeroCuenta((String) datosCuenta.get("numeroCuenta"));
            }
            if (datosCuenta.get("nombreCuenta") != null) {
                cuenta.setNombreCuenta((String) datosCuenta.get("nombreCuenta"));
            }

            // Validar la cuenta usando las anotaciones del modelo
            validarCuenta(cuenta);

            System.out.println("Cuenta creada en el servicio: " + cuenta);

            // Validar el número de cuenta según el banco
            if (!bancoService.validarNumeroCuenta(cuenta.getBanco(), cuenta.getNumeroCuenta())) {
                System.out.println("Número de cuenta inválido para el banco especificado: " + cuenta.getBanco() + ", " + cuenta.getNumeroCuenta());
                throw new RuntimeException("El número de cuenta no es válido para el banco especificado: " + cuenta.getBanco());
            }

            // Validar que el número de cuenta no esté asociado a otra cédula en el TXT
            validarNumeroCuentaEnTXT(numeroDocumento, cuenta.getNumeroCuenta());

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

            // Verificar si la cuenta ya existe para este usuario
            if (cuentaExiste(usuarioNode, cuenta.getNumeroCuenta())) {
                System.out.println("La cuenta ya existe para este usuario: " + cuenta.getNumeroCuenta());
                throw new RuntimeException("La cuenta ya está registrada para este usuario");
            }

            Element cuentaElement = doc.createElement("cuenta");
            cuentaElement.appendChild(crearElemento(doc, "banco", cuenta.getBanco()));
            cuentaElement.appendChild(crearElemento(doc, "numeroCuenta", cuenta.getNumeroCuenta()));
            cuentaElement.appendChild(crearElemento(doc, "nombreCuenta", cuenta.getNombreCuenta()));

            usuarioNode.appendChild(cuentaElement);
            guardarCambiosEnXML(doc);

            System.out.println("Cuenta guardada exitosamente");
            return true;

        } catch (RuntimeException e) {
            System.err.println("Error de validación al guardar cuenta: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error al guardar cuenta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error interno al guardar la cuenta: " + e.getMessage());
        }
    }

    // Validar que el número de cuenta no esté asociado a otra cédula en el TXT
    private void validarNumeroCuentaEnTXT(String numeroDocumento, String numeroCuenta) {
        try {
            File file = new File(TXT_FILE);
            if (!file.exists()) {
                System.out.println("Archivo TXT no encontrado, permitiendo creación: " + TXT_FILE);
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder bloque = new StringBuilder();
            String linea;
            String cedulaActual = null;
            String nCuentaActual = null;

            while ((linea = reader.readLine()) != null) {
                if (linea.equals("-*-*-*-*-*")) {
                    if (nCuentaActual != null && nCuentaActual.equals(numeroCuenta)) {
                        if (!cedulaActual.equals(numeroDocumento)) {
                            reader.close();
                            throw new RuntimeException("El número de cuenta " + numeroCuenta + " ya está registrado con otra cédula");
                        }
                    }
                    cedulaActual = null;
                    nCuentaActual = null;
                    bloque = new StringBuilder();
                } else {
                    bloque.append(linea).append("\n");
                    String[] partes = linea.split(":");
                    if (partes.length == 2) {
                        String clave = partes[0].trim();
                        String valor = partes[1].trim();
                        if (clave.equals("Cedula")) {
                            cedulaActual = valor;
                        } else if (clave.equals("NCuenta")) {
                            nCuentaActual = valor;
                        }
                    }
                }
            }

            // Procesar el último bloque
            if (nCuentaActual != null && nCuentaActual.equals(numeroCuenta)) {
                if (!cedulaActual.equals(numeroDocumento)) {
                    reader.close();
                    throw new RuntimeException("El número de cuenta " + numeroCuenta + " ya está registrado con otra cédula");
                }
            }

            reader.close();
        } catch (Exception e) {
            System.err.println("Error al validar número de cuenta en TXT: " + e.getMessage());
            throw new RuntimeException("Error al validar el número de cuenta: " + e.getMessage());
        }
    }

    // Obtener tarjetas por número de documento
    public List<CuentaModel> obtenerTarjetasPorNumeroDocumento(String numeroDocumento) {
        List<CuentaModel> cuentasRegistradas = obtenerCuentasPorNumeroDocumento(numeroDocumento);
        List<CuentaModel> tarjetas = new ArrayList<>();
        try {
            File file = new File(TXT_FILE);
            if (!file.exists()) {
                System.err.println("Archivo TXT no encontrado: " + TXT_FILE);
                return tarjetas;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder bloque = new StringBuilder();
            String linea;

            while ((linea = reader.readLine()) != null) {
                if (linea.equals("-*-*-*-*-*")) {
                    procesarBloqueTarjetas(bloque.toString(), cuentasRegistradas, tarjetas, numeroDocumento);
                    bloque = new StringBuilder();
                } else {
                    bloque.append(linea).append("\n");
                }
            }
            if (!bloque.toString().trim().isEmpty()) {
                procesarBloqueTarjetas(bloque.toString(), cuentasRegistradas, tarjetas, numeroDocumento);
            }
            reader.close();
            System.out.println("Tarjetas procesadas para documento " + numeroDocumento + ": " + tarjetas);
        } catch (Exception e) {
            System.err.println("Error al leer tarjetas desde TXT: " + e.getMessage());
            e.printStackTrace();
        }
        return tarjetas;
    }

    private void procesarBloqueTarjetas(String bloque, List<CuentaModel> cuentasRegistradas, List<CuentaModel> tarjetas, String numeroDocumento) {
        String[] lineas = bloque.trim().split("\n");
        CuentaModel tarjeta = new CuentaModel();
        String cedula = null;
        boolean cuentaValida = false;

        for (String linea : lineas) {
            String[] partes = linea.split(":");
            if (partes.length == 2) {
                String clave = partes[0].trim();
                String valor = partes[1].trim();
                switch (clave) {
                    case "Cedula":
                        cedula = valor;
                        break;
                    case "NCuenta":
                        tarjeta.setNumeroCuenta(valor);
                        break;
                    case "Banco":
                        tarjeta.setBanco(valor);
                        break;
                    case "TipoCuenta":
                        tarjeta.setNombreCuenta(valor); // Usamos nombreCuenta para TipoCuenta
                        break;
                    case "Saldo":
                        try {
                            String cleanedSaldoStr = valor.replace(".", "").replace(",", ".");
                            tarjeta.setSaldo(Double.parseDouble(cleanedSaldoStr));
                        } catch (NumberFormatException e) {
                            System.err.println("Error al parsear saldo: " + valor);
                        }
                        break;
                    case "NTarjeta":
                        tarjeta.setNumeroTarjeta(valor);
                        break;
                    case "ValidoHasta":
                        tarjeta.setValidoHasta(valor);
                        break;
                    case "NombreTarjeta":
                        tarjeta.setNombreTarjeta(valor);
                        break;
                }
            }
        }

        // Verificar si la cuenta está registrada para el usuario
        for (CuentaModel cuenta : cuentasRegistradas) {
            if (cuenta.getNumeroCuenta().equals(tarjeta.getNumeroCuenta())) {
                cuentaValida = true;
                break;
            }
        }

        // Solo agregar la tarjeta si la cuenta es válida y la cédula coincide con el numeroDocumento
        if (cuentaValida && cedula != null && cedula.equals(numeroDocumento)) {
            tarjetas.add(tarjeta);
        }
    }

    // Método para validar CuentaModel usando las anotaciones
    private void validarCuenta(CuentaModel cuenta) {
        Set<ConstraintViolation<CuentaModel>> violations = validator.validate(cuenta);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<CuentaModel> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new RuntimeException("Errores de validación: " + sb.toString());
        }
    }
    // Método para validar CuentaModel usando las anotaciones
    private void validarCuenta(CuentaModel cuenta) {
        Set<ConstraintViolation<CuentaModel>> violations = validator.validate(cuenta);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<CuentaModel> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new RuntimeException("Errores de validación: " + sb.toString());
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
            Map<String, Double> saldos = leerSaldosDesdeTXT();

            System.out.println("Saldos leídos desde TXT: " + saldos);

            if (usuarioNode != null) {
                NodeList listaCuentas = ((Element) usuarioNode).getElementsByTagName("cuenta");
                System.out.println("Cuentas encontradas en XML: " + listaCuentas.getLength());

                for (int i = 0; i < listaCuentas.getLength(); i++) {
                    Element cuentaElement = (Element) listaCuentas.item(i);
                    String banco = getElementTextContent(cuentaElement, "banco");
                    String numeroCuenta = getElementTextContent(cuentaElement, "numeroCuenta");
                    String nombreCuenta = getElementTextContent(cuentaElement, "nombreCuenta");
                    CuentaModel cuenta = new CuentaModel(banco, numeroCuenta, nombreCuenta);
                    Double saldo = saldos.get(numeroCuenta);
                    cuenta.setSaldo(saldo != null ? saldo : 0.0);
                    System.out.println("Cuenta procesada: " + numeroCuenta + ", Saldo: " + (saldo != null ? saldo : "No encontrado"));
                    cuentas.add(cuenta);
                }
            } else {
                System.out.println("Usuario no encontrado en XML para documento: " + numeroDocumento);
            }

        } catch (Exception e) {
            System.err.println("Error al obtener cuentas: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Cuentas devueltas: " + cuentas);
        return cuentas;
    }

    // Obtener todas las cuentas (GET)
    public List<CuentaModel> obtenerTodasLasCuentas() {
        List<CuentaModel> cuentas = new ArrayList<>();
        try {
            System.out.println("Obteniendo todas las cuentas");
            Document doc = obtenerDocumentoXML();
            NodeList usuarioNodes = doc.getElementsByTagName("usuario");
            Map<String, Double> saldos = leerSaldosDesdeTXT();

            System.out.println("Saldos leídos desde TXT: " + saldos);

            for (int i = 0; i < usuarioNodes.getLength(); i++) {
                Element usuarioElement = (Element) usuarioNodes.item(i);
                NodeList listaCuentas = usuarioElement.getElementsByTagName("cuenta");

                for (int j = 0; j < listaCuentas.getLength(); j++) {
                    Element cuentaElement = (Element) listaCuentas.item(j);
                    String banco = getElementTextContent(cuentaElement, "banco");
                    String numeroCuenta = getElementTextContent(cuentaElement, "numeroCuenta");
                    String nombreCuenta = getElementTextContent(cuentaElement, "nombreCuenta");
                    CuentaModel cuenta = new CuentaModel(banco, numeroCuenta, nombreCuenta);
                    Double saldo = saldos.get(numeroCuenta);
                    cuenta.setSaldo(saldo != null ? saldo : 0.0);
                    System.out.println("Cuenta procesada: " + numeroCuenta + ", Saldo: " + (saldo != null ? saldo : "No encontrado"));
                    cuentas.add(cuenta);
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

    // Modificar nombre de cuenta en XML (PUT)
    public boolean modificarCuentaEnXML(String numeroDocumento, String nombreCuenta, Map<String, Object> datosCuenta) {
        try {
            System.out.println("Modificando nombre de cuenta para documento: " + numeroDocumento + ", cuenta: " + nombreCuenta);
            System.out.println("Datos de cuenta recibidos: " + datosCuenta);

            // Crear y mapear la instancia de CuentaModel en el servicio
            CuentaModel nuevaCuenta = new CuentaModel();
            
            // Mapear los datos del Map al objeto CuentaModel
            if (datosCuenta.get("banco") != null) {
                nuevaCuenta.setBanco((String) datosCuenta.get("banco"));
            }
            if (datosCuenta.get("numeroCuenta") != null) {
                nuevaCuenta.setNumeroCuenta((String) datosCuenta.get("numeroCuenta"));
            }
            if (datosCuenta.get("nombreCuenta") != null) {
                nuevaCuenta.setNombreCuenta((String) datosCuenta.get("nombreCuenta"));
            }

            // Validar la cuenta usando las anotaciones del modelo
            validarCuenta(nuevaCuenta);

            System.out.println("Nuevo nombre de cuenta: " + nuevaCuenta.getNombreCuenta());

            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList cuentas = ((Element) usuarioNode).getElementsByTagName("cuenta");

                for (int i = 0; i < cuentas.getLength(); i++) {
                    Element cuentaElement = (Element) cuentas.item(i);
                    String nombreCuentaActual = getElementTextContent(cuentaElement, "nombreCuenta");

                    if (nombreCuentaActual.equals(nombreCuenta)) {
                        setElementTextContent(doc, cuentaElement, "nombreCuenta", nuevaCuenta.getNombreCuenta());
                        guardarCambiosEnXML(doc);
                        System.out.println("Nombre de cuenta modificado exitosamente.");
                        return true;
                    }
                }
                System.out.println("Cuenta no encontrada para modificar.");
            } else {
                System.out.println("Usuario no encontrado para modificar cuenta.");
            }
        } catch (RuntimeException e) {
            System.err.println("Error de validación al modificar cuenta: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error al modificar nombre de cuenta: " + e.getMessage());
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

    // Obtener saldo de una cuenta desde el archivo TXT
    public Double obtenerSaldoCuenta(String numeroCuenta) {
        Map<String, Double> saldos = leerSaldosDesdeTXT();
        Double saldo = saldos.get(numeroCuenta);
        System.out.println("Saldo para cuenta " + numeroCuenta + ": " + (saldo != null ? saldo : "No encontrado"));
        return saldo != null ? saldo : 0.0;
    }

    // Leer saldos desde el archivo TXT
    private Map<String, Double> leerSaldosDesdeTXT() {
        Map<String, Double> saldos = new HashMap<>();
        try {
            File file = new File(TXT_FILE);
            System.out.println("Leyendo archivo TXT: " + TXT_FILE);
            if (!file.exists()) {
                System.err.println("Archivo TXT no encontrado: " + TXT_FILE);
                return saldos;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder bloque = new StringBuilder();
            String linea;

            while ((linea = reader.readLine()) != null) {
                if (linea.equals("-*-*-*-*-*")) {
                    procesarBloqueTXT(bloque.toString(), saldos);
                    bloque = new StringBuilder();
                } else {
                    bloque.append(linea).append("\n");
                }
            }
            if (!bloque.toString().trim().isEmpty()) {
                procesarBloqueTXT(bloque.toString(), saldos);
            }
            reader.close();
            System.out.println("Saldos procesados: " + saldos);
        } catch (Exception e) {
            System.err.println("Error al leer archivo TXT: " + e.getMessage());
            e.printStackTrace();
        }
        return saldos;
    }

    private void procesarBloqueTXT(String bloque, Map<String, Double> saldos) {
        String[] lineas = bloque.trim().split("\n");
        String nCuenta = null;
        String saldoStr = null;

        System.out.println("Procesando bloque: \n" + bloque);

        for (String linea : lineas) {
            String[] partes = linea.split(":");
            if (partes.length == 2) {
                String clave = partes[0].trim();
                String valor = partes[1].trim();
                if (clave.equals("NCuenta")) {
                    nCuenta = valor;
                } else if (clave.equals("Saldo")) {
                    saldoStr = valor;
                }
            }
        }

        if (nCuenta != null && saldoStr != null) {
            try {
                String cleanedSaldoStr = saldoStr.replace(".", "").replace(",", ".");
                double saldo = Double.parseDouble(cleanedSaldoStr);
                saldos.put(nCuenta, saldo);
                System.out.println("Cuenta: " + nCuenta + ", Saldo: " + saldo);
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear saldo para cuenta " + nCuenta + ": " + saldoStr + ", Error: " + e.getMessage());
            }
        } else {
            System.err.println("Bloque incompleto, NCuenta: " + nCuenta + ", Saldo: " + saldoStr);
        }
    }

    // Métodos auxiliares
    private Document obtenerDocumentoXML() throws Exception {
        File file = new File(XML_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        System.out.println("Intentando cargar archivo XML: " + XML_FILE);
        if (file.exists() && file.length() > 0) {
            System.out.println("Archivo XML encontrado, tamaño: " + file.length() + " bytes");
            return dBuilder.parse(file);
        } else {
            System.out.println("Archivo XML no existe o está vacío, creando nuevo documento");
            return crearDocumentoVacio(dBuilder, file);
        }
    }

    private Document crearDocumentoVacio(DocumentBuilder dBuilder, File file) {
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                System.out.println("Directorio creado: " + dirCreated);
            }

            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("usuarios");
            doc.appendChild(rootElement);
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
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                System.out.println("Directorio creado: " + dirCreated);
            }

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
        System.out.println("Nodo usuario no encontrado para documento: " + numeroDocumento);
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

    public boolean modificarNombreCuenta(String documento, String oldNombre, String nuevoNombre) {
        try {
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, documento);

            if (usuarioNode != null) {
                NodeList cuentas = ((Element) usuarioNode).getElementsByTagName("cuenta");
                for (int i = 0; i < cuentas.getLength(); i++) {
                    Element cuentaElement = (Element) cuentas.item(i);
                    String nombreActual = getElementTextContent(cuentaElement, "nombreCuenta");

                    if (nombreActual.equals(oldNombre)) {
                        setElementTextContent(doc, cuentaElement, "nombreCuenta", nuevoNombre);
                        guardarCambiosEnXML(doc);
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}