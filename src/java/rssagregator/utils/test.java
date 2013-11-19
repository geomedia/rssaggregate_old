/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 *
 * @author clem
 */
public class test {
    
    public static void main(String[] args) {
        try {
            
           
            
            VelocityEngine ve = new VelocityEngine();
            ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            ve.init();
            
            
            
//            ve.init(p);
            
            Template t = ve.getTemplate("rssagregator/utils/helloworld.vm");
           
            VelocityContext context = new VelocityContext();
            context.put("name", "World");
            StringWriter writer = new StringWriter();
            t.merge(context, writer);
                System.out.println(writer.toString());
        } catch (Exception ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}
