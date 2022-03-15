package org.evergreen_ils.ui.offline;

public class NonCatType {

    Integer id;
    String label;

    public NonCatType(Integer id, String label) {
        this.id = id;                                                    
        this.label = label;                                                    
    }  
    
    @Override
    public String toString() {
        return label;
    }
}
