/*
 * This class defines an entry for the Collision data structure (Hashtable) used in the SubscalePlus.java
 */

package subscaleplus;
import java.io.Serializable;
import java.util.*;

public class CollisionMapEntry2 implements Serializable{
    private ArrayList <Integer> dimset;
    private ArrayList <Integer> points;
    
    public CollisionMapEntry2(int dim,ArrayList <Integer> pt)
    {
        points=pt;
        dimset=new ArrayList<Integer>();
        dimset.add(dim);
    }
     
    public void addDim(int dimnext)
    {
        dimset.add(dimnext);      
    }
    
    /**
     * Get the size of maximal subspace
     * @return
     */
    public int getNumDim()
    {
        return dimset.size();
    }
    
    /**
     * Get maximal subspace
     * @return
     */
    public ArrayList<Integer> getDimSet() 
    {
        return dimset;
    }
    
    /**
     * If the set of dense points are found in the same single dimensional space again. (Checks if the Entry has been made already or not )
     * @param dim
     * @return
     */
    public boolean exists(int dim)
    {
        return dimset.contains(dim);            
    }
        
    public ArrayList <Integer> getPoints()
     {
        return points;         
     }    

    
}
