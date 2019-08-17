/**
 * ====================================================================
 *             Universal Java VM Launcher
 *
 * Universal Java VM Launcher is a new tool for running other Java Virtual
 * machines.
 *
 * Copyright (C) 2002-2008 Taha BEN SALAH
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
package net.vpc.toolbox.jlauncher;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.IOException;
import java.io.OutputStream;

class WindowTextOutput extends OutputStream {
    private static JFrame singleFrame = null;
    private static boolean isSingleFrame = true;

    private JFrame frame = null;
    private JTextArea editorPane = new JTextArea("");
    private JScrollPane p = new JScrollPane(editorPane);

    public WindowTextOutput(String title) {
        if(isSingleFrame){
            if(singleFrame==null){
              singleFrame=new JFrame(JLauncher.APP_FULL_NAME);
                singleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                singleFrame.setSize(400, 400);
                singleFrame.getContentPane().add(new JTabbedPane());
            }
            frame=singleFrame;
        }else{
            frame.setTitle(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.getContentPane().add(new JTabbedPane());
        }
        JTabbedPane jTabbedPane=((JTabbedPane)(frame.getContentPane().getComponent(0)));
        jTabbedPane.addTab(title,p);
        jTabbedPane.setSelectedIndex(jTabbedPane.getTabCount()-1);

        frame.setVisible(true);
    }

    public void write(int b) throws IOException {
        Document d = editorPane.getDocument();
        try {
            d.insertString(d.getLength(), String.valueOf((char) b), null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] b) throws IOException {
        Document d = editorPane.getDocument();
        try {
            d.insertString(d.getLength(), new String(b), null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        Document d = editorPane.getDocument();
        try {
            d.insertString(d.getLength(), new String(b, off, len), null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
