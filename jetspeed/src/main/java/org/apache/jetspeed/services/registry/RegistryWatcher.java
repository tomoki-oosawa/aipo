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

package org.apache.jetspeed.services.registry;

import java.io.File;
import java.io.FileFilter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Monitors a Registry directory and notifies the associated Registry
 * of file updates.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: RegistryWatcher.java,v 1.10 2004/02/23 03:31:50 jford Exp $
 */
public class RegistryWatcher extends Thread
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(RegistryWatcher.class.getName());
    
    /** Minimum scan rate for evaluating file refresh */
    public static final int SCAN_RATE = 10;

    /**
    The files monitored by this watcher
    */
    private Hashtable files = new Hashtable();

    /**
    the refresh rate, in milliseconds, to use for monitoring this file
    */
    private long refreshRate = 0;

    /**
    The object that relies on this RegsitryWatcher
    */
    private FileRegistry subscriber = null;

    /**
    The filter to use for filtering registry files
    */
    private FileFilter filter = null;

    /**
     * This object marks that we are done
    */
    private boolean done = false;

    /**
     * Creates a default RegistryWatcher
     */
    public RegistryWatcher()
    {
        setDaemon(true);
        setPriority(Thread.MIN_PRIORITY);
    }

    /** Modifies the subscriber to this Watcher
     *
     * @param registry the new registry subscriber
     */
    public void setSubscriber(FileRegistry registry)
    {
        synchronized (this)
        {
            if (subscriber!=null)
            {
                Enumeration en = files.keys();
                while(en.hasMoreElements())
                {
                    try
                    {
                        subscriber.removeFragment(((File)en.nextElement()).getCanonicalPath());
                    }
                    catch (Exception e)
                    {
                        logger.error("RegistryWatcher: Can't remove fragment", e);
                    }
                }
            }

            this.subscriber = registry;

            if (subscriber!=null)
            {
                Enumeration en = files.keys();
                while(en.hasMoreElements())
                {
                    try
                    {
                        subscriber.loadFragment(((File)en.nextElement()).getCanonicalPath());
                    }
                    catch (Exception e)
                    {
                        logger.error("RegistryWatcher: Can't load fragment", e);
                    }
                }
            }
        }
    }

    /** @return the subscriber to this watcher */
    public FileRegistry getSubscriber()
    {
        return this.subscriber;
    }

    /** Sets the refresh rate for this watcher
     *  @param refresh the refresh rate in seconds
     */
    public void setRefreshRate(long refresh)
    {
        this.refreshRate = (( refresh > SCAN_RATE ) ? refresh : SCAN_RATE) * 1000;
    }

    /** @return the refresh rate, in seconds, of this watcher */
    public long getRefreshRate()
    {
        return refreshRate / 1000;
    }

    /** Sets the file filter for selecting the registry files
     *  @param filter the file filter to use
     */
    public void setFilter(FileFilter filter)
    {
        this.filter = filter;
    }

    /** @return the file filter used by this watcher instance */
    public FileFilter getFilter()
    {
        return filter;
    }

    /** Change the base file or directory to be monitored by this watcher
     *
     * @param f the file or directory to monitor
     */
    public void changeBase(File f)
    {
        synchronized (this)
        {
            if (this.subscriber!=null)
            {
                Enumeration en = files.keys();
                while (en.hasMoreElements())
                {
                    try
                    {
                        subscriber.removeFragment(((File)en.nextElement()).getCanonicalPath());
                    }
                    catch (Exception e)
                    {
                        logger.error("RegistryWatcher: Can't remove fragment", e);
                    }
                }
            }
            files.clear();
            findFiles(f);
        }
    }

    /**
     * Refresh the monitored file list
     *
     * @param f the file or directory to monitor
     */
    private void findFiles(File f)
    {
        File[] contents = null;

        if (f.exists() && f.canRead())
        {
            this.files.put(f,new Long(f.lastModified()));

            if (f.isDirectory())
            {

                if (filter != null)
                    contents = f.listFiles(filter);
                else
                    contents = f.listFiles();

                if (contents!=null)
                {
                    for (int i=0; i< contents.length; i++)
                    {
                        files.put(contents[i],new Long(contents[i].lastModified()));

                        if (subscriber!=null)
                        {
                            try
                            {
                                subscriber.loadFragment(contents[i].getCanonicalPath());
                            }
                            catch (Exception e)
                            {
                                logger.error("RegistryWatcher: Can't load fragment", e);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * <p>Main routine for the monitor which periodically checks whether
     * the filex have been modified.</p>
     * The algorithm used does not guarantee a constant refresh rate
     * between invocations.
     */
    public void run()
    {
        try
        {
            while(!done)
            {
                boolean needRefresh = false;

                synchronized (this)
                {
                    Map fragments = subscriber.getFragmentMap();

                    if (logger.isDebugEnabled())
                    {
                        logger.debug( "RegistryWatcher: Saving dirty fragments.");
                    }

                    Iterator i = fragments.keySet().iterator();
                    while(i.hasNext())
                    {
                        try
                        {
                            String filename = (String)i.next();
                            RegistryFragment fragment = (RegistryFragment)subscriber.getFragmentMap().get(filename);

                            // if fragment has some uncommitted changes
                            if (fragment.isDirty())
                            {
                                //save it to disk
                                subscriber.saveFragment(filename);

                                if (logger.isDebugEnabled())
                                {
                                    logger.debug( "RegistryWatcher: Saved " + filename);
                                }

                                //and update the stored timestamp
                                Enumeration en = files.keys();
                                while(en.hasMoreElements())
                                {
                                    File f = (File)en.nextElement();
                                    if (filename.equals(f.getCanonicalPath()))
                                    {
                                        files.put(f,new Long(f.lastModified()));
                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            logger.error("RegistryWatcher: exception during update",e);
                        }
                    }

                    if (logger.isDebugEnabled())
                    {
                        logger.debug( "RegistryWatcher: Checking for updated files.");
                    }

                    Enumeration en = files.keys();
                    while(en.hasMoreElements())
                    {
                        try
                        {
                            File f = (File)en.nextElement();
                            long modified = ((Long)files.get(f)).longValue();

                            if (!f.exists())
                            {
                                files.remove(f);
                            }
                            else
                            {
                                if (f.lastModified() > modified)
                                {
                                    files.put(f,new Long(f.lastModified()));

                                    if (f.isDirectory())
                                    {
                                        File[] contents = null;

                                        if (filter != null)
                                        {
                                            contents = f.listFiles(filter);
                                        }
                                        else
                                        {
                                            contents = f.listFiles();
                                        }

                                        if (contents!=null)
                                        {
                                            for (int idx=0; idx< contents.length; idx++)
                                            {
                                                if (files.get(contents[idx])==null)
                                                {
                                                    files.put(contents[idx],new Long(contents[idx].lastModified()));

                                                    if (subscriber!=null)
                                                    {
                                                        subscriber.loadFragment(contents[idx].getCanonicalPath());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        subscriber.loadFragment(f.getCanonicalPath());
                                    }

                                    if (logger.isDebugEnabled())
                                    {
                                        logger.debug("RegistryWatcher: Refreshing because "
                                                    + f.getCanonicalPath()
                                                    + " was modified.("
                                                    + f.lastModified()
                                                    + " "
                                                    + modified
                                                    + ")");
                                    }

                                    RegistryFragment frag = (RegistryFragment)fragments.get(f.getCanonicalPath());

                                    if (frag!=null)
                                    {
                                        frag.setChanged(true);
                                    }

                                    needRefresh = true;
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            logger.error("RegistryWatcher: exception during update",e);
                        }
                    }

                    if (needRefresh)
                    {
                        subscriber.refresh();
                        needRefresh = false;
                    }

                    // make sure to reset the state of all fragments
                    i = fragments.keySet().iterator();
                    while(i.hasNext())
                    {
                        RegistryFragment frag = (RegistryFragment)fragments.get((String)i.next());
                        frag.setDirty(false);
                        frag.setChanged(false);
                    }
                }

                sleep( refreshRate );
            }
        }
        catch  (InterruptedException e)
        {
            logger.error("RegistryWatcher: Stopping monitor: ", e);
            return;
        }
    }

    /**
     * Mark that the watching thread should be stopped
     */
    public void setDone()
    {
        done = true;
        logger.info("RegistryWatcher: Watching thread stop requested");
    }

}
