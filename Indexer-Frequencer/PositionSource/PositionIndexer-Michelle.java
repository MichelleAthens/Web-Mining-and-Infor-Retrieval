
package position;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import java.io.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.Hashtable;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/**
 *
 * @author Michelle
 */
public class Position {

    /**
     * @param args the command line arguments
     */
    
    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
    Boolean check = false;
    String word = inputStr;
    for(int i = 0; i < items.length; i++)
    {
        //System.out.println("Comparing " + word +" : " + items[i]);
        if(word.equals(items[i])){
            //System.out.println("STOPWORDFOIUND");
            check = true;
        }
    }
    return check;
}
    
    public static void main(String[] args) throws FileNotFoundException {
    int documentnumber = 0;
    List<String> stopwords = new ArrayList<String>();
    Map <String,String> mapping = new HashMap<String,String>();
    Scanner sc = new Scanner(new File("stopwords.txt"));
    Map<String, String> container = new HashMap<String,String>();
    while(sc.hasNextLine()){
        stopwords.add(sc.nextLine());
    }
    String[] stop = stopwords.toArray(new String[0]);
    
    /*System.out.println("STOP");
    for(int i = 0; i<stop.length; i++){
        
        System.out.println(stop[i]);
    }*/
    File folder = new File("crawled");
    File[] listofFiles = folder.listFiles();
    //List<Values> newlist = new ArrayList<Values>(); 
    try{
        
    for (int i = 0; i < listofFiles.length; i++) { //Looping for documents
       documentnumber++;
       File file = listofFiles[i];
       
       if (file.isFile() && file.getName().endsWith(".html")) {
       System.out.println(documentnumber + "FILE NAME: " + listofFiles[i]);
       
       String document = FileUtils.readFileToString(listofFiles[i]);
       Document doc = Jsoup.parse(document);
       String body = doc.text();
       String lower = body.toLowerCase();
       //System.out.println(lower);
       
       lower = lower.replaceAll("[.,;'\\]\\\\/\\[/:*#$={}|?+&!0123%\\_456789@<>^\"()-]", " ");
       
           String[] words = lower.split(" ");
           int position=0;
               for(String word : words){ //for every word in word
                   
                   position++;
               if(stringContainsItemFromList(word,stop)){
                    //continue; Dont do anything if its a stop word
                   //System.out.println("Stop word found");
                   //position++;
               }
               else if(mapping.containsKey(word)) //CONTAINS
                        { 
                        String temp = mapping.get(word);
                        String pos = Integer.toString(position);
                        String number = Integer.toString(documentnumber);
                        String data1 = "[ " + number + ", " + pos + " ]";
                        
                        String finaldata = temp + " | " + data1;
                        mapping.put(word, finaldata);
                        //System.out.println(word + " " + finaldata);
                        //If thheres a word in existance
                        //position = mapping.get(word);
                        //count++; //increment the map
                        //mapping.put(word,position); //Update the map
                        }
                    else{ //If there is no word in existance, map a new map entry
                        //mapping.put(word,1);
                        //String temp = mapping.get(word);
                        String pos = Integer.toString(position);
                        String number = Integer.toString(documentnumber);
                        String data = "[ " + number + ", " + pos + " ]";
                        mapping.put(word,data);
                        //System.out.println(word + " " + data);

                                
               }
               
               }
    File file1 = new File("index.txt");
    File file2 = new File("unique.txt");
    try{
    FileWriter fw = new FileWriter(file1);
    FileWriter fw2 = new FileWriter(file2);
    PrintWriter pw = new PrintWriter(fw);
    PrintWriter pw2 = new PrintWriter(fw2);
    
    for(Map.Entry<String, String> entry : mapping.entrySet()){ 
    String word = entry.getKey();
    String value = entry.getValue();
    pw.println(word + " " + value);
    pw2.println(word);
    }
    pw.close();
    pw2.close();
}catch (IOException e) {
               System.err.println(e.getMessage());
    
}//Map Completed
        /*for(Map.Entry<String, String> entry : mapping.entrySet()){ //For every entry in map
            //MAP = TERM : VALUE
            //
            if(container.containsKey(entry.getKey())){ //If table has the term
            //System.out.println(entry.getKey() + " |Term found in table");
            int frequency = entry.getValue(); //Get the frequency value from the map
            //System.out.println("FREQ: " + frequency);
            String tabledata = Integer.toString(frequency); //Convert the frequency to a string
            String data = "[ " + documentnumber + ", " + tabledata + " ]"; // [ document number, frequency]
            //System.out.println("DATA" + data);
            String word = entry.getKey();
            String olddata = container.get(word);
            String finaldata = data + " | " + olddata;
            container.put(entry.getKey(), finaldata); //TERM : DATATOGETHER
            //System.out.println("FINAL: " + finaldata);
            //container.put(entry.get)
            }
            else {//No term found
            //Values data = new Values(documentnumber,entry.getValue()); //Make object
            //System.out.println(entry.getKey() + " |No term found in table");
            int frequency = entry.getValue();
            String tabledata = Integer.toString(frequency);
            String data = "[ " + documentnumber + ", " + tabledata + " ]"; // [ document number, frequency]
            container.put(entry.getKey(),data);
            //System.out.println(data);       
            }
            //System.out.println("=======================");
        //System.out.println(entry.getKey() + " : " + entry.getValue());
    }*/
               
               
           
       //}
        
       //}catch (IOException e) {
       //        System.err.println(e.getMessage());
       //}
       }
    }
    
    }catch (IOException e) {
               System.err.println(e.getMessage());
    
}
    


}
    }
    

