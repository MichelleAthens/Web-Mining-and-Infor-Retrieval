package webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Webcrawler {
    private HashSet<String> links;
    private HashSet<String> relatedlinks;
    private HashSet<String> title;
    private List<List<String>> articles;
    private String [] related = {"pc","keyboard","mlg","mouse","rules","challenge","fun","skill","chance","coordination","input","play","gameplay","computer","interactive","goals","challenges","role","tv","screen","video","games","console","platform","multiplayer","singleplay","handheld","mobile","online","esrb","player","conventions","convention","gamepads","joysticks","action","fighting","shoot","co-op","puzzle","strategy","survival","controller","horror","role-playing","role","playing","simulation","massive","turn","adventure"};    
    int counter=0;
    int relatecounter=0;
    int failed=0;
    int relatedarticles =0;
    int unrelated=0;
    int i=0;
    int j=0;
    int check =0;
    String link;
    Elements otherlinks;
    Document doc;
    public Webcrawler() {
        links = new HashSet<>();
        articles = new ArrayList<>();
        relatedlinks = new HashSet<>();
        title = new HashSet<>();
        
    }

    public void getPageLinks(String URL) {
        if (!links.contains(URL)) {
            try {
                Document document = Jsoup.connect(URL).timeout(10000).get(); //Connect to the url and put in document
                links.add(URL);//Add the url
                Elements content = document.getElementsByTag("p"); //Go to the <p> tags
                Elements otherLinks = content.select("a[href^=\"/wiki/\"]"); //Extract the links
                
                for (Element page : otherLinks) {
                    String href = page.attr("href");
                    
                    if(!links.contains("https://en.wikipedia.org"+href))
                    {
                    links.add("https://en.wikipedia.org"+href);
                        i++;
                    }
                    }
            } catch (IOException e) {
                failed++;
                System.err.println(e.getMessage());
            }
        }
    }
public static boolean stringContainsItemFromList(String inputStr, String[] items) {
    return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
}
    
    public void getArticles() {
        FileWriter writer;
        try{
            writer = new FileWriter("Video Games Related"); //Make document 
        links.forEach(x -> { //For each url in links
           
            Document document; //make a document
            try {
                document = Jsoup.connect(x).timeout(10000).get(); //Connects to url and parses the document
                
                        String body = document.text(); //Get the document's text intoa  string
                        String lower = body.toLowerCase(); //Turn the text in the document to lower case
                       
                        if(stringContainsItemFromList(lower,related)){ //Look for any 1 match
                            
                            for(int i=0; i<related.length; i++) //If match found, look for more matches
                            {
                            if(document.text().matches("^.*?" + related[i] + ".*$"))//If document matches the term between anything
                            {
                                relatecounter++;
                            }
                            if(relatecounter==2)
                            {
                                break;
                            }
                            }
                            if (relatecounter==2) {
                        
                        relatecounter=0;//reset the counter
                        //relatedarticles++;
                        //Related articles increment
                        
                        URL url = new URL(x);
                        File destination = new File(document.title() + ".html");
                        
                        FileUtils.copyURLToFile(url, destination);
                        System.out.println("\n" + "Title: " + document.title() + "\n" + "URL " +x + "\n" );
                        writer.write("\n" + "Title: " + document.title() + "\n" + "URL: " + x + "\n" + "\n");
                        relatedlinks.add(x); //Add link to link array for storage
                        title.add(document.title()); //Add title to title array for storage
                        
                    }
                    else{ //Only 1 match
                        unrelated++;
                    }
                        }else{ //No matches
                            unrelated++;
                        }
                        

            } catch (IOException e) {
                failed++;
                System.out.println("\nDocument Failed: " + x + "\n");
                System.err.println(e.getMessage());
            }
        });
        writer.close();
        }  catch (IOException e) {
                failed++;
                System.err.println(e.getMessage());
            }
    }
    
    
    
   public static void main(String[] args) {
        Webcrawler crawl = new Webcrawler();
        crawl.getPageLinks("https://en.wikipedia.org/wiki/Video_game");
        crawl.getPageLinks("https://en.wikipedia.org/wiki/History_of_video_games");
        System.out.println("====================CRAWLING====================");
        crawl.getArticles();
        //System.out.println("================================================");
        
    }
}