package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.stream.Stream;

public class ServerThread extends Thread{
	@SuppressWarnings("FieldMayBeFinal")
    private Socket skCliente;
	private static String path = "src\\resources\\";
		
	public ServerThread(Socket skCliente) {
	        this.skCliente = skCliente;
	}
	
	public boolean validUser(String login) {
		
		boolean result = false;
		
		StringBuilder contentBuilder = new StringBuilder();
		System.out.println(path + login.split(" ")[0] + File.separator + "pass.txt");
		System.out.println(login);
		try (BufferedReader br = new BufferedReader(new FileReader(path + login.split(" ")[0] + File.separator + "pass.txt"))) 
		{

		    String sCurrentLine;
		    while ((sCurrentLine = br.readLine()) != null) 
		    {
		    	if(sCurrentLine.equals(login.split(" ")[1])) {
		    		result = true;
		    	}
		      //  contentBuilder.append(sCurrentLine).append("\n");
		    }
		} 
		catch (IOException e) 
		{
		    e.printStackTrace();
		}

		//System.out.println(fileContent);
		
		
		return result;
	}
	
	public static boolean isNewUser(String user) {
		boolean result = false;
		
		Path directorio = Paths.get(path + user.split(" ")[0]);
		
		if (Files.exists(directorio)) {
			result = true;
		}
		
		/*StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) 
		{
		    String sCurrentLine;
		    while ((sCurrentLine = br.readLine()) != null) 
		    {
		    	String users = sCurrentLine.split(" ")[0];
		    	user = user.split(" ")[0];
		    	System.out.println(users + " - " + user);
		    	if(user.equals(users)) {
		    		result = true;
		    	}
		    }
		} 
		catch (IOException e) 
		{
		    e.printStackTrace();
		}*/
		
		return result;
	}
	 
	
	public static void newUser(String user) {
		
		try {
			new File(path + user.split(" ")[0]).mkdirs();
			File file = new File(path + user.split(" ")[0] + File.separator + "pass.txt");
			FileWriter fileWriter = new FileWriter(file,true);
			fileWriter.append(user.split(" ")[1]);
			fileWriter.close();
		}catch (IOException e) {
		    e.printStackTrace();//exception handling left as an exercise for the reader
		}
	}
	
	public static String getSalt(String user) {
		String salt ="";
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(path + user + File.separator + "pass.txt"))) 
		{

		    String sCurrentLine;
		    while ((sCurrentLine = br.readLine()) != null) 
		    {
		    	salt = sCurrentLine.split(" ")[1];
		      //  contentBuilder.append(sCurrentLine).append("\n");
		    }
		} 
		catch (IOException e) 
		{
		    e.printStackTrace();
		}
		return salt;
	}
	
    @Override
    public void run() {
        int resultado = 0;
        String cadena = "";
        
        try {
            while (resultado != -1) {
            	
            	String peticion = readSocket(skCliente, "");
            	String log = peticion.split(" ")[0];
            	if(log.equals("login")) {
            		System.out.println("Logging...");
            		String user = peticion.split(" ")[1];
            		writeFileSocket(skCliente,path+ user + File.separator + "salt");
            		writeFileSocket(skCliente,path+ user + File.separator + "Kdata.iv");
                	String login = readSocket(skCliente, "");
                	System.out.println(login);
                	if(validUser(login)) {
                		writeSocket(skCliente, "Valid");
                    	resultado = -1;
                	}else {
                		writeSocket(skCliente, "Invalid");
                    	resultado = -1;
                	}  	
            	}else if(peticion.equals("register")) {
            		System.out.println("Registering new user...");
            		String register = readSocket(skCliente, "");
     
                	if(!isNewUser(register)) {
                		//writeSocket(skCliente, "Creating new user");
                		newUser(register);
                		readFileSocket(skCliente, path + register.split(" ")[0] + File.separator + "salt");
                		readFileSocket(skCliente, path + register.split(" ")[0] + File.separator + "Kdata.iv");
                		writeSocket(skCliente, "Registered");

                    	resultado = -1;
                	}else {
                		writeSocket(skCliente, "User Already Exists");
                    	resultado = -1;
                	}  	
            	}else {
            		System.out.println("Invalid Request");
            	}
            	
            }
            System.out.println("Cliente desconectado");
            skCliente.close();
        } catch (IOException e) {
            System.out.println("Error en run");
            System.out.println("Error: " + e.toString());
        }
    }
    
    public String readSocket (Socket p_sk, String p_Datos)
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

    public void writeSocket (Socket p_sk, String p_Datos)
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
    
    public void readFileSocket(Socket sock, String filename) {
    	try {
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
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void writeFileSocket(Socket sock, String filename) {
		try { 
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
		}catch(Exception e) {
			e.printStackTrace();
		}
    }

}
