package encrypt;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	
	public static void main(String argv[]) throws Exception{
		
		SecretKey key = generateKey(128);
		String algorithm = "AES/CBC/PKCS5Padding";
		IvParameterSpec ivParameterSpec = generateIv(argv[3]);
		File inputFile = Paths.get(argv[0]).toFile();
		File encryptedFile = new File(argv[1]);
		File decryptedFile = new File(argv[2]);
		
		//******************
		encrypt(algorithm, key, ivParameterSpec, inputFile, encryptedFile);
		System.out.println("Encriptado.");
		decrypt(algorithm, key, readIV(argv[3]), encryptedFile, decryptedFile);
		System.out.println("Desencriptado.");
	}
	
	public static String encryptPassword(String input, SecretKey key, IvParameterSpec iv) throws Exception{
			String algorithm = "AES/CBC/PKCS5Padding";
		    Cipher cipher = Cipher.getInstance(algorithm);
		    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		    byte[] cipherText = cipher.doFinal(input.getBytes());
		    return Base64.getEncoder()
		        .encodeToString(cipherText);
		}
	public static String decryptPassword(String cipherText, SecretKey key,IvParameterSpec iv) throws Exception {
			String algorithm = "AES/CBC/PKCS5Padding";
		    Cipher cipher = Cipher.getInstance(algorithm);
		    cipher.init(Cipher.DECRYPT_MODE, key, iv);
		    byte[] plainText = cipher.doFinal(Base64.getDecoder()
		        .decode(cipherText));
		    return new String(plainText);
		}
	
	public static void encryptFile(String path, SecretKey key, IvParameterSpec iv) throws Exception {

		String algorithm = "AES/CBC/PKCS5Padding";
		File inputFile = Paths.get(path).toFile();
		File encryptedFile = new File(path + ".enc");
		encrypt(algorithm, key, iv, inputFile, encryptedFile);
	}
	
	public static void decryptFile(String path, SecretKey key, IvParameterSpec iv) throws Exception {

		String algorithm = "AES/CBC/PKCS5Padding";
		File encryptedFile = Paths.get(path).toFile();
		File decryptedFile = new File(path.replace(".enc", ""));
		System.out.println(encryptedFile.getName() + " - " + decryptedFile.getName());
		decrypt(algorithm, key, iv, encryptedFile, decryptedFile);
	}
	
	public void encryptController(String zipPath) throws Exception{
		SecretKey key = generateKey(128); 

		String algorithm = "AES/CBC/PKCS5Padding";
		IvParameterSpec ivParameterSpec = generateIv(zipPath.replace(".zip", ".iv"));
		File inputFile = Paths.get(zipPath).toFile();
		File encryptedFile = new File(zipPath.replace(".zip", ".encrypt"));
		String keyFile = zipPath.replace(".zip", ".key");
		
		//GUARDAMOS LA KEY EN EL DIRECTORIO
		SaveKey(key, keyFile);
		
		
		//******************
		encrypt(algorithm, key, ivParameterSpec, inputFile, encryptedFile);
	}
	
	public void decryptController(String zipPathEnc) throws Exception{
		
		String algorithm = "AES/CBC/PKCS5Padding";
		File encryptedFile = Paths.get(zipPathEnc).toFile();
		File decryptedFile = new File(zipPathEnc.replace(".encrypt", "Decrypt.zip"));
		String keyFile = zipPathEnc.replace(".encrypt", ".key");
		decrypt(algorithm, ReadKey(keyFile), readIV(zipPathEnc.replace(".encrypt", ".iv")), encryptedFile, decryptedFile);
	}
	
	
	public static void SaveKey(SecretKey key, String file) throws IOException, NoSuchAlgorithmException{
		FileWriter fichero = new FileWriter(file);
		PrintWriter pw = new PrintWriter(fichero);
		pw.println(convertSecretKeyToString(key));
		fichero.close();
	}
	
	
	
	public static SecretKey ReadKey(String file) throws IOException{
		File archivo = new File(file);
		FileReader fr = new FileReader(archivo);
		BufferedReader br = new BufferedReader(fr);
		SecretKey key= convertStringToSecretKey(br.readLine());
		fr.close();
		return key;
	}
	
	public static String convertSecretKeyToString(SecretKey secretKey) throws NoSuchAlgorithmException {
	    byte[] rawData = secretKey.getEncoded();
	    String encodedKey = Base64.getEncoder().encodeToString(rawData);
	    return encodedKey;
	}
	
	public static SecretKey convertStringToSecretKey(String encodedKey) {
	    byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
	    SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
	    return originalKey;
	}
	
	//n tamaño de la clave(128, 192, 256)
	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(n);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}
	
	
	
	public static IvParameterSpec generateIv(String file) throws IOException {
		byte[] iv = new byte[16];
		//saveIV(iv, file);
		//System.out.println("IV");
		//System.out.println(Arrays.toString(iv));
		new SecureRandom().nextBytes(iv);
		IvParameterSpec iV = new IvParameterSpec(iv);
		byte[] ivFinal = iV.getIV();
		saveIV(ivFinal,file);
		return iV;
	}
	
	
	public static void saveIV(byte[] iv, String file) throws IOException {
		FileOutputStream fs = new FileOutputStream(new File(file));
		BufferedOutputStream bos = new BufferedOutputStream(fs);
		bos.write(iv);
		bos.close();
	}
	
	
	public static IvParameterSpec readIV(String resource) throws IOException {
		byte[] iv = new byte[16];
		DataInputStream dis = null;
		dis = new DataInputStream(new FileInputStream(new File(resource)));
		dis.readFully(iv);
		if(dis != null) {
			dis.close();
		}
		//new SecureRandom().nextBytes(iv);
		return new IvParameterSpec(iv);
	}
	
	public static void encrypt(String algorithm, SecretKey key, IvParameterSpec iv, File inputFile, File outputFile) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		FileInputStream inputStream = new FileInputStream(inputFile);
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		byte[] buffer = new byte[64];
		int bytesRead;
		while((bytesRead = inputStream.read(buffer)) != -1) {
			byte[] output = cipher.update(buffer, 0, bytesRead);
			if(output != null) {
				outputStream.write(output);
			}
		}
		byte[] outputBytes = cipher.doFinal();
		if(outputBytes != null) {
			outputStream.write(outputBytes);
		}
		inputStream.close();
		outputStream.close();
	}
	
	public static void decrypt(String algorithm, SecretKey key, IvParameterSpec iv, File inputFile, File outputFile) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		FileInputStream inputStream = new FileInputStream(inputFile);
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		byte[] buffer = new byte[64];
		int bytesRead;
		while((bytesRead = inputStream.read(buffer)) != -1) {
			byte[] output = cipher.update(buffer, 0, bytesRead);
			if(output != null) {
				outputStream.write(output);
			}
		}
		byte[] outputBytes = cipher.doFinal();
		if(outputBytes != null) {
			outputStream.write(outputBytes);
		}
		inputStream.close();
		outputStream.close();
	}
}