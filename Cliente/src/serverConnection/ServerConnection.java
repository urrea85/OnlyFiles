package serverConnection;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class ServerConnection {
	private static Socket skServidor;
	private static String host = "127.0.0.1";
	private static int puerto = 9999;
	
	private static TrustManager[] trustManagers;
	private static KeyManager[] keyManagers;
	
	private static String keyPath = "certs\\clientStore.jks", 
			   			  trustPath = "certs\\clientTrustedCerts.jks",
			              pass = "clientpass"; 
	
	public static void main(String[] args) {	  
		boolean connected = establishConnection();			
		if(connected) {
			try {
				for(;;) {
				System.out.println(trustManagers);
				System.out.println(keyManagers);
				String patata = readSocket(skServidor,"");
				System.out.println(patata);
				if(patata.equals("-1;"))
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(closeConnection());
		}	
	}
	
	//Función a llamar desde la interfaz para crear la conexión
	public ServerConnection() {
        establishConnection();
	}
	
	//Extrae los certificados de los almacenes
	private static void getCerts(String keyPath, String trustPath, String pass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(keyPath),pass.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, pass.toCharArray());
			keyManagers = kmf.getKeyManagers();
			
			KeyStore trustedStore = KeyStore.getInstance("JKS");
			trustedStore.load(new FileInputStream(trustPath), pass.toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustedStore);
			trustManagers = tmf.getTrustManagers();
		}
	
	public static boolean establishConnection() {
		try {
			getCerts(keyPath, trustPath, pass);
			
			
			System.out.println("Estableciendo conexión...");
			SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            skServidor = clientFactory.createSocket(host, puerto);
			return true;
		}catch (FileNotFoundException e){
        	System.out.println("No se encuentran los almacenes de certificados");
        	System.out.println("Error: " + e.toString());
        	return false;
		} catch (GeneralSecurityException e) {
        	System.out.println("No se puede acceder a los certificados");	
        	System.out.println("Error: " + e.toString());
        	return false;
		} catch (ConnectException e) {
			System.out.println("El servidor no está disponible");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean closeConnection() {
		try {
			System.out.println("Cerrando conexión...");
			skServidor.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void writeSocket() {
		
	}
	
	public static String readSocket (Socket p_sk, String p_Datos)
    {
        try
        {
                InputStream aux = p_sk.getInputStream();
                DataInputStream flujo = new DataInputStream( aux );
                p_Datos = flujo.readUTF();
                return p_Datos;
        }
        catch (IOException e)
        {
                System.out.println("Comunicación perdida, cerrando conexión");
                //System.out.println("Error: " + e.toString());
                return "-1;";
        }       
    }
}
