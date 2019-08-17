/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.jperf;

import java.text.DecimalFormat;

/**
 *
 * @author vpc
 */
public class Utils {
    public static String formatDataRate(long l){
        if(l>1024*1024*1024){
            return  new DecimalFormat("0.00").format((((double)l)/(1024*1024*1024)))+"Gb/s";
        }
        if(l>1024*1024){
            return  new DecimalFormat("0.00").format((((double)l)/(1024*1024)))+"Mb/s";
        }
        if(l>1024){
            return  new DecimalFormat("0.00").format((((double)l)/(1024)))+"Kb/s";
        }
        return l+"b/s";
    } 
    
    public static String formatTime(long l){
        return l+"ms";
    } 
}
