/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

public class journal extends file { 
    int level;
    int parentID; 
    String storeType;
    
    public journal(String ti, String ty, int id, int l, int pID, String storeType) {
        super(ti, ty, id);
        level = l;
        parentID = pID; 
        this.storeType = storeType;
    }
    
    public int getLevel() { 
        return level;
    }
    
    public int getParentID() { 
        return parentID; 
    }
    
    public String getStore() { 
        return storeType; 
    }
}