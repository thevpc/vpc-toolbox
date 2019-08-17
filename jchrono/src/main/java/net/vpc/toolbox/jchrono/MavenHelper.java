/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.toolbox.jchrono;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author vpc
 */
public class MavenHelper {

    public static String getVersion(String groupIp, String artifctId, String defaultValue) {
        InputStream is = null;
        try {
            try {
                URL r = MavenHelper.class.getResource("/META-INF/maven/" + groupIp + "/" + artifctId + "/pom.properties");
                Properties p = new Properties();
                is = r.openStream();
                p.load(is);
                return p.getProperty("version",defaultValue);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return defaultValue;
    }
}
