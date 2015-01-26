/*
 * Different dense points from different slices of splitfactor might have appended sequentially in a given file.
* This program put all dense points of the same subspace together by sifting through all csv files
 */

package subscaleplus;
import java.io.*;
import java.util.*;
import java.lang.Math;
import java.util.Map.Entry;

/**
 * @author akaur
 */
public class MergeDensePoints {
public static String folderstr="clusters"+File.separator;  
public static String fname;
    public static void main(String[] args) throws Exception {        
        File folder=new File(folderstr);
	File listOfFiles[]=folder.listFiles(new FilenameFilter() { public boolean accept(File folder, String name) { return name.toLowerCase().endsWith(".csv");}});	
	for(File f : listOfFiles) {
                fname=f.getName();
		writeDensePoints2(refineSubspaces2());  
	}	
    }
   
   
   
    private static Map <Subspace,Set<Integer>> refineSubspaces2() throws Exception {  
	Map <Subspace,Set<Integer>> SSMap=new HashMap<>();
	
        try(BufferedReader in = new BufferedReader(new FileReader(folderstr+fname))) {
            String keyValue;
            String [] key,value;
            String [] kvarr=new String[2];            
            
            while((keyValue=in.readLine())!=null) { 
                
		
                keyValue=keyValue.substring(1, keyValue.length()-1);
                kvarr=keyValue.split("\\]-\\[");
                value=kvarr[1].split("\\s*,\\s*");		                
		Set <Integer> pts=new TreeSet<>();
		
                for(String d:value) {
                    pts.add(Integer.parseInt(d));
                }
		key=kvarr[0].split("\\s*,\\s*");
                ArrayList <Integer> dimset=new ArrayList<Integer>();
                for(String d:key) {
                    dimset.add(Integer.parseInt(d));
                }
		Subspace ss=new Subspace(dimset);
		if(SSMap.containsKey(ss)) {                
                	SSMap.get(ss).addAll(pts);
            	}
            	else {
               		Set <Integer> points=new TreeSet<>(pts);               
	                SSMap.put(ss,pts);             
            	}	     
                
            }   
        }
	
	return SSMap;
    }
    
    private static void writeDensePoints2(Map <Subspace,Set<Integer>> SSMap) throws IOException  {    
         try(PrintWriter out = new PrintWriter(new FileWriter(folderstr+fname)))  {             
            for(Map.Entry<Subspace,Set<Integer>> entry : SSMap.entrySet()) {
                out.print(entry.getKey().getSubspace()); 
                out.print("-");
                out.print(entry.getValue());
                out.println();
            }
	    out.flush();
         }
         catch(IOException ioe) {
             //ioe.printStackTrace();
        }
	//Iterator mapIt = SSMap.keySet().iterator();
	//while ( mapIt.hasNext()) {     
	//	 mapIt.next();
	  //       mapIt.remove();
        //}       
	SSMap=null;
   }
     
}
