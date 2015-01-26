/*
 * Program to find the epsilon neighbourgoods in all one dimensional spaces.
 */

package subscaleplus;
import java.io.*;
import java.util.*;

/**
 * @author akaur
 */

public class DenseNeighbours {

    /**
     * @param args the command line arguments
     */
    private static double EPSILON;   
    private static int SIZE,DIM,totalCombi,SPFACTOR,TAU,N;
    private static long runTime,LOW,HIGH,SLICE;
    private static String dataFile;
    private static final Map <Long,Integer> longtoint=new HashMap<>();
    private static final Map <Integer,Long> inttolong=new HashMap<>();
    
    public static void main(String[] args) throws Exception {
        /****************** SET PARAMETERS **********************/
        dataFile=args[0];
        EPSILON=Double.parseDouble(args[1]);
        TAU=Integer.parseInt(args[2]);
        SPFACTOR=Integer.parseInt(args[3]);
        /*******************************************************/
        
        List<List<Double>> dataMatrix; // Input data matrix of points (SIZE X DIM)
        
        //Reading input file into dataMatrix
        int pointID;                 
        try (Scanner input = new Scanner(new File(dataFile))) {   
            dataMatrix=new ArrayList<List<Double>>();
            Random keyGenerator = new Random();           
            pointID=0;   
            input.nextLine();            
            while(input.hasNextLine()) {
                List <Double> pointVector=new ArrayList<Double>();
                long rand=(long)(100000000000000L+keyGenerator.nextDouble()*100000000000000L);  
                //14-digit random numbers
                inttolong.put(pointID,rand);
                longtoint.put(rand,pointID); 
                input.useDelimiter(",") ;                                                
                while(input.hasNextDouble()) {
                    pointVector.add(input.nextDouble());   
                }
                pointVector.add(Double.parseDouble(input.nextLine().replace(",","")));
                dataMatrix.add(pointVector);  
                pointID++;
            } 
            SIZE=dataMatrix.size();
            DIM=dataMatrix.get(0).size();
            input.close();
        }
        long startTime=System.currentTimeMillis();
        TreeSet <Long> ItoL=new TreeSet <Long>(longtoint.keySet());
        long min=0L,max=0L;
        for(int i=0;i<=TAU;i++) {
            min+=ItoL.pollFirst();
            max+=ItoL.pollLast();
        }
        PointIDValue[] dimVector=new PointIDValue [SIZE];   //1-dimensional points projected on dimension 'dim', where 0<=dim<DIM
        List<List<List<Integer>>> epChunk=new ArrayList<List<List<Integer>>>();    //Data structure to store epsilon chunks (core-sets) created in all single dimensions
        
        //Sort point values in each dimension and then create epsilon chunks.
        for(int dim=0;dim<DIM;dim++) {
            for(pointID=0;pointID<SIZE;pointID++) {
               dimVector[pointID]=new PointIDValue(pointID,dataMatrix.get(pointID).get(dim));              
            }             
            sort(dimVector);    
            epChunk.add(createDensityChunks(dimVector));            
        }  

        SLICE=Math.floorDiv(max-min,(long)SPFACTOR);
        LOW=min;
        writeChunkToFile(epChunk);
        
       /* File outputFile = new File("collisionMap.dat");
        if (outputFile.exists()) {
           if (!outputFile.delete()) {
               System.err.println("Failed to delete \""+outputFile.getName()+'"');
           };
        }*/        
    }
    private static void writeChunkToFile(List<List<List<Integer>>> epChunk) throws IOException {
        File file_out= new File("epChunk.dat");
        try(ObjectOutputStream output=new ObjectOutputStream(new FileOutputStream(file_out,false))) {
            output.writeObject(inttolong);
            output.writeObject(longtoint);            
            output.writeLong(SLICE);
            output.writeLong(LOW);
            output.writeObject(epChunk);           
            output.close();
        }       
    }
   
    private static void writeParametersTofile() throws IOException  {    
         try(PrintWriter out = new PrintWriter(new FileWriter("parameters.csv",false)))  {  
            out.print(dataFile);
            out.print("-");
            out.print(SIZE);
            out.print("-");
            out.print(DIM);
            out.print("-");
            out.printf("%f",EPSILON);
            out.print("-");
            out.print(TAU);
            out.print("-");
            out.print(SPFACTOR);
            out.println();            
            out.flush();
         }
         catch(IOException ioe) {
        }
   }
     
    private static List<List<Integer>> createDensityChunks(PointIDValue [] arr) {       
           List <List<Integer>> chunkList=new ArrayList<>();  
           int lastPoint=-1;           
           int next=0;       
           ArrayList <Integer> chunk=new ArrayList<>(); 
           for(int ptr=0;ptr<SIZE;ptr++) {
               while ((next<SIZE) && ( arr[next].getValue()- arr[ptr].getValue())<EPSILON) {
                   chunk.add(arr [next].getNum()); 
                   next++;               
                }
                if(chunk.isEmpty()) {
                    continue;
                }
                int newLastPoint=chunk.get(chunk.size()-1);            
                if(newLastPoint!=lastPoint) {                              
                    lastPoint=newLastPoint;
                    if(chunk.size()>TAU) {//atleast TAU neighbours, THIS point has already been added to chunk and does not add to count
                        chunk.trimToSize();                        
                        chunkList.add(new ArrayList<Integer>(chunk));
                    }
                }
                chunk.remove(0);//to move to the chunklist starting with next element
            }  
           ((ArrayList)chunkList).trimToSize();
       return chunkList;    
    }
    
    
    
        /**
     * Take a two-column array as input. ANd sort on the second column i.e. values
     * @param dimVector
     * @throws IOException
     */       
    private static void sort(PointIDValue [] dimVector) {
        sort(dimVector,0,dimVector.length-1);
    }
          
    private static void sort(PointIDValue [] dimVector, int first, int last) {
        if (last > first) {
            int pivotIndex = partition(dimVector, first, last);
            sort(dimVector, first, pivotIndex - 1);
            sort(dimVector, pivotIndex + 1, last);
        }
    }

  /** Partition the array list[first..last] */
  private static int partition(PointIDValue [] dimVector, int first, int last) {
    double pivot = dimVector[first].getValue(); // Choose the first element as the pivot
    int p=dimVector[first].getNum();
    int low = first + 1; // Index for forward search
    int high = last; // Index for backward search

    while (high > low) {
      // Search forward from left
      while (low <= high && dimVector[low].getValue() <= pivot) {
            low++;
        }

      // Search backward from right
      while (low <= high && dimVector[high].getValue() > pivot) {
            high--;
        }

      // Swap two elements in the list
      if (high > low) {
        double temp = dimVector[high].getValue();
        int t=dimVector[high].getNum();
        dimVector[high].setNumValue(dimVector[low].getNum(),dimVector[low].getValue());
        dimVector[low].setNumValue(t,temp);        
      }
    }

    while (high > first && dimVector[high].getValue() >= pivot) {
          high--;
      }

    // Swap pivot with list[high]
    if (pivot > dimVector[high].getValue()) {
      dimVector[first].setNumValue(dimVector[high].getNum(),dimVector[high].getValue());
      dimVector[high].setNumValue(p,pivot);
      return high;
    }
    else {
      return first;
    }
  }
}
