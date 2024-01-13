
package classes;

public class section {
    String title; 
    String notes; 
    int sectionID; 
    public section(String t, String n, int s) { 
        title = t;
        sectionID = s; 
        notes = n;
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public String getNotes() { 
        return notes; 
    }
    
    public int getID() { 
        return sectionID; 
    }
}
