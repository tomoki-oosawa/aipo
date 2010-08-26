/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

import org.apache.oro.text.perl.*;
//import java.util.regex.*;

import java.net.*;
import org.apache.commons.lang.StringUtils;

/**
 * <P>Data Validation functions using Regex</P>
 *
 * @author <a href="mailto:ben.woodward@bbc.co.uk">Ben Woodward</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ValidationHelper.java,v 1.4 2004/02/23 03:23:42 jford Exp $
 */
public class ValidationHelper
{
    //static string bigEmailRegex = new String("[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\"[^\\\\\\x80-\\xff\\n\\015\"]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015\"]*)*\")[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:\\.[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\"[^\\\\\\x80-\\xff\\n\\015\"]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015\"]*)*\")[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*)*@[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\\[(?:[^\\\\\\x80-\\xff\\n\\015\\[\\]]|\\\\[^\\x80-\\xff])*\\])[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:\\.[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\\[(?:[^\\\\\\x80-\\xff\\n\\015\\[\\]]|\\\\[^\\x80-\\xff])*\\])[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*)*|(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\"[^\\\\\\x80-\\xff\\n\\015\"]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015\"]*)*\")[^()<>@,;:\".\\\\\\[\\]\\x80-\\xff\\000-\\010\\012-\\037]*(?:(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)|\"[^\\\\\\x80-\\xff\\n\\015\"]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015\"]*)*\")[^()<>@,;:\".\\\\\\[\\]\\x80-\\xff\\000-\\010\\012-\\037]*)*<[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:@[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\\[(?:[^\\\\\\x80-\\xff\\n\\015\\[\\]]|\\\\[^\\x80-\\xff])*\\])[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:\\.[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\\[(?:[^\\\\\\x80-\\xff\\n\\015\\[\\]]|\\\\[^\\x80-\\xff])*\\])[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*)*(?:,[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*@[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\\[(?:[^\\\\\\x80-\\xff\\n\\015\\[\\]]|\\\\[^\\x80-\\xff])*\\])[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:\\.[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\\[(?:[^\\\\\\x80-\\xff\\n\\015\\[\\]]|\\\\[^\\x80-\\xff])*\\])[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*)*)*:[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*)?(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\"[^\\\\\\x80-\\xff\\n\\015\"]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015\"]*)*\")[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:\\.[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\"[^\\\\\\x80-\\xff\\n\\015\"]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015\"]*)*\")[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*)*@[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\\[(?:[^\\\\\\x80-\\xff\\n\\015\\[\\]]|\\\\[^\\x80-\\xff])*\\])[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:\\.[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\\)[\\040\\t]*)*(?:[^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff]+(?![^(\\040)<>@,;:\".\\\\\\[\\]\\000-\\037\\x80-\\xff])|\\[(?:[^\\\\\\x80-\\xff\\n\\015\\[\\]]|\\\\[^\\x80-\\xff])*\\])[\\040\\t]*(?:\\([^\\\\\\x80-\\xff\\n\\015()]*(?:(?:\\\\[^\\x80-\\xff]|\\([^\\\\\\x80-\\xff\\n\\015()]*(?:\\\\[^\\x80-\\xff][^\\\\\\x80-\\xff\\n\\015()]*)*\\))[^\\\\\\x80-\\xff\\n\\015()]*)*\)[\040\t]*)*)*>)");
    //static string emailRegex = new String("/^(([^\\-][\\w\\-]*?\\.?){1,})[^-](\\@{1})([^\\-][\\w+\\.\\-])[^\\2]{1,}[^\\-]$/");
    /**
     * Tests that the input string contains only alpha numeric or white spaces
     * <P>
     * @param evalString The string that is to be evaluated
     * @param required indicates whether the field is required or not
     * @return True if the input is alpha numeric, false otherwise.
     **/
    public static boolean isAlphaNumeric(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        Perl5Util util = new Perl5Util();
        return util.match("/^[\\w\\s]+$/", evalString);
    }

    public static boolean isAlphaNumeric(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isAlphaNumeric(evalString, required);
    }

    public static boolean isLooseAlphaNumeric(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        Perl5Util util = new Perl5Util();
        return util.match("/^[\\w\\s\\.\\,\\/\\-\\(\\)\\+]+$/", evalString);
    }

    public static boolean isLooseAlphaNumeric(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isLooseAlphaNumeric(evalString, required);
    }
        
    /**
     * Tests that the input string contains only numeric
     * <P>
     * @param evalString The string that is to be evaluated
     * @return True if the input is numeric, false otherwise.
     **/
    public static boolean isDecimal(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        Perl5Util util = new Perl5Util();
        return util.match("/^(\\d+\\.)?\\d+$/", evalString);
    }
    
    public static boolean isDecimal(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isDecimal(evalString, required);
    }

    /**
     * Tests that the input string contains only an integer
     * <P>
     * @param evalString The string that is to be evaluated
     * @return True if the input is numeric, false otherwise.
     **/
    public static boolean isInteger (String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        Perl5Util util = new Perl5Util();
        return util.match("/^\\d+$/", evalString);
    }
    
    public static boolean isInteger(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isInteger(evalString, required);
    }

    /**
     * Tests that the input string contains a valid email addess
     * <P>
     * @param evalString The string that is to be evaluated
     * @return True if the input is a valid email address.
     **/
    public static boolean isEmailAddress(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        Perl5Util util = new Perl5Util();
        return util.match("/^(?:\\w[\\w-]*\\.)*\\w[\\w-]*@(?:\\w[\\w-]*\\.)+\\w[\\w-]*$/", evalString);
    }
    
    public static boolean isEmailAddress(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isEmailAddress(evalString, required);
    }
    
    /**
     * Tests that the input string contains a valid URL
     * <P>
     * @param evalString The string that is to be evaluated
     * @return True if the input is a valid URL.
     **/
    public static boolean isURL(String evalString, boolean required)
    {
        try
        {
            if (StringUtils.isEmpty(evalString))
            {
                if (true == required)
                {
                    return false;
                }
                return true;
            }
            
            URL url = new URL(evalString);

            /*
            Perl5Util util = new Perl5Util();
            System.out.println("looking at " +evalString);
            return util.match("/^[\\w%?-_~]$/", evalString);
             */
            //Object content = url.getContent();
            //System.out.println("url contains :["+content+"]");
            return true;
        }
        catch (Exception e)
        {
            System.err.println(evalString+" is not a valid URL: "+ e);
            return false;
        }
    }

    public static boolean isURL(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isURL(evalString, required);
    }

    public static boolean isValidIdentifier(String folderName)
    {
        boolean valid = true;

        char[] chars = folderName.toCharArray();
        for (int ix = 0; ix < chars.length; ix++)
        {
            if (!Character.isJavaIdentifierPart(chars[ix]))
            {
                valid = false; 
                break;
            }
        }
        return valid;
    }

    public static boolean isTooLong(String evalString, int maxLength)
    {
        if (null != evalString)
        {
            return (evalString.length() > maxLength);
        }
        return false;
    }

}

