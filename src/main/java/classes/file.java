package classes;

public class file {
    String title; 
    String type; 
    int fileID;
    
    public file (String ti, String ty, int id) { 
        title = ti;
        type = ty;
        fileID = id;
    }
    
    public String getTitle() { 
        return title;
    }
    
    public String getType() { 
        return type;
    }
    
    public int getID() { 
        return fileID;
    }
} 
