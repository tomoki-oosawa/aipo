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

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LineCounter
{
    int count = 0;

    public static void main( String args[] )
    {
        LineCounter lc = new LineCounter();
        int count = 0;
        int totalCount = 0;
        for (int ix = 0; ix < args.length; ix++)
        {
            count = lc.run(args[ix]);
            System.out.println( "Count for path [" + args[ix] + "] = " + count );
            totalCount += count;
        }
        System.out.println( "Total Count = " + totalCount );
    }

    public int run(String path)
    {
        System.out.println("Running LineCounter for " + path );
        count = 0;
        return traverse(path);
    }

    private int traverse(String path)
    {
        File file = new File(path);
        if (file.isFile()) 
        {
            try 
            {
                String name = file.getName();
                if (name.endsWith("java"))
                    count += countFile(file);
            }
            catch (Exception e)
            {
                System.err.println("Failed to count file: " + path + " : " + e.toString());
            }
        } 
        else if (file.isDirectory()) 
        {
            if (!path.endsWith(File.separator))
                path += File.separator;

    //        System.out.println("Processing directory: " + path);
            String list[] = file.list();

            // Process all files recursivly
            for(int ix = 0; list != null && ix < list.length; ix++)
                traverse(path + list[ix]);


        }
        return count;
    }            

    private int countFile( File file )
            throws FileNotFoundException, IOException
    {
  //      System.out.println("Processing file: " + file.getPath());
        FileReader reader = new FileReader( file );
        BufferedReader br = new BufferedReader( reader );
        String s;
        int mycount = 0;

        while ((s = br.readLine()) != null)
        {
            if (s.length() > 0)
                mycount++;
        }
        return mycount;
    }
}

