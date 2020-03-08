package de.kisirisoft.tools.db.oracle.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * @author Ahmed Fikri
 *
 */
public class ClassHelper {
	static Scanner scanner = new Scanner(System.in);
	/**
	 * @param message
	 * @return
	 */
	static public String readInValue(String message) {
		System.out.println(">> "+ message);
		return scanner.nextLine();
	}
	
	/**
	 * @param filePath
	 * @return
	 */
	static public String readAllBytesJava7(String filePath) 
    {
        String content = "";
 
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
 
        return content;
    }
	
	/**
	 * @param message
	 */
	static public void writeOut(String message) {
		System.out.println(message);
	}
	
	static public boolean fileExists(String path) {
		File tmp = new File(path);
		return tmp.exists()&&tmp.isFile();
	}
}
