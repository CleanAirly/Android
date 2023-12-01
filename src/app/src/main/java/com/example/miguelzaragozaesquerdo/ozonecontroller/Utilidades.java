package com.example.miguelzaragozaesquerdo.ozonecontroller;


import android.util.Log;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// -----------------------------------------------------------------------------------
// @author: Jordi Bataller i Mascarell
// -----------------------------------------------------------------------------------
public class Utilidades {

    /**
     * Envía un correo electrónico utilizando el servicio SMTP de Gmail.
     * Esta función configura y envía un correo electrónico a un destinatario específico con un asunto y cuerpo definidos.
     * Se ejecuta en un nuevo hilo para no bloquear el hilo principal de la aplicación.
     * Utiliza las credenciales del remitente especificadas para autenticarse con el servidor SMTP de Gmail
     * y envía el mensaje. Registra un mensaje en el log para confirmar el envío exitoso o capturar errores.
     *
     * @param destinatario El correo electrónico del destinatario al que se enviará el mensaje.
     * @param asunto       El asunto del correo electrónico.
     * @param cuerpo       El cuerpo del mensaje del correo electrónico.
     */
    public static void enviarConGMail(String destinatario, String asunto, String cuerpo) {
        String remitente = "contact.cleanairly@gmail.com";
        String claveemail = "zxmz bnux jraj rtwi";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Properties props = System.getProperties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.user", remitente);
                props.put("mail.smtp.clave", claveemail);
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.port", "587");

                Session session = Session.getDefaultInstance(props);
                MimeMessage message = new MimeMessage(session);

                try {
                    message.setFrom(new InternetAddress(remitente));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
                    message.setSubject(asunto);
                    message.setText(cuerpo);

                    Transport transport = session.getTransport("smtp");
                    transport.connect("smtp.gmail.com", remitente, claveemail);
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();
                    Log.d("TEST - CORREO", "ENVIADO");
                } catch (MessagingException me) {
                    Log.d("TEST - CORREO", "ERROR");
                    me.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Genera un código aleatorio de 6 caracteres.
     * Esta función crea un código aleatorio utilizando una combinación de letras mayúsculas y números.
     * Se seleccionan caracteres al azar de una cadena predefinida ('caracteres') para formar el código.
     * Este código se compone de 6 caracteres, y cada carácter se elige de forma aleatoria.
     * Este tipo de código se puede utilizar para funciones como códigos de verificación, identificadores únicos, etc.
     *
     * @return Un string que representa el código aleatorio generado.
     */
    public static String codigoAleatorio(){
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        Random rnd = new Random();
        StringBuilder codigo = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            codigo.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }

        return codigo.toString();
    }

    /**
     * Genera un hash de la contraseña utilizando el algoritmo SHA-256.
     * @param password La contraseña a ser hasheada.
     * @return El hash de la contraseña.
     */
    public static String hashPassword(String password){
        try{
            // Crea una instancia de MessageDigest con el algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convierte la contraseña en un arreglo de bytes
            byte[] passwordBytes = password.getBytes();

            // Calcula el hash
            byte[] hashBytes = digest.digest(passwordBytes);

            // Convierte el hash en una representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convierte una cadena de texto en un arreglo de bytes.
     * Esta función toma una cadena de texto (String) y utiliza el método getBytes()
     * para convertirla en un arreglo de bytes. Esto puede ser útil para operaciones que requieren
     * una representación de texto en formato de bytes, como la manipulación de datos a nivel de bytes,
     * almacenamiento en formatos binarios, o transmisión a través de protocolos que requieren datos en bytes.
     *
     * @param texto La cadena de texto que se convertirá en un arreglo de bytes.
     * @return Un arreglo de bytes que representa la cadena de texto dada.
     */
    public static byte[] stringToBytes ( String texto ) {
        return texto.getBytes();
    }

    /**
     * Convierte una cadena de texto en un UUID (Identificador Único Universal).
     * Esta función toma una cadena de texto que representa un UUID y la convierte en un objeto UUID.
     * La cadena de texto debe tener exactamente 16 caracteres para ser válida. Divide la cadena en dos partes:
     * la más significativa (primeros 8 caracteres) y la menos significativa (últimos 8 caracteres),
     * y utiliza estos valores para crear el UUID. Si la cadena no tiene 16 caracteres, se lanza un error.
     *
     * @param uuid La cadena de texto que representa un UUID.
     * @return El objeto UUID generado a partir de la cadena de texto.
     * @throws Error Si la cadena no tiene 16 caracteres.
     */
    public static UUID stringToUUID( String uuid ) {
        if ( uuid.length() != 16 ) {
            throw new Error( "stringUUID: string no tiene 16 caracteres ");
        }
        byte[] comoBytes = uuid.getBytes();

        String masSignificativo = uuid.substring(0, 8);
        String menosSignificativo = uuid.substring(8, 16);
        UUID res = new UUID( Utilidades.bytesToLong( masSignificativo.getBytes() ), Utilidades.bytesToLong( menosSignificativo.getBytes() ) );

        return res;
    }

    /**
     * Convierte un UUID (Identificador Único Universal) en una cadena de texto.
     * Esta función toma un objeto UUID y lo convierte en una representación de texto.
     * Extrae los bits más y menos significativos del UUID, los convierte en un arreglo de bytes
     * utilizando 'dosLongToBytes', y luego convierte este arreglo de bytes en una cadena de texto
     * con la función 'bytesToString'. Este proceso permite obtener la representación textual del UUID,
     * que puede ser útil para almacenamiento, visualización o transmisión de datos.
     *
     * @param uuid El objeto UUID que será convertido en una cadena de texto.
     * @return La representación en texto del UUID dado.
     */
    public static String uuidToString ( UUID uuid ) {
        return bytesToString( dosLongToBytes( uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() ) );
    }

    /**
     * Convierte un UUID en una representación hexadecimal en forma de cadena de texto.
     * Esta función transforma un UUID en un arreglo de bytes y luego convierte esos bytes en una representación hexadecimal.
     *
     * @param uuid El UUID a convertir.
     * @return La representación hexadecimal en forma de cadena de texto del UUID.
     */
    public static String uuidToHexString ( UUID uuid ) {
        return bytesToHexString( dosLongToBytes( uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() ) );
    }

    /**
     * Convierte un arreglo de bytes en una cadena de texto.
     * Si el arreglo de bytes es nulo, devuelve una cadena vacía.
     *
     * @param bytes El arreglo de bytes a convertir.
     * @return La cadena de texto resultante.
     */
    public static String bytesToString( byte[] bytes ) {
        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append( (char) b );
        }
        return sb.toString();
    }

    /**
     * Convierte dos valores long en un arreglo de bytes.
     *
     * @param masSignificativos    El valor más significativo.
     * @param menosSignificativos  El valor menos significativo.
     * @return El arreglo de bytes resultante.
     */
    public static byte[] dosLongToBytes( long masSignificativos, long menosSignificativos ) {
        ByteBuffer buffer = ByteBuffer.allocate( 2 * Long.BYTES );
        buffer.putLong( masSignificativos );
        buffer.putLong( menosSignificativos );
        return buffer.array();
    }

    /**
     * Convierte un arreglo de bytes en un valor entero.
     *
     * @param bytes El arreglo de bytes a convertir.
     * @return El valor entero resultante.
     */
    public static int bytesToInt( byte[] bytes ) {
        return new BigInteger(bytes).intValue();
    }

    /**
     * Convierte un arreglo de bytes en un valor long.
     *
     * @param bytes El arreglo de bytes a convertir.
     * @return El valor long resultante.
     */
    public static long bytesToLong( byte[] bytes ) {
        return new BigInteger(bytes).longValue();
    }

    /**
     * Convierte un arreglo de bytes en un valor entero, manejando posibles errores.
     * Si el arreglo de bytes es nulo, devuelve cero. Si el arreglo es demasiado grande, lanza un error.
     *
     * @param bytes El arreglo de bytes a convertir.
     * @return El valor entero resultante.
     * @throws Error Si el arreglo de bytes es demasiado grande.
     */
    public static int bytesToIntOK( byte[] bytes ) {
        if (bytes == null ) {
            return 0;
        }

        if ( bytes.length > 4 ) {
            throw new Error( "demasiados bytes para pasar a int ");
        }
        int res = 0;

        for( byte b : bytes ) {
            res =  (res << 8)
                    + (b & 0xFF);
        }

        if ( (bytes[ 0 ] & 0x8) != 0 ) {
            res = -(~(byte)res)-1;
        }

        return res;
    }

    /**
     * Convierte un arreglo de bytes en una cadena de texto hexadecimal.
     * Si el arreglo de bytes es nulo, devuelve una cadena vacía.
     *
     * @param bytes El arreglo de bytes a convertir.
     * @return La representación hexadecimal en forma de cadena de texto de los bytes.
     */
    public static String bytesToHexString( byte[] bytes ) {

        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
            sb.append(':');
        }
        return sb.toString();
    }
}