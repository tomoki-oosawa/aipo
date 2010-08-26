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

package org.apache.jetspeed.util.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

/**
* Task to merge files to create the database script
*
* @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
* @version $Id: MergeFiles.java,v 1.4 2004/02/23 03:17:53 jford Exp $
*/
public class MergeFiles
{
    protected static List files;
    protected static String dest_file = null;

    public static boolean verbose = false;

    protected static ArrayList baseArray = new ArrayList(1024);
    protected static String lineSeparator = System.getProperty("line.separator", "\r\n");

    public static void main(String[] args) throws Exception
    {
        MergeFiles main=new MergeFiles();

        try
        {
            if(args.length < 2)
            {
                System.out.println("Usage: java MergeFiles [scratch/drop] c:/temp/all.sql c:/temp/dbpsml.sql c:/temp/populate.sql .... c:/temp/File(n)");
                System.out.println("Usage: If scratch is specified then all sql statements starting with DROP will be overlooked.");
                System.out.println("Usage: If drop is specified then only sql statements starting with DROP will be added.");
                System.out.println("Usage: All the files listed after c:/temp/all.sql will be added to c:/temp/all.sql");
                throw new Exception("Incorrect number of arguments supplied");
            }
            int file_index = 0;
            boolean db_from_scratch = false;
            boolean db_drop = false;

            if (args[0].equals("scratch"))
            {
                file_index = 1;
                db_from_scratch = true;
            }
            else if (args[0].equals("drop"))
            {
                file_index = 1;
                db_drop = true;                
            }

            files = new Vector(args.length - 1 - file_index);
            dest_file = args[file_index];
            for (int index = (file_index + 1); index < args.length; index++)
            {
                files.add(args[index]);
            }

            for (int index = 0; index < files.size(); index++)
            {
                BufferedReader reader =  new BufferedReader(new FileReader((String)files.get(index)));
                String line = null;
                int idx = 0;
                while((line = reader.readLine()) != null)
                {   if (line.startsWith("#"))
                    {
                        continue;
                    }
                    if (db_from_scratch)
                    {
                        if (!(line.startsWith("DROP") || 
                              line.startsWith("drop") || 
                              line.startsWith("Drop")))
                        {
                            baseArray.add(idx, line);
                            idx++;
                        }
                    }
                    else if (db_drop)
                    {
                        if ( (line.startsWith("DROP") || 
                              line.startsWith("drop") || 
                              line.startsWith("Drop")))
                        {
                            baseArray.add(idx, line);
                            idx++;
                        }                        
                    }
                    else
                    {
                        baseArray.add(idx, line);
                        idx++;
                    }
                    if(verbose)
                        System.out.println("While reading baseArray["+idx+"] = " + line);
                }
                reader.close();
            }
            if(verbose)
                System.out.println("\nMerge Files\n");

            baseArray.add("commit;");
            baseArray.trimToSize();

            main.writeToFile();

        }
        catch(FileNotFoundException ex)
        {
            System.err.println(ex.getMessage());
        }
        catch(IOException ex)
        {
            System.err.println(ex.getMessage());
        }
        catch(SecurityException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    public void writeToFile() throws FileNotFoundException, IOException
    {
        FileOutputStream writer = null;
        try
        {
            writer = new FileOutputStream(dest_file);
        }
        catch (FileNotFoundException ex)
        {
            File file = new File(dest_file);
            writer = new FileOutputStream(file.getPath(), false);
        }
        writer.flush();
        for (int i = 0; i < baseArray.size(); i++)
        {
            if(verbose)
                System.out.println("While writing baseArray["+i+"] = " + baseArray.get(i));
            writer.write(((String)baseArray.get(i)).getBytes());
            writer.write(lineSeparator.getBytes());
            writer.flush();
        }
        writer.close();

    }


}

