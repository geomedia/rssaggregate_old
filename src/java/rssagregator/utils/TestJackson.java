/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 *
 * @author clem
 */
@JsonFilter("myFilter")
public class TestJackson {
    
    String var1;
    String var2;

    public String getVar1() {
        return var1;
    }

    public void setVar1(String var1) {
        this.var1 = var1;
    }

    public String getVar2() {
        return var2;
    }

    public void setVar2(String var2) {
        this.var2 = var2;
    }
    
    
    
    
    public static void main(String[] args) throws JsonProcessingException {
        
        TestJackson obj = new TestJackson();
        obj.var1 = "AA";
        obj.var2 = "BB";
        
            ObjectMapper mapper = new ObjectMapper();
             FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter",
                        SimpleBeanPropertyFilter.filterOutAllExcept("var1"));
             
               String jsonn =  mapper.writer(filters).writeValueAsString(obj);
               System.out.println(""+jsonn);
    }
}
