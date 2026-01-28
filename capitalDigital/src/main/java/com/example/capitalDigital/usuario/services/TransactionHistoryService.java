package com.example.capitalDigital.usuario.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import com.example.capitalDigital.usuario.models.CuentaModel;
import com.example.capitalDigital.usuario.models.TransactionHistoryModel;

@Service
public class TransactionHistoryService {

    @Autowired
    private CuentaService cuentaService;

    private static final String XML_FILE = "C:\\Users\\simon\\OneDrive\\Escritorio\\CapitalDigitalISProyect\\Proyecto-capitaldigital\\capitalDigital\\src\\main\\java\\com\\example\\capitalDigital\\Info_bank\\TransactionHistory.xml";
    private static final String TXT_FILE = "C:\\Users\\simon\\OneDrive\\Escritorio\\CapitalDigitalISProyect\\Proyecto-capitaldigital\\proyecto-bank\\public\\data\\ListaCuentasDetalladasBancos.txt";

    // Guardar transacción en XML y actualizar saldos en TXT
    public boolean guardarTransactionEnXML(String numeroDocumento, Map<String, Object> historyData) {
        try {
            System.out.println("Guardando transacción en XML para documento: " + numeroDocumento);
            System.out.println("Datos recibidos: " + historyData);

            // Crear instancia de TransactionHistoryModel
            TransactionHistoryModel transaccion = new TransactionHistoryModel();
            transaccion.setBeneficiario((String) historyData.get("beneficiario"));
            transaccion.setMonto((String) historyData.get("monto"));
            transaccion.setBancoOrigen((String) historyData.get("bancoOrigen"));
            transaccion.setNumCuentaOrigen((String) historyData.get("numCuentaOrigen"));
            transaccion.setBancoDestino((String) historyData.get("bancoDestino"));
            transaccion.setNumCuentaDestino((String) historyData.get("numCuentaDestino"));
            transaccion.setConcepto((String) historyData.get("concepto"));
            transaccion.setFechaHora(LocalDateTime.now());

            // Validar que la cuenta de origen existe
            List<CuentaModel> cuentas = cuentaService.obtenerCuentasPorNumeroDocumento(numeroDocumento);
            boolean cuentaOrigenExiste = cuentas.stream()
                    .anyMatch(c -> c.getNumeroCuenta().equals(transaccion.getNumCuentaOrigen())
                            && c.getBanco().equals(transaccion.getBancoOrigen()));
            if (!cuentaOrigenExiste) {
                System.out.println("Cuenta de origen no encontrada: " + transaccion.getNumCuentaOrigen());
                return false;
            }

            // Validar saldo suficiente
            Double monto = Double.parseDouble(transaccion.getMonto());
            Double saldoOrigen = cuentaService.obtenerSaldoCuenta(transaccion.getNumCuentaOrigen());
            if (saldoOrigen == null || monto > saldoOrigen) {
                System.err.println("Saldo insuficiente para la cuenta de origen: " + transaccion.getNumCuentaOrigen() + ", Saldo: " + saldoOrigen + ", Monto: " + monto);
                return false;
            }

            // Actualizar saldos en el archivo TXT
            boolean saldosActualizados = actualizarSaldosEnTXT(transaccion.getNumCuentaOrigen(), transaccion.getNumCuentaDestino(), monto);
            if (!saldosActualizados) {
                System.err.println("Error al actualizar saldos en el archivo TXT");
                return false;
            }

            // Obtener o crear el documento XML
            Document doc = obtenerDocumentoXML();
            Element root = doc.getDocumentElement();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode == null) {
                System.out.println("Usuario no encontrado, creando nuevo usuario");
                Element usuarioElement = doc.createElement("usuario");
                usuarioElement.setAttribute("numeroDocumento", numeroDocumento);
                root.appendChild(usuarioElement);
                usuarioNode = usuarioElement;
            }

            // Crear elemento de transacción
            Element transaccionElement = doc.createElement("transaccion");
            transaccionElement.appendChild(crearElemento(doc, "beneficiario", transaccion.getBeneficiario()));
            transaccionElement.appendChild(crearElemento(doc, "monto", transaccion.getMonto()));
            transaccionElement.appendChild(crearElemento(doc, "bancoOrigen", transaccion.getBancoOrigen()));
            transaccionElement.appendChild(crearElemento(doc, "numCuentaOrigen", transaccion.getNumCuentaOrigen()));
            transaccionElement.appendChild(crearElemento(doc, "bancoDestino", transaccion.getBancoDestino()));
            transaccionElement.appendChild(crearElemento(doc, "numCuentaDestino", transaccion.getNumCuentaDestino()));
            transaccionElement.appendChild(crearElemento(doc, "concepto", transaccion.getConcepto()));
            transaccionElement.appendChild(crearElemento(doc, "fecha", transaccion.getFecha()));

            usuarioNode.appendChild(transaccionElement);
            guardarCambiosEnXML(doc);

            System.out.println("Transacción guardada exitosamente");
            return true;

        } catch (NumberFormatException e) {
            System.err.println("Error al parsear monto: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error al guardar transacción: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar saldos en el archivo TXT
    private boolean actualizarSaldosEnTXT(String numCuentaOrigen, String numCuentaDestino, double monto) {
        try {
            File file = new File(TXT_FILE);
            System.out.println("Actualizando saldos en archivo TXT: " + TXT_FILE);
            if (!file.exists()) {
                System.err.println("Archivo TXT no encontrado: " + TXT_FILE);
                return false;
            }

            // Leer todo el contenido del archivo
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> lineas = new ArrayList<>();
            String linea;
            while ((linea = reader.readLine()) != null) {
                lineas.add(linea);
            }
            reader.close();

            // Procesar las líneas para actualizar los saldos
            boolean origenActualizado = false;
            boolean destinoActualizado = false;
            for (int i = 0; i < lineas.size(); i++) {
                if (lineas.get(i).startsWith("NCuenta:" + numCuentaOrigen)) {
                    // Encontrar el saldo en las líneas siguientes
                    for (int j = i; j < lineas.size(); j++) {
                        if (lineas.get(j).startsWith("Saldo:")) {
                            String saldoStr = lineas.get(j).split(":")[1].trim();
                            String cleanedSaldoStr = saldoStr.replace(".", "").replace(",", ".");
                            double saldoActual = Double.parseDouble(cleanedSaldoStr);
                            double nuevoSaldo = saldoActual - monto;
                            // Usar Locale para formatear el saldo al formato venezolano (99.999,99)
                            lineas.set(j, "Saldo:" + String.format(Locale.forLanguageTag("es-VE"), "%,.2f", nuevoSaldo));
                            origenActualizado = true;
                            break;
                        }
                    }
                }
                if (lineas.get(i).startsWith("NCuenta:" + numCuentaDestino)) {
                    // Actualizar saldo de destino (si existe)
                    for (int j = i; j < lineas.size(); j++) {
                        if (lineas.get(j).startsWith("Saldo:")) {
                            String saldoStr = lineas.get(j).split(":")[1].trim();
                            String cleanedSaldoStr = saldoStr.replace(".", "").replace(",", ".");
                            double saldoActual = Double.parseDouble(cleanedSaldoStr);
                            double nuevoSaldo = saldoActual + monto;
                            // Usar Locale para formatear el saldo al formato venezolano (99.999,99)
                            lineas.set(j, "Saldo:" + String.format(Locale.forLanguageTag("es-VE"), "%,.2f", nuevoSaldo));
                            destinoActualizado = true;
                            break;
                        }
                    }
                }
            }

            if (!origenActualizado) {
                System.err.println("Cuenta de origen no encontrada en TXT: " + numCuentaOrigen);
                return false;
            }

            // Escribir el archivo actualizado
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String l : lineas) {
                writer.write(l);
                writer.newLine();
            }
            writer.close();
            System.out.println("Saldos actualizados en TXT: Origen=" + numCuentaOrigen + ", Destino=" + numCuentaDestino);
            return true;

        } catch (Exception e) {
            System.err.println("Error al actualizar saldos en TXT: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Consultar transacciones por número de documento
    public List<TransactionHistoryModel> consultarTransacciones(String numeroDocumento) {
        List<TransactionHistoryModel> transacciones = new ArrayList<>();
        try {
            System.out.println("Consultando transacciones para documento: " + numeroDocumento);
            Document doc = obtenerDocumentoXML();
            Node usuarioNode = encontrarNodoUsuario(doc, numeroDocumento);

            if (usuarioNode != null) {
                NodeList listaTransacciones = ((Element) usuarioNode).getElementsByTagName("transaccion");
                System.out.println("Transacciones encontradas: " + listaTransacciones.getLength());

                for (int i = 0; i < listaTransacciones.getLength(); i++) {
                    Element transaccionElement = (Element) listaTransacciones.item(i);
                    TransactionHistoryModel transaccion = new TransactionHistoryModel();
                    transaccion.setBeneficiario(getElementTextContent(transaccionElement, "beneficiario"));
                    transaccion.setMonto(getElementTextContent(transaccionElement, "monto"));
                    transaccion.setBancoOrigen(getElementTextContent(transaccionElement, "bancoOrigen"));
                    transaccion.setNumCuentaOrigen(getElementTextContent(transaccionElement, "numCuentaOrigen"));
                    transaccion.setBancoDestino(getElementTextContent(transaccionElement, "bancoDestino"));
                    transaccion.setNumCuentaDestino(getElementTextContent(transaccionElement, "numCuentaDestino"));
                    transaccion.setConcepto(getElementTextContent(transaccionElement, "concepto"));
                    // Parsear la fecha del XML y asignarla a fechaHora
                    String fechaStr = getElementTextContent(transaccionElement, "fecha");
                    if (!fechaStr.isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        transaccion.setFechaHora(LocalDateTime.parse(fechaStr, formatter));
                    }
                    transacciones.add(transaccion);
                }
            } else {
                System.out.println("Usuario no encontrado en XML para documento: " + numeroDocumento);
            }

        } catch (Exception e) {
            System.err.println("Error al consultar transacciones: " + e.getMessage());
            e.printStackTrace();
        }
        return transacciones;
    }

    // Métodos auxiliares para XML
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
            Element rootElement = doc.createElement("transacciones");
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

    private Element crearElemento(Document doc, String nombre, String valor) {
        Element element = doc.createElement(nombre);
        element.appendChild(doc.createTextNode(valor != null ? valor : ""));
        return element;
    }
}