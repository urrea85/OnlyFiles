package compress;


import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.*;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Compress {
	
	public void zip(List<String> files, String name) throws IOException{
		
		FileOutputStream fos = new FileOutputStream(name);
		ZipOutputStream zipos = new ZipOutputStream(fos);
		
		for (String file : files) {
			File fileToZip = new File(file);
			FileInputStream fis = new FileInputStream(fileToZip);
			ZipEntry zip = new ZipEntry(fileToZip.getName());
			zipos.putNextEntry(zip);
			
			byte[] bytes = new byte[1024];
			int length;
			while((length = fis.read(bytes)) >= 0) {
				zipos.write(bytes,0,length);
			}
			fis.close();
		}
		zipos.close();
		fos.close();
		
	    File zip = new File(name);
	    if (!zip.exists()) {
	        throw new FileNotFoundException("The created zip file could not be found");
	    }
	}
	
	public static void unzip(String fileZip) throws Exception{
		File destDir = new File(fileZip.replace(".zip",File.separator));

		byte[] buffer = new byte[1024];
		//try {
			
			ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
			ZipEntry zipEntry = zis.getNextEntry();

			if(zipEntry!= null) {
				while(zipEntry != null) {
					File newFile = newFile(destDir, zipEntry);
				     if (zipEntry.isDirectory()) {
				         if (!newFile.isDirectory() && !newFile.mkdirs()) {
				             throw new IOException("Failed to create directory " + newFile);
				         }
				     } else {
				         // fix for Windows-created archives
				         File parent = newFile.getParentFile();
				         if (!parent.isDirectory() && !parent.mkdirs()) {
				             throw new IOException("Failed to create directory " + parent);
				         }
				         
				         // write file content
				         FileOutputStream fos = new FileOutputStream(newFile);
				         int len;
				         while ((len = zis.read(buffer)) > 0) {
				             fos.write(buffer, 0, len);
				         }
				         fos.close();
				     }
			 zipEntry = zis.getNextEntry();
				} 
			} else {
				ZipFile war = new ZipFile(fileZip); 
				Enumeration<? extends ZipEntry> entries = war.entries(); 
				 ZipFile zf = new ZipFile(fileZip);
				 ZipEntry e = entries.nextElement(); 
				 while(!e.getName().contains("meta.json")) {
					 e = entries.nextElement();
				 }
				  InputStream is = zf.getInputStream(e);
				  String separator = Pattern.quote(File.separator);
				  String[] paths = fileZip.split(separator);
				  String name = paths[paths.length-1];
				  new File(fileZip.replace(".zip", File.separator)).mkdirs();
				  Files.copy(is, Paths.get(fileZip.replace(".zip", File.separator) + name.replace("Decrypt.zip", "meta.json")));
			}
			
			zis.closeEntry();
			zis.close();
		/*} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(destinationDir, zipEntry.getName());
	
	    String destDirPath = destinationDir.getCanonicalPath();
	    String destFilePath = destFile.getCanonicalPath();
	
	    if (!destFilePath.startsWith(destDirPath + File.separator)) {
	        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
	    }
	
	    return destFile;
	}
	
	
	public JSONObject metadata(List<List<String>> jsonList) {
		JSONObject obj=new JSONObject();   
		
		for(int i = 0; i < jsonList.size(); i++) {
		
			obj.put(jsonList.get(i).get(0), jsonList.get(i).get(1));
		}
   
		return obj;
	}
	
	public void metaToFile(JSONObject json, String name) {
		FileWriter file;
		try {

			file = new FileWriter(name);
			file.write(json.toJSONString());
			file.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String showMeta(String path) throws IOException {
		JSONParser parser = new JSONParser();
		String result = "";
		try {
			String filename = path.replace(".encrypt", "meta.json");
			String separator = Pattern.quote(File.separator);
			String[] paths = path.split(separator);
			String fileName = paths[paths.length-1];
			String repl = "Decrypt" + File.separator + fileName.replace(".encrypt", "meta.json");
			filename = filename.replace("meta.json", repl);
			System.out.println("The file " +  filename);

			Object obj = parser.parse(new FileReader(filename));
			Map<String,String> map = (Map) obj;	
			for (Map.Entry<String, String> entry : map.entrySet()) {
				result += entry.getKey() + "/" + entry.getValue() + "->";
			    //System.out.println(entry.getKey() + "/" + entry.getValue());
			}
			 
		} catch (Exception e) {
				e.printStackTrace();
		  
		}
		
		return result;
	}
	
	  public void deleteFile(String filename) { 
	    File file = new File(filename); 
	    if (file.delete()) { 
	      System.out.println("Deleted the file: " + file.getName());
	    } else {
	      System.out.println("Failed to delete the file.");
	    } 
	  } 
	  private static final byte[] BUFFER = new byte[4096 * 1024];

	  
	  public static void copy(InputStream input, OutputStream output) throws IOException { 
		  int bytesRead; 
		  while ((bytesRead = input.read(BUFFER))!= -1) { 
			  output.write(BUFFER, 0, bytesRead); 
		} 
	}


	public static void main(String[] args) throws Exception {

		List<String> files = Arrays.asList("prueba.txt","imagen.jpg");
		File zipM;
		//Creating a JSON with metadata
		//JSONObject json = metadatos();
		
		//Saving the JSON created before
		//metaToFile(json,"metadata.json");
		
		//Try to unzip into a specific path
		File destFile = new File("C:\\Users\\josea\\OneDrive\\Escritorio\\filesToZip");
		unzip("C:\\Users\\josea\\OneDrive\\Escritorio\\filesToZip\\holaDecrypt.zip");
		

		//unzip("compress.zip",destFile);
}
}
