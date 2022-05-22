package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.lang.System.Logger;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.*;

import javax.net.ssl.*;

public class Server {
	private static int port = 9999;
	private static String path;
	
	private static TrustManager[] trustManagers;
	private static KeyManager[] keyManagers;
	
	public static final Logger LOGGER = Logger.getLogger("Server");
	
	private static String keyPath = "src\\certs\\serverKey.jks", 
			   trustPath = "src\\certs\\serverTrustedNewCerts.jks",
			   pass = "servpass";
	
	
	private static void setSetKeyPath() {
		System.out.println("Introduzca la ruta:");
		InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader (isr);
        String ruta = "";
        try {
			ruta = br.readLine();
			keyPath = ruta;
		} catch (IOException e) {
			System.out.println("Ha ocurrido un error, intentelo de nuevo");
		}
	};
	
	private static void setSetTrustPath() {
		System.out.println("Introduzca la ruta:");
		InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader (isr);
        String ruta = "";
        try {
			ruta = br.readLine();
			trustPath = ruta;
		} catch (IOException e) {
			System.out.println("Ha ocurrido un error, intentelo de nuevo");
		}
	};
	
	private static void setSetCertPass() {
		System.out.println("Introduzca la ruta:");
		InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader (isr);
        String ruta = "";
        try {
			ruta = br.readLine();
			pass = ruta;
		} catch (IOException e) {
			System.out.println("Ha ocurrido un error, intentelo de nuevo");
		}
	};
	
	
	
	public static void main(String[] args) {
		File fichero = new File ("/src/fichero.txt");
        
        boolean exit = false;
        int opc = 0;
        
        Handler fileHandler = null;
        Formatter simpleFormatter = null;
       
		try {
			fileHandler = new FileHandler("server.log",true);
		} catch (SecurityException | IOException e1) {
			/*try {
				fileHandler = new FileHandler("./server.log");
			} catch (SecurityException | IOException e) {
				e.printStackTrace();
			}*/
			e1.printStackTrace();
		}
        
    	try {
			LOGGER.addHandler(fileHandler);
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);
			simpleFormatter = new SimpleFormatter();
			fileHandler.setFormatter(simpleFormatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
        
        while(exit == false){
        	System.out.println(keyPath);
        	System.out.println(trustPath);
        	System.out.println(pass);
            try {
                System.out.println(
                      "[1] Iniciar Servidor\n"
                    + "[2] Establecer ruta del almacen de certificados (keypath)\n"
                    + "[3] Establecer ruta del almacen de certificados de confianza (trustpath)\n"
                    + "[4] Establecer contraseña del almacen de certificados \n"
                    + "[5] Cerrar");
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader (isr);
                opc = Integer.parseInt(br.readLine());
                
                switch (opc) {
                    case 1:
                        initServer();
                        break;
                    case 2:
                    	setSetKeyPath();
                        break;
                    case 3:
                    	setSetTrustPath();
                    	break;
                    case 4:
                    	setSetCertPass();
                        break;
                    case 5: 
                        exit = true;
                        System.out.print("Cerrando servidor \n");
                        break;
                    default:
                        System.out.print("Opción incorrecta o no soportada");
                }
                
            } catch (IOException ex) {
                System.out.println("Error: " + ex.toString());
            }             
        }
    }
	
	//Extrae los certificados de los almacenes
	private static void getCerts(String keyPath, String trustPath, String pass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(keyPath),pass.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, "servpass".toCharArray());
		keyManagers = kmf.getKeyManagers();
		
		KeyStore trustedStore = KeyStore.getInstance("JKS");
		trustedStore.load(new FileInputStream(trustPath), pass.toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustedStore);
		trustManagers = tmf.getTrustManagers();
	}
	
	
	
	
	private static void initServer() {
		try {

			/*System.setProperty("javax.net.ssl.keyStore", "certs/serverkey.jks");
	        System.setProperty("javax.net.ssl.keyStorePassword","servpass");
	        System.setProperty("javax.net.ssl.trustStore", "certs/serverTrustedCerts.jks");
	        System.setProperty("javax.net.ssl.trustStorePassword", "servpass");*/
			//C:\Users\alexp\OneDrive\Escritorio\Socket\Server\src\certs
			
			getCerts(keyPath, trustPath, pass);
			
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(keyManagers, trustManagers, null);

			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			ServerSocket serverSocket = ssf.createServerSocket(port);
			//System.out.println("Socket iniciado, escuchando puerto: " + port);	
			LOGGER.log(Level.INFO,"Socket iniciado, escuchando puerto: " + port);
			
            for(;;){
                Socket skServer = serverSocket.accept();
                ServerThread t = new ServerThread(skServer);
                t.start();
            }            
        } catch (FileNotFoundException e){
        	//System.out.println("No se encuentran los almacenes de certificados");
        	System.out.println("Error: " + e.toString());
        	LOGGER.log(Level.SEVERE, "No se encuentran los almacenes de certificados ("+ e.toString() +")");
		} catch(BindException e) {
			int oport = port;
			port = port + 1;
			//System.out.println("El puerto " + oport + " está en uso, en el próximo intento se usará el puerto: " + port);
			LOGGER.log(Level.WARNING,"El puerto " + oport + " está en uso, en el próximo intento se usará el puerto: " + port);
		} catch (GeneralSecurityException e) {
        	//System.out.println("No se puede acceder a los certificados");	
        	//System.out.println("Error: " + e.toString());
        	LOGGER.log(Level.SEVERE, "No se puede acceder a los certificados (" + e.toString() + ")");
		} catch (IOException e) {
            //System.out.println("Error: " + e.toString());
			LOGGER.log(Level.SEVERE, "Error: " + e.toString());
        }
	}
}
