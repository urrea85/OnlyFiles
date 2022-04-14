package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerThread extends Thread{
	@SuppressWarnings("FieldMayBeFinal")
    private Socket skCliente;
	
	public ServerThread(Socket skCliente) {
	        this.skCliente = skCliente;
	}
	 
    @Override
    public void run() {
        int resultado = 0;
        String cadena = "";
        
        try {
            while (resultado != -1) {
            	System.out.println("Atendiendo cliente");
            	writeSocket(skCliente, "Esto es una prueba");
            	resultado = -1;
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

}
