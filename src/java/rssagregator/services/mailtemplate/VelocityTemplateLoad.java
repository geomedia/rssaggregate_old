/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.mailtemplate;

import java.io.StringWriter;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.DateTool;

/**
 *
 * @author clem
 */
public class VelocityTemplateLoad {

    /**
     * *
     * Retourne une chaine de caractère mis en forme en utilisant Velocity
     *
     * @param template Le chemin du templace
     * @param context Le contexte a fournir à velocity
     * @return
     * @throws Exception
     */
    public static String rendu(String template, VelocityContext context) throws Exception {


        org.apache.velocity.tools.generic.DateTool datetoo = new DateTool();
                context.put("dateTool", datetoo); // Un outil pour formatter des date




        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());




        ve.init();
        Template t = ve.getTemplate(template, "UTF-8");
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();

    }
}
