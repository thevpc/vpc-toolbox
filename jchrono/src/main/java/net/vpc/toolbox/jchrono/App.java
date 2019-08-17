/**
 * ====================================================================
 *                        jchrono application
 *
 * Description: <start>A simple portable ui chronometer application<end>
 *
 * Copyright (C) 2012-2012 Taha BEN SALAH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * ====================================================================
 */
package net.vpc.toolbox.jchrono;

import javax.swing.JFrame;

/**
 * Application Main Entry Point
 * @author Taha Ben Salah
 */
public class App 
{
    public static String getVersion() {
        return MavenHelper.getVersion("net.vpc.app.jchrono", "jchrono", "(dev)");
    }
    
    public static void main( String[] args )
    {
//        "toto".
        JFrame f=new JFrame("JChrono "+getVersion());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new ChronoPanel());
        f.pack();
        f.setVisible(true);
    }
}
