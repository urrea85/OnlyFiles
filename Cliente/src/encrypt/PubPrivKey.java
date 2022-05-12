package encrypt;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;



public class PubPrivKey {

	private static PublicKey publicKey;
	private static PrivateKey privateKey;
	
	public static void generateKeyPar() {
		try {
		    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		    keyPairGenerator.initialize(1024);
		    KeyPair keyPair = keyPairGenerator.generateKeyPair();

		    publicKey = keyPair.getPublic();
		    privateKey = keyPair.getPrivate();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	public void getPublicKey() {
		
	}
	
	public void getPrivateKey() {
		//Encriptar ya aquí
		
	}
	
	public static byte[] encrypt(boolean isPublic, String data) throws Exception{
		//
		String algorithm = "RSA/ECB/PKCS1Padding";
		Cipher cipher = Cipher.getInstance(algorithm);
		if(isPublic)
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		else
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		  
		//Adding data to the cipher
		byte[] input = data.getBytes();	  
		cipher.update(input);
		  
		//encrypting the data
		byte[] cipherText = cipher.doFinal();	 
	
		//System.out.println(new String(cipherText, "UTF8"));
		
		return cipherText;
	}
	
	public static byte[] decrypt(boolean isPublic, byte[] cipherText) throws Exception{
		//"RSA/ECB/PKCS1Padding"
		String algorithm = "RSA/ECB/PKCS1Padding";
		Cipher cipher = Cipher.getInstance(algorithm);
		if(isPublic)
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
		else
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
		
		byte[] decipheredText = cipher.doFinal(cipherText);
		//System.out.println(new String(decipheredText));
		
		return decipheredText;
	}
	
	
	public static void SaveKeyPair() throws IOException {

		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream("public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream("private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	}
	
	public static void LoadKeyPair() throws Exception {
		// Read Public Key.
		File filePublicKey = new File("public.key");
		FileInputStream fis = new FileInputStream("public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
 
		// Read Private Key.
		File filePrivateKey = new File("private.key");
		fis = new FileInputStream("private.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
 
		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		publicKey = keyFactory.generatePublic(publicKeySpec);
 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		privateKey = keyFactory.generatePrivate(privateKeySpec);
		
		
	}
	
	

	public static void main(String[] args) {
		generateKeyPar();
		try {
			byte[] cipherText = encrypt(true, "holabbquetal");
			byte[] decipheredText = decrypt(false, cipherText);
			byte[] cipherText2 = encrypt(true,"onichanwuwu");
			System.out.println(new String(decrypt(false,cipherText2)));
			SaveKeyPair();
			LoadKeyPair();
			System.out.println(new String(decrypt(false,cipherText2)));
			
			//Ejemplo Firma
			byte[] cipherText3 = encrypt(false,"mainnoquelavasaliar");
			System.out.println(new String(decrypt(true,cipherText3)));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
