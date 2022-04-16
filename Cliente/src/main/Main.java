package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


import encrypt.AES;
import serverConnection.ServerConnection;

public class Main {
	
	private static String password;
	private static String Klogin, Kdatos;
	private static byte[] salt;
	
	public static String generatePassword(int length, boolean m, boolean M, boolean e, boolean n) {
		String mayus, minus, especiales, numeros, total="", resultado="";
		minus ="abcdefghijklmnñopqrstuvwxyz";
		mayus ="ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
		especiales="+-*/&#!<>=@[]{}_-";
		numeros="0123456789";
		
		if(m)
			total += minus;
		if(M)
			total += mayus;
		if(e)
			total+=especiales;
		if(n)
			total+=numeros;
				
		Random rand = new Random();
		
		for(int i = 0; i < length; i++) {
			int randInt =  rand.nextInt(total.length());
			resultado += total.charAt(randInt);
		}
		
		return resultado;
	}
	
	public static boolean isGoodPasswd(String password) {
		boolean result;
		int length = 8, upper = 0, lower=0, special = 0, digits=0;
		char letter;
		
		if(password.length() < length) {
			System.out.println("Password requires a minimum of " + length + " characters");
			result = false;
		}else {
			for(int i=0; i < password.length(); i++) {
				letter = password.charAt(i);
				if(Character.isUpperCase(letter))
					upper++;
				else if(Character.isLowerCase(letter))
					lower++;
				else if(Character.isDigit(letter))
					digits++;
				else
					special++;
			}
			
			if(upper < 1 || lower < 1 || special < 1 || digits < 1) {
				System.out.println("Weak password");
				result = false;
			}else{
				System.out.println("Strong password");
				result = true;
			}
		}
		return result;
	}
		
	public static void dividePassword(String password) {
		final int mid = password.length()/2;
		Klogin = password.substring(0,mid);
		Kdatos = password.substring(mid);
	}
	
	public static String calculateKlogin(String password) {
		final int mid = password.length()/2;
		return password.substring(0,mid);
	}
	
	public static String calculateKdatos(String password) {
		final int mid = password.length()/2;
		return password.substring(mid);
	}
	
	public static byte[] generateSalt() throws Exception {
		byte[] salt2 = new byte[100];
	    SecureRandom random = new SecureRandom();
	    random.nextBytes(salt2);
	    salt = salt2;
		writeByte(salt);
	    return salt2;
	}
	
    public static byte[] readByte(File file)
            throws Exception
        {
     
            // Creating an object of FileInputStream to
            // read from a file
            FileInputStream fl = new FileInputStream(file);
     
            // Now creating byte array of same length as file
            byte[] arr = new byte[100];
     
            // Reading file content to byte array
            // using standard read() method
            fl.read(arr);
     
            // lastly closing an instance of file input stream
            // to avoid memory leakage
            fl.close();
     
            // Returning above byte array
            return arr;
        }
	 // Method which write the bytes into a file
    static void writeByte(byte[] bytes)
    {
        try {
  
            // Initialize a pointer
            // in file using OutputStream
            OutputStream os = new FileOutputStream("salt");
  
            // Starts writing the bytes in it
            os.write(bytes);
            System.out.println("Successfully"+ " byte inserted");
            // Close the file
            os.close();
        }
  
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
	
	public static SecretKey hashPassword(String password, byte[] salt) throws Exception{
		    
		    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		    
		    KeySpec spec = new PBEKeySpec(password.toCharArray(),salt, 65536, 256);
		    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		    return secret;
		}
	
	public static boolean validPassword(String password, String passEncrypt, byte[] sal, IvParameterSpec iv) throws Exception {
		String Klog = calculateKlogin(password);
		String Kdata = calculateKdatos(password);
		SecretKey KlogHash = hashPassword(Klog, sal);
		SecretKey KdataHash = hashPassword(Kdata, sal);
		String passLogEncrypt = AES.encryptPassword(AES.convertSecretKeyToString(KlogHash), KdataHash, iv);

		if(passLogEncrypt.equals(passEncrypt))
			return true;
		else
			return false;
	}
	
	public static String register(String user, String password, byte[] sal, IvParameterSpec iv) throws Exception {
		
		dividePassword(password);
		SecretKey KloginHashed = hashPassword(Klogin,sal);
		SecretKey KdatosHashed = hashPassword(Kdatos,sal);
		System.out.println(AES.convertSecretKeyToString(KloginHashed));
		System.out.println(AES.convertSecretKeyToString(KdatosHashed));
		String KloginEncrypt = AES.encryptPassword(AES.convertSecretKeyToString(KloginHashed),KdatosHashed,iv);
		System.out.println(KloginEncrypt);
		
		String all = salt.toString() + iv.toString() + KloginEncrypt;
		return KloginEncrypt;	
	}
	
	public static String login(String user,String password,byte[] sal, IvParameterSpec iv) throws Exception {
		
		dividePassword(password);
		SecretKey KloginHashed = hashPassword(Klogin,sal);
		SecretKey KdatosHashed = hashPassword(Kdatos,sal);
		System.out.println(AES.convertSecretKeyToString(KloginHashed));
		System.out.println(AES.convertSecretKeyToString(KdatosHashed));

		String KloginEncrypt = AES.encryptPassword(AES.convertSecretKeyToString(KloginHashed),KdatosHashed,iv);
		System.out.println(KloginEncrypt);
		return KloginEncrypt;
		
		//System.out.println(closeConnection());
	}
	
	public static void main(String[] args) throws Exception {
		password = generatePassword(64,true,true,true,true);
		isGoodPasswd("Password1234@");
		dividePassword(password);

		System.out.println("Generated password: " + password);
		System.out.println("Klogin= " + Klogin);
		System.out.println("Kdatos= " + Kdatos);

		generateSalt();
		SecretKey KloginHashed = hashPassword(Klogin,salt);
		SecretKey KdatosHashed = hashPassword(Kdatos,salt);
		
		String KloginH = AES.convertSecretKeyToString(KloginHashed);
		String KdatosH = AES.convertSecretKeyToString(KdatosHashed);
		System.out.println("Klogin hashed: " + KloginH + " length: " + KloginH.length());
		System.out.println("KDatos hashed: " + KdatosH + " length: " + KdatosH.length());
		
		try {
			IvParameterSpec iv = AES.generateIv("password.iv");
			String KloginEncrypt = AES.encryptPassword(AES.convertSecretKeyToString(KloginHashed),KdatosHashed,iv);
			String KloginDecrypt = AES.decryptPassword(KloginEncrypt,KdatosHashed,iv);
			System.out.println("Klogin Encrypted: " + KloginEncrypt + " length: " + KloginEncrypt.length());
			System.out.println("Klogin Decrypted: " + KloginDecrypt);
			
			if(validPassword(password, KloginEncrypt, salt, iv))
				System.out.println("Contraseña válida");
			else
				System.out.println("Contraseña inválida");
			
			System.out.println("usu");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Debemos pasar la Klogin al servidor y la Kdatos también
		//Reto propuesto: restringir uso de password a 1 mes y 2FA
	}
}
