package io.github.ucd.hornet.connector.enums;

/**
 *
 * @author Uellington Damasceno
 */
public enum FlowDirection {
    UP_DOWN("UD_"),
    DOWN_UP("DU_");
    
    private final String prefix;
    
    private FlowDirection(String prefix){
        this.prefix = prefix;
    }
    
    public String prefix(){
        return this.prefix;
    }
}
