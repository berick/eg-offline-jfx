package org.evergreen_ils.ui.offline;

import javafx.beans.NamedArg;

public class CircDuration {
    String value;
    String label;

    public CircDuration(                                                       
        @NamedArg("value") String value, @NamedArg("label") String label) {    
                                                                               
        this.value = value;                                                    
        this.label = label;                                                    
    }  
    
    @Override
    public String toString() {
        return label;
    }
}
