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
import java.security.PublicKey;
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
import encrypt.PubPrivKey;
import main.Main;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.*;

//uJ10>=ocd3sG&z?oJMCFiJr+u_-_s3pB+Fw[o=ffjG3+skG!sQ44cJ*dXZ{Rqm8r
public class ServerConnection {
	private static SSLSocket skServidor;
	private static String host = "127.0.0.1";
	private static int puerto = 9999;
	private static SSLContext sc;
	private static TrustManager[] trustManagers;
	private static KeyManager[] keyManagers;
	private static SecretKey kdata;
	private static IvParameterSpec iv;
	
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
				//System.out.println(sal);
				//System.out.println(salt);
				readFileSocket(skServidor,"Kdata.iv");
				//AES.decryptFile("Kdata.iv.enc", kdata, iv);
				iv = AES.readIV("Kdata.iv");
				//exporting salt and iv to calculate hash
				readFileSocket(skServidor,"salt");
				byte[] salt = Main.readByte(new File("salt"));
				readFileSocket(skServidor,"public.key");
				readFileSocket(skServidor,"private.key.enc");
				Main main = new Main();
				String KloginEncrypt = main.login(user, password, salt, iv);
				kdata = main.getKdatosHashed();
				AES.decryptFile("private.key.enc", kdata, iv);
				writeSocket(skServidor,user+" "+KloginEncrypt);
				String valido = readSocket(skServidor, "");
				System.out.println(valido);
				if(valido.equals("Valid")) { 
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
		iv = AES.generateIv("Kdata.iv");
		Main main = new Main();
		String KloginEncrypt = main.register(user, password, salt, iv);
		PubPrivKey par = new PubPrivKey();
		par.generateKeyPar();
		par.SaveKeyPair();
		par.LoadKeyPair();
		kdata = main.getKdatosHashed();

		boolean connected = establishConnection();			
		if(connected) {
		
			try {
				writeSocket(skServidor,"register");
				writeSocket(skServidor,user+" "+KloginEncrypt);
				writeFileSocket(skServidor,"salt");
				writeFileSocket(skServidor,"Kdata.iv");
				writeFileSocket(skServidor,"public.key");
				AES.encryptFile("private.key",kdata,iv);
				writeFileSocket(skServidor,"private.key.enc");
				String valido = readSocket(skServidor, "");
				System.out.println("Aqui estoy");

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
	
	public static String listFiles(String user) {
		String result = "";
		boolean connected = establishConnection();			
		if(connected) {
			try {
				writeSocket(skServidor,"list " + user);
				result = readSocket(skServidor, "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static String listUsers(String user) {
		String result = "";
		boolean connected = establishConnection();			
		if(connected) {
			try {
				writeSocket(skServidor,"listUsers " + user);
				result = readSocket(skServidor, "");
				System.out.println(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static String listShared(String user) {
		String result = "";
		boolean connected = establishConnection();			
		if(connected) {
			try {
				writeSocket(skServidor,"listShared " + user);
				result = readSocket(skServidor, "");
				System.out.println(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	public static void shareZip(String path, String user, String toUser, String name) {
		boolean result = false;
		boolean connected = establishConnection();			
		if(connected) {
			try {
				writeSocket(skServidor,"share " + toUser + " " + user + " " + name);
				readFileSocket(skServidor,toUser + ".public.key");
				PubPrivKey pub = new PubPrivKey();
				//se pasa el kfile no el kdata fumao, entonces debes descargar el kfile y el iv
				readFileSocket(skServidor,path + File.separator + name.replace(".encrypt",".iv.enc"));
				readFileSocket(skServidor,path + File.separator + name.replace(".encrypt",".key.enc"));
				AES.decryptFile(path + File.separator + name.replace(".encrypt",".iv.enc"), kdata, iv);
				AES.decryptFile(path + File.separator + name.replace(".encrypt",".key.enc"), kdata, iv);
				
				//IvParameterSpec ivFile = AES.readIV(path + File.separator + name.replace(".encrypt",".iv"));
				SecretKey sharedKey = AES.ReadKey(path + File.separator + name.replace(".encrypt",".key"));
				byte[] kdataEncrypted = pub.encryptShared(true, AES.convertSecretKeyToString(sharedKey),toUser + ".public.key");
				pub.saveBytes(kdataEncrypted, "tempkdata.key");
				writeFileSocket(skServidor,"tempkdata.key");
				IvParameterSpec ivFinal = AES.readIV(path+File.separator + name.replace(".encrypt", ".iv"));
				byte[] ivEncrypted = pub.encryptShared(true, ivFinal.toString(), toUser + ".public.key");
				pub.saveBytes(ivEncrypted, "temp.iv");
				writeFileSocket(skServidor, "temp.iv");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void downloadSharedFiles(String path, String user, String toUser, String name) {
		System.out.println(path);
		boolean connected = establishConnection();			
		if(connected) {
			try {
				writeSocket(skServidor,"downloadShared " + toUser + " " + user + " " + name);
				readFileSocket(skServidor,path + File.separator + name.replace(".encrypt", ".key.pub"));
				readFileSocket(skServidor,path + File.separator + name.replace(".encrypt", ".iv.pub"));
				readBigFileSocket(skServidor,path + File.separator + name);
				
				PubPrivKey pub = new PubPrivKey();
				pub.LoadKeyPair();
				byte[] kfileEncrypt = pub.readBytes(path + File.separator + name.replace(".encrypt", ".key.pub"));
				byte[] kfileB = pub.decrypt(false, kfileEncrypt);
				pub.saveBytes(kfileB, path + File.separator + name.replace(".encrypt", ".key"));
				//SecretKey kfile = AES.ReadKey(path + File.separator + name.replace(".encrypt", ".key"));
				
				byte[] ivEncrypt = pub.readBytes(path + File.separator + name.replace(".encrypt", ".iv.pub"));
				byte[] iv = pub.decrypt(false, ivEncrypt);
				pub.saveBytes(iv, path + File.separator + name.replace(".encrypt", ".iv"));
				//IvParameterSpec ivFinal = AES.readIV(path + File.separator + name.replace(".encrypt", ".iv"));
				
				//AES.ReadKey(name);
				//AES.decryptFile(path + File.separator +name, kfile, ivFinal);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static boolean downloadFiles(String path, String user, String name) {
		boolean result = false;
		System.out.println(path);
		boolean connected = establishConnection();			
		if(connected) {
			try {
				writeSocket(skServidor,"download " + user + " " + name);
				readFileSocket(skServidor,path + File.separator + name.replace(".encrypt", ".iv.enc"));
				readFileSocket(skServidor,path + File.separator + name.replace(".encrypt", ".key.enc"));
				AES.decryptFile(path + File.separator +name.replace(".encrypt", ".iv.enc"), kdata, iv);
				AES.decryptFile(path + File.separator +name.replace(".encrypt", ".key.enc"), kdata, iv);
				readBigFileSocket(skServidor,path + File.separator + name);
				result = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static boolean signature(String path, String user, String name) {
		boolean result = false;
		System.out.println(path);
		boolean connected = establishConnection();			
		if(connected) {
			try {
				writeSocket(skServidor,"signature " + user + " " + name);
				readFileSocket(skServidor,path + File.separator + name.replace(".encrypt", ".checksum.priv"));
				readFileSocket(skServidor,path + File.separator + user +".pub");
				PubPrivKey pub = new PubPrivKey();
				byte[] checksumEncrypted = pub.readBytes(path + File.separator + name.replace(".encrypt", ".checksum.priv"));
				PublicKey pubk = pub.fileToPub(path + File.separator + user +".pub");
				byte[] checksum = pub.decryptSigned(checksumEncrypted, pubk);
				result = pub.compareChecksum(path + File.separator + name, checksum);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static boolean uploadFiles(String path, String user, String name, String checksum) {
		
		boolean result = false;
		System.out.println(path);
		boolean connected = establishConnection();			
		if(connected) {
			try {
				PubPrivKey pub = new PubPrivKey();
				pub.LoadKeyPair();
				byte[] kfileB = pub.encrypt(false, checksum);
				pub.saveBytes(kfileB, path + File.separator + name + ".checksum.priv");
				
				//Falta enviar iv y key encriptados con Kdata
				writeSocket(skServidor,"upload " + user + " " + name);
				AES.encryptFile(path + File.separator + name +".iv", kdata, iv);
				writeFileSocket(skServidor,path + File.separator + name +".iv.enc");
				AES.encryptFile(path + File.separator + name +".key", kdata, iv);
				writeFileSocket(skServidor,path + File.separator + name +".key.enc");
				writeFileSocket(skServidor, path + File.separator + name +".checksum.priv");
				writeBigFileSocket(skServidor,path + File.separator + name +".encrypt");
				result = true;
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

	//Funci?n a llamar desde la interfaz para crear la conexi?n
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
			
			
			System.out.println("Estableciendo conexi?n...");
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
			System.out.println("El servidor no est? disponible");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean closeConnection() {
		try {
			System.out.println("Cerrando conexi?n...");
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
                System.out.println("Comunicaci?n perdida, cerrando conexi?n");
                //System.out.println("Error: " + e.toString());
                return "-1;";
        }       
    }
	
	
	//The number of bytes read without permission is as many as 16383-16=16367 bytes.
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
        fos.close();
	}
	
	public static void readBigFileSocket(Socket socket, String fileName) throws IOException {
		//https://stackoverflow.com/questions/17285846/large-file-transfer-over-java-socket
		int bytesRead;
	    InputStream in;
	    int bufferSize=0;

	    try {
	        bufferSize=socket.getReceiveBufferSize();
	        in=socket.getInputStream();
	        DataInputStream clientData = new DataInputStream(in);
	        System.out.println(fileName);
	        OutputStream output = new FileOutputStream(fileName);
	        byte[] buffer = new byte[bufferSize];
	        int read;
	        while((read = clientData.read(buffer)) != -1){
	            output.write(buffer, 0, read);
	        }

	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
	
    public static void writeBigFileSocket(Socket socket, String filename) {
        try {
        	byte[] mybytearray = new byte[8192];
        	File myFile = new File(filename);
            FileInputStream fis = new FileInputStream(myFile);  
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            OutputStream os;
            os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            
            int read;
            while((read = dis.read(mybytearray)) != -1){
                dos.write(mybytearray, 0, read);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	/*
	 * 		Scanner in = new Scanner(sock.getInputStream());
    	InputStream is = sock.getInputStream();
        PrintWriter pr = new PrintWriter(sock.getOutputStream(), true);
        String FileName = in.nextLine();
        int FileSize = in.nextInt();
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        byte[] filebyte = new byte[8192];
        int count = 0;
        int suma = 0;
        while ((count = is.read(filebyte)) > 0) {
        //int file = is.read(filebyte, 0, filebyte.length);
        	bos.write(filebyte, 0, count);
        	suma += count;
        }
         
        System.out.println("Incoming File: " + FileName);
        System.out.println("Size: " + FileSize + "Byte");
        if(FileSize == suma)System.out.println("File is verified");
        else System.out.println("File is corrupted. File Recieved " + count + " Byte");
        pr.println("File Recieved SUccessfully.");
        bos.close();
        fos.close();
	 */
}
