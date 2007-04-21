package net.sourceforge.jwbf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileLoader {
	
	public static final String MW110 = "testHtml/mw1_10/";
	
	private FileLoader() {
//		do nothing
	}
	public static String readFromFile(final File f) {
		  String thisLine;
		  String temp = "";
		   try {
		       BufferedReader in = new BufferedReader (
		                     new FileReader (f) );
		       try {
		           while( (thisLine = in.readLine()) != null ) {
		        	   temp += thisLine;
		           }
		           in.close();
		       } catch (IOException e) {
		           System.out.println("Read error " + e);
		       }
		   } 
		   catch (IOException e) {
		       System.out.println("Open error " + e);
		   }
		   return temp;
	}
	public static String readFromFile(final String s) {
		   File f = new File(s);
		   return readFromFile(f);
	}

}
