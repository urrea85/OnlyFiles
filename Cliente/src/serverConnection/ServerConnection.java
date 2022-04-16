package serverConnection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Scanner;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import encrypt.AES;
import main.Main;

import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.*;

public class ServerConnection {
	private static SSLSocket skServidor;
	private static String host = "127.0.0.1";
	private static int puerto = 9999;
	private static SSLContext sc;
	private static TrustManager[] trustManagers;
	private static KeyManager[] keyManagers;
	
	private static String keyPath = "certs\\clientKey.jks", 
			   			  trustPath = "certs\\clientTrustedNewCerts.jks",
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
	
	public static boolean login(String user, String password) throws Exception {
		
		boolean result = false;
		 	Main.generateSalt();

		boolean connected = establishConnection();			
		if(connected) {
			try {
				writeSocket(skServidor,"login " + user);
				//exporting salt and iv to calculate hash
				readFileSocket(skServidor,"salt");
				byte[] salt = Main.readByte(new File("salt"));
				//System.out.println(sal);
				//System.out.println(salt);
				readFileSocket(skServidor,"Kdata.iv");
				IvParameterSpec iv = AES.readIV("password.iv");
				String KloginEncrypt = Main.login(user, password, salt, iv);
				writeSocket(skServidor,user+" "+KloginEncrypt);
				String valido = readSocket(skServidor, "");
				System.out.println(valido);
				if(valido.equals("Registered")) { //salt es correcto, falta iv
					result = true;
				}else {
					result = false;
				}
				closeConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static boolean register(String user, String password) throws Exception {
		
		boolean result = false;
		byte[] salt = 	Main.generateSalt();
		String string = new String(salt);
		IvParameterSpec iv = AES.generateIv("password.iv");
		String KloginEncrypt = Main.register(user, password, salt, iv);

		boolean connected = establishConnection();			
		if(connected) {
		
			try {
				writeSocket(skServidor,"register");
				writeSocket(skServidor,user+" "+KloginEncrypt);
				writeFileSocket(skServidor,"salt");
				writeFileSocket(skServidor,"password.iv");
				String valido = readSocket(skServidor, "");
				System.out.println(valido);
				if(valido.equals("Registered")) {
					result = true;
				}else {
					result = false;
				}
				closeConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static SSLSocket getSkServidor() {
		return skServidor;
	}

	public static void setSkServidor(SSLSocket skServidor) {
		ServerConnection.skServidor = skServidor;
	}

	//Función a llamar desde la interfaz para crear la conexión
	public ServerConnection() {
        establishConnection();
	}
	
	//Extrae los certificados de los almacenes
	private static void getCerts(String keyPath, String trustPath, String pass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, KeyManagementException {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(keyPath),pass.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, pass.toCharArray());

			
			KeyStore trustedStore = KeyStore.getInstance("JKS");
			trustedStore.load(new FileInputStream(trustPath), pass.toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustedStore);
			
			sc = SSLContext.getInstance("TLS");
						
			keyManagers = kmf.getKeyManagers();
			trustManagers = tmf.getTrustManagers();
			
			sc.init(keyManagers, trustManagers, null);
		}
	
	public static boolean establishConnection() {
		try {
			getCerts(keyPath, trustPath, pass);
			
			
			System.out.println("Estableciendo conexión...");
			SSLSocketFactory clientFactory = sc.getSocketFactory();
            skServidor = (SSLSocket) clientFactory.createSocket(host, puerto);
			skServidor.startHandshake();
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
	
	public static void writeSocket(Socket p_sk, String p_Datos)
    {
        try
        {
                OutputStream aux = p_sk.getOutputStream();
                DataOutputStream flujo= new DataOutputStream( aux );
                flujo.writeUTF(p_Datos);      
        }
        catch (IOException e)
        {
                System.out.println("Error en writeSocket");
                System.out.println("Error: " + e.toString());
        }      		
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
	
	public static void writeFileSocket(Socket sock, String filename) throws Exception {
		 	File MyFile = new File(filename);
	        int FileSize = (int) MyFile.length();
	        OutputStream os =sock.getOutputStream();
	        PrintWriter pr = new PrintWriter(sock.getOutputStream(), true);
	        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(MyFile));
	        Scanner in = new Scanner(sock.getInputStream());
	         
	        pr.println(filename);
	        pr.println(FileSize);
	        byte[] filebyte = new byte[FileSize];
	        bis.read(filebyte, 0, filebyte.length);
	        os.write(filebyte, 0, filebyte.length);
	        System.out.println(in.nextLine());
	        os.flush();
	}
	
	public static void readFileSocket(Socket sock, String filename) throws Exception {
		Scanner in = new Scanner(sock.getInputStream());
    	InputStream is = sock.getInputStream();
        PrintWriter pr = new PrintWriter(sock.getOutputStream(), true);
        String FileName = in.nextLine();
        int FileSize = in.nextInt();
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        byte[] filebyte = new byte[FileSize];
         
        int file = is.read(filebyte, 0, filebyte.length);
        bos.write(filebyte, 0, file);
         
        System.out.println("Incoming File: " + FileName);
        System.out.println("Size: " + FileSize + "Byte");
        if(FileSize == file)System.out.println("File is verified");
        else System.out.println("File is corrupted. File Recieved " + file + " Byte");
        pr.println("File Recieved SUccessfully.");
        bos.close();
	}
}
