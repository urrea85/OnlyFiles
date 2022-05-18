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
import java.util.logging.Level;
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
	
	public static String userFiles(String user) {
		
		String files ="";
		
		File dir = new File(path+user);
		String[] ficheros = dir.list();
		
		if (ficheros == null)
			  System.out.println("No hay ficheros en el directorio especificado");
		else { 
		  for (int x=0;x<ficheros.length;x++) {
		    if(ficheros[x].contains(".encrypt")) {
			    files += ficheros[x] + " ";
		    }
		  }
		}
		System.out.println("Estos son los ficheros: " + files);
		
		return files;
	}
	
	
public static String usersPub(String user) {
		
		String files ="";
		
		File dir = new File(path);
		String[] ficheros = dir.list();
		
		if (ficheros == null)
			  System.out.println("No hay usuarios");
		else { 
		  for (int x=0;x<ficheros.length;x++) {
		    if(!ficheros[x].equals(user)) {
			    files += ficheros[x] + " ";
		    }
		  }
		}
		System.out.println("Estos son los usuarios: " + files);
		
		return files;
	}


public static String userSharedFiles(String ruta) {
	
	String files ="";
	
	File dir = new File(path+ruta);
	String[] ficheros = dir.list();
	
	if (ficheros == null)
		  System.out.println("No hay ficheros en el directorio especificado");
	else { 
	  for (int x=0;x<ficheros.length;x++) {
		  files += ficheros[x].replace(".key.pub", "") + " ";
	  }
	}
	System.out.println("Estos son los ficheros: " + files);
	
	return files;
}

public static String infoShared(String user) {
	
	String files ="";
	
	File dir = new File(path+user+File.separator);
	String[] ficheros = dir.list();
	
	if (ficheros == null)
		  System.out.println("No hay usuarios");
	else { 
	  for (int x = 0; x < ficheros.length ; x++) {
		  Path path2 = Paths.get(path+user+File.separator+ficheros[x]);
		  if(Files.isDirectory(path2)) {
			  String result = userSharedFiles(user+File.separator+ficheros[x]);
			  String[] remoteFiles = result.split(" ");		
				for(String file: remoteFiles) {
					files += ficheros[x] + ":" + file + " ";
				}
		  }
	  }
	}
	System.out.println("Contenido de la carpeta: " + files);
	return files;
}
	
    @Override
    public void run() {
        int resultado = 0;
        String cadena = "";
        
        Server.LOGGER.log(Level.INFO,"Atendiendo cliente");
        
        try {
            while (resultado != -1) {
            	
            	String peticion = readSocket(skCliente, "");
            	String log = peticion.split(" ")[0];
            	if(log.equals("login")) {
            		System.out.println("Logging...");
            		String user = peticion.split(" ")[1];
            		writeFileSocket(skCliente,path+ user + File.separator + "kdata.iv");
            		writeFileSocket(skCliente,path+ user + File.separator + "salt");
            		writeFileSocket(skCliente,path+ user + File.separator + "public.key");
            		writeFileSocket(skCliente,path+ user + File.separator + "private.key.enc");
                	String login = readSocket(skCliente, "");
                	System.out.println(login);
                	if(validUser(login)) {
                		writeSocket(skCliente, "Valid");
                		Server.LOGGER.log(Level.INFO,"Usuario " + user + " inicio de sesion correcto");
                    	resultado = -1;
                	}else {
                		writeSocket(skCliente, "Invalid");
                		Server.LOGGER.log(Level.INFO,"Usuario " + user + " inicio de sesion incorrecto");
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
                		readFileSocket(skCliente, path + register.split(" ")[0] + File.separator + "public.key");
                		readFileSocket(skCliente, path + register.split(" ")[0] + File.separator + "private.key.enc");
                		writeSocket(skCliente, "Registered");
                		Server.LOGGER.log(Level.INFO,"Usuario " + register + " registrado correctamente");
                    	resultado = -1;
                	}else {
                		writeSocket(skCliente, "User Already Exists");
                		Server.LOGGER.log(Level.INFO,"Usuario " + register + " ya registrado");
                    	resultado = -1;
                	}  	
            	}else if(log.equals("upload")){
            		System.out.println(peticion);
            		String user = peticion.split(" ")[1];
            		String name = peticion.split(" ")[2];
            		readFileSocket(skCliente, path+ user + File.separator + name + ".iv.enc");
            		readFileSocket(skCliente, path+ user + File.separator + name +".key.enc");
            		readFileSocket(skCliente, path+ user + File.separator + name +".checksum.priv");
            		readBigFileSocket(skCliente, path+ user + File.separator + name + ".encrypt");
            		Server.LOGGER.log(Level.INFO,"Usuario " + user + " sube correctamente " + name);
            		resultado = -1;
            	}else if(log.equals("download")) {
               		String user = peticion.split(" ")[1];
            		String name = peticion.split(" ")[2];
            		writeFileSocket(skCliente, path+ user + File.separator + name.replace(".encrypt", ".iv.enc"));
            		writeFileSocket(skCliente, path+ user + File.separator + name.replace(".encrypt", ".key.enc"));
            		writeBigFileSocket(skCliente, path+ user + File.separator + name);
            		Server.LOGGER.log(Level.INFO,"Usuario " + user + " descarga correctamente " + name);
            		resultado = -1;
            	}else if(log.equals("list")){
            		String user = peticion.split(" ")[1];
            		String files = userFiles(user);//funcion para pasar string de nombre zips
            		writeSocket(skCliente,files);
            		resultado = -1;
            	}else if(log.equals("listUsers")) {
            		String user = peticion.split(" ")[1];
            		String users = usersPub(user);
            		writeSocket(skCliente,users);
            		resultado = -1;
            	}else if(log.equals("share")) {
            		String user = peticion.split(" ")[1];
            		String userReal = peticion.split(" ")[2];
            		String name = peticion.split(" ")[3];
            		writeFileSocket(skCliente,path+ user + File.separator + "public.key");
            		writeFileSocket(skCliente,path+ userReal + File.separator + name.replace(".encrypt", ".iv.enc"));
            		writeFileSocket(skCliente,path+ userReal + File.separator + name.replace(".encrypt", ".key.enc"));
            		new File(path + user + File.separator+  userReal).mkdirs();
            		readFileSocket(skCliente,path+ user + File.separator + userReal + File.separator + name+".key.pub");
            		readFileSocket(skCliente,path+ user + File.separator + userReal + File.separator + name+".iv.pub");
            		Server.LOGGER.log(Level.INFO,"Usuario " + user + " comparte " + name + " con " + userReal);
            		resultado = -1;
            	}else if(log.equals("listShared")) {
            		String user = peticion.split(" ")[1];
            		String result = infoShared(user);
            		writeSocket(skCliente,result);
            		resultado = -1;
            	}else if(log.equals("signature")) {
            		String user = peticion.split(" ")[1];
            		String name = peticion.split(" ")[2];
            		writeFileSocket(skCliente,path+ user + File.separator + name.replace(".encrypt", ".checksum.priv"));
            		writeFileSocket(skCliente,path+ user + File.separator + "public.key");
            		resultado = -1;
            	}else if(log.equals("downloadShared")) {
            		String user = peticion.split(" ")[1];
            		String userReal = peticion.split(" ")[2];
            		String name = peticion.split(" ")[3];
            		writeFileSocket(skCliente,path+ userReal + File.separator + user + File.separator + name +".key.pub");
            		writeFileSocket(skCliente,path+ userReal + File.separator + user + File.separator + name +".iv.pub");
            		writeBigFileSocket(skCliente,path+ user + File.separator + name);
            		Server.LOGGER.log(Level.INFO,"Usuario " + userReal + " descarga " + name + " de " + user);
            		resultado = -1;
            	}else {
            		//System.out.println("Invalid Request");
            		Server.LOGGER.log(Level.WARNING, "Peticion invalida");
            		resultado = -1;
            	}
            	
            }
            //System.out.println("Cliente desconectado");
            Server.LOGGER.log(Level.INFO,"Cliente desconectado");
            skCliente.close();
        } catch (Exception e) {
            //System.out.println("Error en run");
            //System.out.println("Error: " + e.toString());
        	Server.LOGGER.log(Level.SEVERE,"Error: " + e.toString());
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
        if(FileSize == file) {
        	System.out.println("File is verified");
        }
        else {
        	//System.out.println("File is corrupted. File Recieved " + file + " Byte");
    		Server.LOGGER.log(Level.WARNING,"Archivo " + file + " corrupto");
        }
        pr.println("File Recieved SUccessfully.");
        bos.close();
        fos.close();
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
	        bis.close();
		}catch(Exception e) {
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
    
	public static void readBigFileSocket(Socket socket, String fileName){
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
}
