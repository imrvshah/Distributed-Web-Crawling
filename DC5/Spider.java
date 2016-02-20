import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Spider
{
	private static final int MAX_PAGES_TO_SEARCH = Integer.MAX_VALUE;
	Map<String, String> visited = new HashMap<String, String>();
	public Map<String, StringBuilder> searchResult = new HashMap<String, StringBuilder>();
	LinkedList<String> pageToVisit = new LinkedList<String>();
	String dir = "";
	public void traverse(String url1) 
	{

		String[] urlname = url1.split("//", 2);
		dir=urlname[1];
		pageToVisit.add(url1);
		while(pageToVisit.size()!=0 && visited.size() < MAX_PAGES_TO_SEARCH)
		{
			try {
			String url = pageToVisit.remove();
			SpiderLeg leg = new SpiderLeg();
			if(!visited.containsKey(url))
			{
				String currentUrl = url.replace("/", "_");
				currentUrl = currentUrl.replace(":", "_");
				currentUrl = currentUrl.replace("?", "_");
				currentUrl = currentUrl.replace("=", "_");
				visited.put(url, currentUrl);
				leg.crawl(url); // Function Call to Crawl
				leg.count();
				for (Map.Entry<String, Word> entry: leg.countMap.entrySet()) {
					StringBuilder sb = new StringBuilder(url);
					sb.append("|"+entry.getValue().count+";");
					if (searchResult.containsKey(entry.getKey())) {
						StringBuilder sb1 = searchResult.get(entry.getKey());
						sb1.append(sb);
						searchResult.put(entry.getKey(), sb1);
						//System.out.println(entry.getKey());
						//System.out.println(sb1);

					}
					else {
						searchResult.put(entry.getKey(), sb);
					}
				}
				dataStore(url, leg.str);
				pageToVisit.addAll(leg.getLinks());
			}
			}
			catch (IOException e) {System.out.println("IOException!!!");}
			catch (IllegalArgumentException e) {System.out.println("IllegalArgumentException!!!");}
			catch (NullPointerException e) {System.out.println("NullPointerException!!!");}
		}
	}

	public String[] search (String query) {
		StringBuilder sb1 = new StringBuilder(); 
		if (searchResult.containsKey(query)) {
			sb1 = searchResult.get(query);
			
/*
			String str = sb1.toString();
			String []strArray = str.split(";");
			Word [] words = new Word[strArray.length];
			for (int i=0; i<strArray.length; i++) {
				String stri[] = strArray[i].split("\\|");
				words[i] = new Word();
				words[i].word = stri[0];
				words[i].count = Integer.parseInt(stri[1]);
			}
			Merge_Sort ms = new Merge_Sort();
			ms.mergeSort(words, 0, strArray.length-1);
			sb1=new StringBuilder();
			System.out.println("Priority wise sorted list in descending order is :- ");
			for (int i=strArray.length-1; i>=0; i--) {
				System.out.println("Link = "+words[i].word+" with count = "+words[i].count);
				sb1.append(words[i].word+"|"+words[i].count+";");
			}
//*/
		}
		else {
			//System.out.println(query+" not found!!!");
			return null;
		}
		return sb1.toString().split(";");
	}

	public void dataStore(String url, String text) throws IOException{
		
			boolean success = false; 
			File directory = new File(dir);
			if(directory.exists())
				{ 
				//System.out.println("Directory already exists ..."); 
			} else {
				//System.out.println("Directory not exists, creating now");
				success = directory.mkdir();
			if (success){ 
				//System.out.printf("Successfully created new directory : %s%n", dir);
			} else { 
				//System.out.printf("Failed to create new directory: %s%n", dir); 
			}
			} 
			String file = url.replace("/", "_");
			file = file.replace(":", "_");
			file = file.replace("?", "_");
			file = file.replace("=", "_");
			String filename = file+".txt";
			String workingDir = System.getProperty("user.dir");
			workingDir = workingDir + File.separator+ dir+ File.separator+filename;
			//System.out.println(workingDir);
			File f = new File(workingDir); 
			if (f.exists()) 
			{ 
				//System.out.println("File already exists");
				 BufferedWriter output = null;
					output = new BufferedWriter(new FileWriter(f));
		            output.write(text);
			} else{ 
				//System.out.println("No such file exists, creating now");
				success = f.createNewFile(); 
				if(success) { 
					//System.out.printf("Successfully created new file: %s%n", f);
					 BufferedWriter output = null;
			        try {
			            
			            output = new BufferedWriter(new FileWriter(f));
			            String str = url + "\n";
			            output.write(str);
			            output.write(text);
			        }
			        catch (IOException ex) {
			        	  // report
			        	} finally {
			        		 if ( output != null ) output.close();
			        	}
			} else{ 
				//System.out.printf("Failed to create new file: %s%n", f); 
			} 
			} 
		
	}
}
