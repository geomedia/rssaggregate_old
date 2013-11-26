/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Version;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
public class test {

    public static void main(String[] args) throws IOException {

        System.out.println("aa");

        Item it = new Item();
        String str = XMLTool.serialise(it);
        
        System.out.println(str);
        
        Object o = it.getClass().getAnnotation(Version.class);
        System.out.println(""+o);
    }
}
