/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.util;

import java.util.StringTokenizer;
import java.util.Map;

/**
 * This class provides static util methods for String manaipulation that 
 * aren't part of the default JDK functionalities.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: StringUtils.java,v 1.9 2004/02/23 03:23:42 jford Exp $
 */
public class StringUtils 
{
    /**
     * Replaces all the occurences of a substring found within a string by a 
     * replacement string
     *
     * @deprecated (Generates too much garbage)
     * @param original the string in where the replace will take place
     * @param find the substring to find and replace
     * @param replacement the replacement string for all occurences of find
     * @return the original string where all the occurences of find are replaced by
     *          replacement
     */
    public static String replaceAll(String original, String find, String replacement)
    {
        StringBuffer buffer = new StringBuffer(original);

        int idx = original.length();
        int offset = find.length();
        
        while( ( idx=original.lastIndexOf(find, idx-1) ) > -1 )
        {
            buffer.replace(idx,idx+offset, replacement);
        }

        return buffer.toString();
    }
    /**
     * Replaces all the occurences of a substring found 
     * within a StringBuffer by a 
     * replacement string
     *
     * @param buffer the StringBuffer in where the replace will take place
     * @param find the substring to find and replace
     * @param replacement the replacement string for all occurences of find
     * @return the original StringBuffer where all the
     * occurences of find are replaced by replacement
     */
    public static StringBuffer replaceAll(StringBuffer buffer, String find, String replacement)
    {

        int bufidx = buffer.length() - 1;
        int offset = find.length();
        while( bufidx > -1 ) { 
            int findidx = offset -1;
            while( findidx > -1 ) {
                if( bufidx == -1 ) {
                    //Done
                    return buffer;
                }
                if( buffer.charAt( bufidx ) == find.charAt( findidx ) ) {
                    findidx--; //Look for next char
                    bufidx--; 
                } else {
                    findidx = offset - 1; //Start looking again
                    bufidx--;
                    if( bufidx == -1 ) {
                        //Done
                        return buffer;
                    }
                    continue;
                }
            }
            //Found
            //System.out.println( "replacing from " + (bufidx + 1) +
            //                    " to " + (bufidx + 1 + offset ) +
            //                    " with '" + replacement + "'" );
            buffer.replace( bufidx+1, 
                            bufidx+1+offset, 
                            replacement);
            //start looking again
        }
        //No more matches
        return buffer;
            
    }

   /**
     *  Takes an array of tokens and converts into separator-separated string.
     *
     * @param String[] The array of strings input.
     * @param String The string separator.
     * @return String A string containing tokens separated by seperator.
     */
    public static final String arrayToString(String[] array, String separators)
    {
        StringBuffer sb = new StringBuffer("");
        String empty = "";
        
        if (array == null)
            return empty;

        if (separators == null)
            separators = ",";

        for (int ix=0; ix < array.length; ix++) 
        {
            if (array[ix] != null && !array[ix].equals("")) 
            {
                sb.append(array[ix] + separators);
            }
        }
        String str = sb.toString();
        if (!str.equals("")) 
        {
            str = str.substring(0, (str.length() - separators.length()));
        }
        return str;
    }

    /**
      *  Converts a delimited string into an array of string tokens.
      *                    
      * @param String[] The 'separator' separated string.
      * @param String The string separator.
      * @return String A string array of the original tokens.
      */
    public static final String[] stringToArray(String str, String separators)
    {
        StringTokenizer tokenizer;
        String[] array = null;
        int count = 0;

        if (str == null)
            return array;

        if (separators == null)
            separators = ",";

        tokenizer = new StringTokenizer(str, separators);
        if ((count = tokenizer.countTokens()) <= 0) {
            return array;
        }
        
        array = new String[count];
        
        int ix = 0;
        while (tokenizer.hasMoreTokens()) 
        {
            array[ix] = tokenizer.nextToken();
            ix++;
        }

        return array;
    }

    /**
     * Remove a given set of characters from a String.
     *
     * @param String The input string to be cleansed of 'removeChars'.
     * @param String The characters to be removed.
     * @return String The new string cleansed of 'removeChars'.
     */
    public static String removeChars (String data, String removeChars)
    {
        String temp = null;
        StringBuffer out = new StringBuffer();
        temp = data;

        StringTokenizer st = new StringTokenizer(temp, removeChars);
        while (st.hasMoreTokens())
        {
            String element = (String) st.nextElement();
            out.append(element);
        }
        return out.toString();
    }

    /* 
     * Given a filename, strips the .extension 
     *
     */
    public static String stripExtension(String filename)
    {
        int index = filename.lastIndexOf('.');
        if (index > -1)
        {
            return filename.substring(0, index);
        }
        return filename;
    }               

    /**
     * Performs variable substitution for a string. String is scanned for ${variable_name} and if one is found,
     * it is replaced with corresponding value from the vars hashtable.
     * 
     * @param origString unmodified string
     * @param vars Hashtable of replacement values
     * @return modified string
     * @exception Exception
     */
    public static String replaceVars(String origString, Map vars) 
    {

        StringBuffer finalString = new StringBuffer();
        int index = 0;
        int i = 0;
        String key = null;
        String value = null;
        while ((index = origString.indexOf("${", i)) > -1) 
        {
            key = origString.substring(index + 2, origString.indexOf("}", index+3));
            value = (String) vars.get(key);
            finalString.append(origString.substring(i, index));
            if (value != null) 
            {
                finalString.append(value);
            } 
            else 
            {
                finalString.append("${"+key+"}");
            }
            i = index + 3 + key.length();
        }
        finalString.append (origString.substring(i));

        return finalString.toString();
    }

}
