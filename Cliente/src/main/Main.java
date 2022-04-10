package main;

import java.util.Random;

public class Main {

	private static String Klogin, Kdatos;
	
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
		System.out.println(password);
		final int mid = password.length()/2;
		Klogin = password.substring(0,mid);
		Kdatos = password.substring(mid);
		System.out.println("Klogin= " + Klogin);
		System.out.println("Kdatos= " + Kdatos);
	}
	
	public static void main(String[] args) {
		String password = generatePassword(64,true,true,true,true);
		dividePassword(password);
		isGoodPasswd("Password1234@");
		//Reto propuesto: restringir uso de password a 1 mes y 2FA
	}
}
