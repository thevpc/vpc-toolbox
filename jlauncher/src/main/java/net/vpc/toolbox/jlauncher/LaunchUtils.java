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

import java.io.*;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 28 mai 2003
 * Time: 18:39:07
 * To change this template use Options | File Templates.
 */
public class LaunchUtils {

    private static ResourceBundle bundle = ResourceBundle.getBundle("net.vpc.toolbox.jlauncher.JLauncherBundle");

    public static ResourceBundle getResourceBundle() {
        return bundle;
    }

    public static String getString(String key) {
        return bundle.getString(key);
    }

    public static String getString(String key, Object[] values) {
        return MessageFormat.format(bundle.getString(key), values);
    }

    public static String[] split(String string, char separator, boolean first) {
        if (string == null || string.length() == 0) {
            return null;
        }
        int i = first ? string.indexOf(separator) : string.lastIndexOf(separator);
        if (i < 0) {
            return null;
        } else {
            String[] r = new String[2];
            r[0] = substring(string, 0, i);
            r[1] = substring(string, i + 1);
            return r;
        }
    }

    public static String substring(String string, int start) {
        if (string == null) {
            return "";
        }
        else {
            return substring(string, start, string.length());
        }
    }

    public static Object getObjectFromSerializedForm(byte[] bytes) throws ClassNotFoundException {
        ObjectInputStream b = null;
        try {
            b = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return b.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        } finally {
            try {
                if (b != null) {
                    b.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static byte[] getSerializedFormOf(Object object) {
        ObjectOutputStream b = null;
        try {
            ByteArrayOutputStream bb = new ByteArrayOutputStream();
            b = new ObjectOutputStream(bb);
            b.writeObject(object);
            return bb.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        } finally {
            try {
                if (b != null) {
                    b.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static String[] addArgument(String value, String[] arguments) {
        return insertArgument(value, arguments, Array.getLength(arguments));
    }

    public static String[] removeArgument(String[] arguments, int index) {
        return removeArguments(arguments, index, 1);
    }

    public static String[] removeArguments(String[] arguments, int index, int count) {
        if (count == 0) {
            return arguments;
        }
        int max = Array.getLength(arguments);
        if (index + count > max) {
            throw new ArrayIndexOutOfBoundsException(index + " > " + (max - count));
        } else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int j = max - index - count;
        Object destArray = Array.newInstance(arguments.getClass().getComponentType(), max - count);
        System.arraycopy(arguments, 0, destArray, 0, index);
        if (j > 0) {
            System.arraycopy(arguments, index + count, destArray, index, j);
        }
        return (String[]) destArray;

    }

    public static String[] insertArgument(String value, String[] arguments, int index) {
        int max = Array.getLength(arguments);
        if (index >= max + 1) {
            throw new ArrayIndexOutOfBoundsException(index + " > " + max);
        }
        Object destArray = Array.newInstance(arguments.getClass().getComponentType(), max + 1);
        System.arraycopy(arguments, 0, destArray, 0, index);
        System.arraycopy(arguments, index, destArray, index + 1, max - index);
        Array.set(destArray, index, value);
        return (String[]) destArray;
    }

    // copied from Utils
    public static ArrayList lineWrapToStringVector(String msg, int max, boolean hard, ArrayList initialVector) {
        ArrayList v = initialVector == null ? new ArrayList() : initialVector;
        if (msg == null) {
            v.add("");
        } else {
            for (StringTokenizer st = new StringTokenizer(msg, "\n\r"); st.hasMoreTokens();) {
                String s = st.nextToken();
                int pos = 0;
                for (int len = s.length(); pos < len;) {
                    if (len - pos < max) {
                        v.add(s.substring(pos));
                        break;
                    }
                    int m = Math.min(max, len - pos - 1);
                    int a = m;
                    if (" ,\t;.'\":\t!".indexOf(s.charAt(pos + a)) >= 0) {
                        if (hard)
                            for (; " ,\t;.'\":\t!".indexOf(s.charAt(pos + a)) >= 0 && a <= max; a++) ;
                        else
                            for (; " ,\t;.'\":\t!".indexOf(s.charAt(pos + a)) >= 0; a++) ;
                        v.add(s.substring(pos, pos + a));
                        pos += a;
                    } else {
                        for (; a > pos && " ,\t;.'\":\t!".indexOf(s.charAt(a + pos)) < 0; a--) ;
                        if (a == 0)
                            a = m;
                        v.add(s.substring(pos, pos + a + 1));
                        pos += a + 1;
                    }
                }

            }

        }
        return v;
    }

    // copied from Utils
    public static String replaceString(String string, String oldPortion, String newPotion) {
        String x = "";
        for (int i = 0; i < string.length();) {
            String souschaine = substring(string, i, i + oldPortion.length());
            if (oldPortion.equals(souschaine)) {
                x = x + newPotion;
                i += oldPortion.length();
            } else {
                x = x + string.charAt(i);
                i++;
            }
        }

        return x;
    }

    // copied from Utils
    public static String substring(String string, int start, int end) {
        if (string == null || string.length() == 0)
            return "";
        if (start < 0)
            start = 0;
        if (end > string.length())
            end = string.length();
        if (end <= start)
            return "";
        else
            return string.substring(start, end);
    }

    /**
     * copied from
     * Swaps x[a] with x[b].
     */
    public static void swap(Object[] x, int a, int b) {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static void sort(File[] a) {
        File[] aux = (File[]) a.clone();
        mergeSort(aux, a, 0, a.length);
    }

    public static void mergeSort(File[] src, File[] dest, int low, int high) {
        int length = high - low;

        // Insertion sort on smallest arrays
        if (length < 7) {
            for (int i = low; i < high; i++)
                for (int j = i; j > low &&
                        //c.compare(dest[j-1], dest[j])>0
                        dest[j - 1].getName().compareTo(dest[j].getName()) > 0
                        ; j--)
                    swap(dest, j, j - 1);
            return;
        }

        // Recursively sort halves of dest into src
        int mid = (low + high) >> 1;
        mergeSort(dest, src, low, mid);
        mergeSort(dest, src, mid, high);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (
//                c.compare(src[mid-1], src[mid])
                src[mid - 1].getName().compareTo(src[mid].getName())
                <= 0) {
            System.arraycopy(src, low, dest, low, length);
            return;
        }

        // Merge sorted halves (now in src) into dest
        for (int i = low, p = low, q = mid; i < high; i++) {
            if (q >= high || p < mid &&
//                    c.compare(src[p], src[q])
                    src[p].getName().compareTo(src[q].getName())
                    <= 0)
                dest[i] = src[p++];
            else
                dest[i] = src[q++];
        }
    }

    public static String[] getSystemClassPathForClass(String className){
        String[] cp=System.getProperty("java.class.path").split(System.getProperty("path.separator"));
        ArrayList okUrl=new ArrayList();
        for (int i = 0; i < cp.length; i++) {
            try {
                class MyURLClassLoader extends URLClassLoader{
                    public MyURLClassLoader(String path) throws MalformedURLException {
                        super(new URL[]{new URL("file://"+path.replace('\\','/'))});
                    }
                     public Class findClass(String name) throws ClassNotFoundException {
                        return  super.findClass(name);
                     }
                };
                MyURLClassLoader urlClassLoader=new MyURLClassLoader(cp[i]);
                try {
                    urlClassLoader.findClass(className);
                    okUrl.add(cp[i]);
                } catch (ClassNotFoundException e) {
                }
            } catch (MalformedURLException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return (String[]) okUrl.toArray(new String[okUrl.size()]);
    }
    
}
