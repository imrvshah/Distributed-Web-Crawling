import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg
{
	String str="";
	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	private Document htmlDocument;
    static String line;
    Map<String, Word> countMap = new HashMap<String, Word>();

	public boolean crawl(String url)
	{
		try
		{
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			Document htmlDocument = connection.get();
			this.htmlDocument = htmlDocument;
			if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
			{
				//System.out.println("\nVisiting " + url);
			}
			if(!connection.response().contentType().contains("text/html"))
			{
				//System.out.println("**Failure** Retrieved something other than HTML");
				return false;
			}
			Elements linksOnPage = htmlDocument.select("a[href]");
			//System.out.println("Found (" + linksOnPage.size() + ") links");
			for(Element link : linksOnPage)
			{
				this.links.add(link.absUrl("href"));
			}
			return true;
		}
		catch(IOException ioe)
		{
			// We were not successful in our HTTP request
			return false;
		}
	}

	public void count(){
		try{
			String text = this.htmlDocument.body().text();
			
			//System.out.println("Analyzing text...");
			//Create BufferedReader so the words can be counted
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
			while ((line = reader.readLine()) != null) {
            	line=line.toLowerCase();
            	String[] words = line.split("[^A-ZÃ…Ã„Ã–a-zÃ¥Ã¤Ã¶_0-9-]+");
            	//processString(words);
            	oneWordString(words, countMap);
            	twoWordString(words, countMap);
            	threeWordString(words, countMap);
			}

			reader.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		for (Word wrd: countMap.values()) {
			str+=wrd.word+'\t'+wrd.count+"\r\n";
		}

	}
	   public static void oneWordString(String[] words, Map<String, Word> countMap) {
       for (String word : words) {
       	//word=word.toLowerCase();
           if ("".equals(word) || word.length() < 4) {
               continue;
           }

           Word wordObj = countMap.get(word);
           if (wordObj == null) {
               wordObj = new Word();
               wordObj.word = word;
               wordObj.count = 0;
               countMap.put(word, wordObj);
           }

           wordObj.count++;
       }
   }
   
   public static void twoWordString(String[] words, Map<String, Word> countMap) {
	   for (int i=0; i<words.length-1; i++) {
		   if (words[i].compareTo("")!=0 && words[i+1].compareTo("")!=0) {
			   String word=(words[i] + " " + words[i+1]);
			   //word=word.toLowerCase();
			   if (!line.contains(word) || word.length() < 7) {
				   continue;
			   }

			   Word wordObj = countMap.get(word);
			   if (wordObj == null) {
				   wordObj = new Word();
				   wordObj.word = word;
				   wordObj.count = 0;
				   countMap.put(word, wordObj);
			   }
			   wordObj.count++;
		   }
	   }
   }

   public static void threeWordString(String[] words, Map<String, Word> countMap) {
	   for (int i=0; i<words.length-2; i++) {
		   if (words[i].compareTo("")!=0 && words[i+1].compareTo("")!=0 && words[i+2].compareTo("")!=0) {
			   String word=(words[i] + " " + words[i+1] + " " + words[i+2]);
			   //word=word.toLowerCase();
			   if (!line.contains(word) || word.length() < 9) {
				   continue;
			   }

			   Word wordObj = countMap.get(word);
			   if (wordObj == null) {
				   wordObj = new Word();
				   wordObj.word = word;
				   wordObj.count = 0;
				   countMap.put(word, wordObj);
			   }
			   wordObj.count++;
		   }
	   }
   }
   

	public List<String> getLinks()
	{
		return this.links;
	}

}
	class Word implements Comparable<Word> {
		String word;
		int count;

		@Override
		public int hashCode() { return word.hashCode(); }

		@Override
		public boolean equals(Object obj) { return word.equals(((Word)obj).word); }

		@Override
		public int compareTo(Word b) { return b.count - count; }
	}
