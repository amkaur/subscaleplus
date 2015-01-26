/*
 * The object of this class will be used to represent a subspace
 */

package subscaleplus;
import java.util.*;
/**
 *
 * @author akaur
 */
public class Subspace implements Comparable{
    private final ArrayList<Integer> ss;
    
    public Subspace(ArrayList <Integer> dims) {
        this.ss=dims;
    }
    
    public ArrayList<Integer> getSubspace() {
        return ss;
    }
    
    @Override
    public int hashCode() {
        return ss.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Subspace) && ((Subspace) obj).ss.equals(ss);
    }
    
    @Override
    public int compareTo(Object other) {
        if (!(other instanceof Subspace)) throw new IllegalArgumentException();
        else {
            ArrayList<Integer> thislist=this.ss;
            ArrayList<Integer> otherlist=((Subspace)other).ss;        
            if (thislist.size()< otherlist.size()) return -1;  
            if (thislist.size()> otherlist.size()) return 1;              
            for (int i = 0; i < thislist.size(); i ++) {                
                if (thislist.get(i) < otherlist.get(i)) return -1;
                if (thislist.get(i) > otherlist.get(i)) return 1;
            }
        }
        return 0;
    }
}
