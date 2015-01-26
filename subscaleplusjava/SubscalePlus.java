package subscaleplus;

import java.util.*;
import java.io.*;
import java.util.Map.*;

/**
 * @author akaur
 */
public class SubscalePlus {

    /**
     * @param args the command line arguments
     */
    private static int globaldim,TAU;
    private static long LOW,HIGH;
    private static final String chunkstr="epChunk.dat";
    private static HashMap <Long,Integer> longtoint;
    private static HashMap <Integer,Long> inttolong;
    
    public static void main(String[] args) throws Exception{
        int SLICENUM=Integer.valueOf(args[0]);
        TAU=Integer.parseInt(args[1]);
        long SLICE=0L;        
       ArrayList<ArrayList<ArrayList<Integer>>> epChunk=new ArrayList<ArrayList<ArrayList<Integer>>>();
        try(ObjectInputStream input=new ObjectInputStream(new FileInputStream(new File(chunkstr)))) {   
            while(true) {
                inttolong=(HashMap<Integer,Long>)input.readObject();
                longtoint=(HashMap<Long,Integer>)input.readObject();
                SLICE=input.readLong();
                LOW=input.readLong()+SLICE*SLICENUM; 
                epChunk=(ArrayList<ArrayList<ArrayList<Integer>>>)input.readObject(); 
            }
        }
        catch(EOFException e) {}   
        catch(IOException ioe) {} 
        HIGH=LOW+SLICE;  
        collisions(epChunk);
    }
    
   
     /*
     * Find dense units in the maximal subspaces
     */   
     private static void collisions(ArrayList<ArrayList<ArrayList<Integer>>> epChunk) throws IOException{
            Map <Long,CollisionMapEntry2> collisionMap=new HashMap<Long,CollisionMapEntry2>();
            globaldim=0;     
            int lastPoint,pivot;
            for(ArrayList<ArrayList<Integer>> dimChunk : epChunk) {  
                lastPoint=-1;   
                for(List<Integer> chunk : dimChunk) { 
                    
                    pivot=chunk.indexOf(lastPoint); 
                    lastPoint=chunk.get(chunk.size()-1);
                    if( pivot < TAU) {
                        //the combinitions need to be generated again
                        getSignatures(collisionMap,chunk,TAU+1); 
                    }
                    else {
                        joinSignatures(collisionMap,chunk,pivot);
                    } 
                }  
                globaldim++;
            } 
            for (Iterator<Entry<Long, CollisionMapEntry2>> it = collisionMap.entrySet().iterator(); it.hasNext();) {
               Map.Entry<Long,CollisionMapEntry2> entry = it.next();
               if(entry.getValue().getNumDim()==1) {
                  it.remove(); 
               }
            }             
            if(collisionMap.size()>0) {
                refineSubspaces(collisionMap);                
            }
    }
     
          
    private static void getSignatures(Map <Long,CollisionMapEntry2> collisionMap,List<Integer> chunk,int numcombi) {
        int chunkSize=chunk.size();
        int count,point;           
        if( chunkSize==0){
            return;
        }
        else if (chunkSize==numcombi) {
            long sum=0L;
            for(int pt : chunk) {
                sum+=inttolong.get(pt); 
            }
            if( (LOW<=sum) && (sum < HIGH) ) {
                if (collisionMap.containsKey(sum)) {                       
                    collisionMap.get(sum).addDim(globaldim);
                }
                else {
                    collisionMap.put(sum, new CollisionMapEntry2(globaldim, new ArrayList <Integer> (chunk)));
                } 
            }
            return;
        }
       ArrayList <Integer> seedcombi=new ArrayList<Integer>(numcombi);
       ArrayList <Integer> tempcombi=new ArrayList<Integer>(numcombi);
       ArrayList <Long> sums=new ArrayList<Long>(numcombi+1);
       ArrayList<Integer> ptarray=new ArrayList<Integer>(numcombi);
       ArrayList<Long> keyChunk=new ArrayList<Long>(chunkSize);
        for(int pt :chunk) {
            keyChunk.add(inttolong.get(pt));
        }
        Collections.sort(keyChunk); 
        for(count=0;count<numcombi;count++) {
            seedcombi.add(chunkSize-numcombi+count);
            tempcombi.add(0);
            sums.add(0L);
            ptarray.add(0);
        }
        seedcombi.set(0,-1);
        sums.add(0L);        
        int i,j,temp;
        long tempsum,keyPoint;
        Boolean flag=false;   
        while(true) {
            i=numcombi-1;            
            while( (i>=0) && (seedcombi.get(i) == (chunkSize-numcombi+i)) ){
                i--;
            }            
            if(i==-1) {                
                break;
            }
            else {
                for(j=i;j<numcombi;j++) {  
                    temp=seedcombi.get(i)+1+j-i;
                    tempcombi.set(j, temp); 
                    keyPoint=keyChunk.get(temp);
                    tempsum=sums.get(j)+keyPoint;                    
                    if (tempsum >= HIGH) {
                        flag=true;
                        while(true) {
                               if( (j<2) ||(tempcombi.get(j)-tempcombi.get(j-1)>1))
                                   break;                    
                                j--;
                        }
                        while(j<numcombi){  
                            tempcombi.set(j,chunkSize-numcombi+j );
                            j++;
                        }
                        break;
                    }                     
                    sums.set(j+1,tempsum);
                    point=longtoint.get(keyPoint);
                    ptarray.set(j, point);  
                }                
                Collections.copy(seedcombi,tempcombi); 
                if(flag==true) {
                    flag=false;
                    continue;
                }  
                if(LOW<=sums.get(numcombi)) {
                    if (collisionMap.containsKey(sums.get(numcombi))) {
                            collisionMap.get(sums.get(numcombi)).addDim(globaldim);
                    }
                    else {
                        collisionMap.put(sums.get(numcombi), new CollisionMapEntry2(globaldim, new ArrayList <Integer> (ptarray))); 
                    }                    
                }                
            }
        }  
    }
      
    private static void getPartialSignatures(ArrayList <Signature> listOfSignatures,List<Integer> chunk,int numcombi) {        
        int chunkSize=chunk.size();
        if( chunkSize==0){
            return;
        }                 
        if (chunkSize==numcombi) {
            long sum=0L;
           ArrayList<Integer> ptarray=new ArrayList<Integer>(numcombi);
            for(int pt : chunk) {
                sum+=inttolong.get(pt);
                ptarray.add(pt);
            }
            if( sum < HIGH) {
                listOfSignatures.add(new Signature(sum,ptarray));                
            }
            return;
        }
       ArrayList<Long> keyChunk=new ArrayList<Long>(chunkSize);
        for(int pt :chunk) {
            keyChunk.add(inttolong.get(pt));
        }
        Collections.sort(keyChunk);
       ArrayList <Integer> seedcombi=new ArrayList<Integer>(numcombi);
       ArrayList <Integer> tempcombi=new ArrayList<Integer>(numcombi);
       ArrayList <Long> sums=new ArrayList<Long>(numcombi+1);
       ArrayList<Integer> ptarray=new ArrayList<Integer>(numcombi); 
        int count,point; 
        for(count=0;count<numcombi;count++) {
            seedcombi.add(chunkSize-numcombi+count);
            tempcombi.add(0);
            sums.add(0L);
            ptarray.add(0);
        }
        seedcombi.set(0,-1);
        sums.add(0L);        
        int i,j,temp,r=numcombi;        
        long tempsum;
        Boolean flag=false;
        long keyPoint;
        while(true) {
            i=numcombi-1;
            while( (i>=0) && (seedcombi.get(i) == (chunkSize-numcombi+i)) ){
                i--;
            }            
            if(i==-1) {                
                break;
            }
            else {
                for(j=i;j<numcombi;j++) {                    
                    temp=seedcombi.get(i)+1+j-i;
                    tempcombi.set(j, temp); 
                    keyPoint=keyChunk.get(temp);
                    tempsum=sums.get(j)+keyPoint;
                    if (tempsum >= HIGH) {
                        flag=true;
                        while(true) {
                           if( (j<2) ||(tempcombi.get(j)-tempcombi.get(j-1)>1))
                               break;                    
                            j--;
                        }
                        while(j<numcombi){                            
                            tempcombi.set(j,chunkSize-numcombi+j );
                            j++;
                        }
                        break;
                    }                     
                    sums.set(j+1,tempsum);
                    point=longtoint.get(keyPoint);
                    ptarray.set(j, point);  
                }
                Collections.copy(seedcombi,tempcombi); 
                if(flag==true) {
                    flag=false;
                    continue;
                }            
                listOfSignatures.add(new Signature(sums.get(numcombi),new ArrayList <Integer> (ptarray))); 
            }
        }
    }
                
    private static void joinSignatures(Map <Long,CollisionMapEntry2> collisionMap,List<Integer> chunk,int pivot)    {      
       ArrayList <Signature> listOfSignatures=new ArrayList<Signature>();
       List<Integer> leftChunk=chunk.subList(0, pivot+1);    
       List<Integer> rightChunk=chunk.subList(pivot+1,chunk.size());  
        int numCombi,rightCombi=1;
        if(rightChunk.size()>TAU) {
            getSignatures(collisionMap,rightChunk,TAU+1);
            numCombi=TAU;    
        }
        else {
            numCombi=rightChunk.size();                               
        } 
        long tempsum;
        do {  
           ArrayList <Signature> leftSignatures=new ArrayList<Signature>();     
           ArrayList <Signature> rightSignatures=new ArrayList<Signature>(); 
            getPartialSignatures(leftSignatures,leftChunk,TAU+1-rightCombi);           
            getPartialSignatures(rightSignatures,rightChunk,rightCombi);          
           for(Signature spR : rightSignatures) {
               for(Signature spL : leftSignatures) {           
                    tempsum=spR.getSum();              
                    ArrayList<Integer> ptarray=new ArrayList<Integer>(spR.getPoints());
                     ptarray.addAll(spL.getPoints()); 
                     tempsum+=spL.getSum();
                     if( (tempsum < HIGH) &&(LOW <= tempsum )) {
                        if (collisionMap.containsKey(tempsum)) {                       
                            collisionMap.get(tempsum).addDim(globaldim);
                        }
                         else {
                            collisionMap.put(tempsum, new CollisionMapEntry2(globaldim, new ArrayList <Integer> (ptarray)));  
                        }   
                    }                      
                }
            }
            rightCombi++;       
        }
        while(rightCombi<=numCombi);    
        listOfSignatures.clear();
    }
    
     /*
     * Sift through the collisionMap to collect the dense points in each maximal subspace     * 
     */
    private static void refineSubspaces(Map <Long,CollisionMapEntry2> collisionMap) throws IOException {        
        Map <Subspace,Set<Integer>> SSMap=new TreeMap<>();    
        for(Map.Entry<Long,CollisionMapEntry2> entry : collisionMap.entrySet())
        {                     
            CollisionMapEntry2 me=entry.getValue();
            Subspace dimset=new Subspace(me.getDimSet());            
            if(SSMap.containsKey(dimset)) {                
                SSMap.get(dimset).addAll(me.getPoints());
            }
            else {
                Set <Integer> points=new TreeSet<Integer>(me.getPoints());               
                SSMap.put(dimset,points);              
            }               
        }  
        collisionMap.clear();
        collisionMap=null;
        writeDensePoints(SSMap);
    }
     
    /*
    * Append maximal dense points to the relevant subspaces' file
    */
    private static void writeDensePoints(Map <Subspace,Set<Integer>> SSMap) throws IOException{
        String outStr= "clusters"+File.separator; 
        ArrayList<Integer> ss;
        PrintWriter out;
        int subspaceFile;
        for(Map.Entry<Subspace,Set<Integer>> entry : SSMap.entrySet()) {
            ss=entry.getKey().getSubspace();
            subspaceFile=ss.size();             
            out = new PrintWriter(new FileWriter(outStr+subspaceFile+".csv",true));
            out.print(entry.getKey().getSubspace()); 
            out.print("-");
            out.print(entry.getValue());
            out.println(); 
            out.close();
        }   
    }
}
