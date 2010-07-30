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
package org.apache.jetspeed.services.registry;

import org.apache.turbine.util.Log;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.text.SimpleDateFormat;

/**
 * Registry watcher for Database Registry service.
 * Keeps any cached registry entries refreshed with backend database.
 *
 * @author <a href="mailto:susinha@cisco.com">Suchisubhra Sinha</a>
 * @version $Id: DatabaseRegistryWatcher.java,v 1.2 2004/02/23 03:31:50 jford Exp $
 */
public class DatabaseRegistryWatcher extends Thread 
{
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
        * This object marks that we are done
    */
    private boolean done = false;

    /**
        * Creates a default RegistryWatcher
        */
    public DatabaseRegistryWatcher()
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
            if (subscriber != null)
            {
                Enumeration en = files.keys();
                while (en.hasMoreElements())
                {
                    try
                    {
                        subscriber.removeFragment(((String) en.nextElement()));
                    }
                    catch (Exception e)
                    {
                        Log.error("RegistryWatcher: Can't remove fragment", e);
                    }
                }
            }
            this.subscriber = registry;
            if (subscriber != null)
            {
                Enumeration en = files.keys();
                while (en.hasMoreElements())
                {
                    try
                    {
                        subscriber.loadFragment(((String) en.nextElement()));
                    }
                    catch (Exception e)
                    {
                        Log.error("RegistryWatcher: Can't load fragment", e);
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
        this.refreshRate = ((refresh > SCAN_RATE) ? refresh : SCAN_RATE) * 1000;
    }
    /** @return the refresh rate, in seconds, of this watcher */
    public long getRefreshRate()
    {
        return refreshRate / 1000;
    }
    /** Change the base file to be monitored by this watcher
        *
        * @param f the file  to monitor
     */
    public void changeBase(Vector f)
    {
        synchronized (this)
        {
            if (this.subscriber != null)
            {
                Enumeration en = files.keys();
                while (en.hasMoreElements())
                {
                    try
                    {
                        subscriber.removeFragment(((String) en.nextElement()));
                    }
                    catch (Exception e)
                    {
                        Log.error("RegistryWatcher: Can't remove fragment", e);
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
    private void findFiles(Vector s)
    {
        Enumeration en = s.elements();
        while (en.hasMoreElements())
        {
            String f = (String) en.nextElement();
            if (f != null)
            {
                this.files.put(f, "now");
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
            while (!done)
            {
                boolean needRefresh = false;
                synchronized (this)
                {
                    Map fragments = subscriber.getFragmentMap();
                    if (Log.getLogger().isDebugEnabled())
                    {
                        Log.debug("RegistryWatcher: Saving dirty fragments.");
                    }
                    Iterator i = fragments.keySet().iterator();
                    while (i.hasNext())
                    {
                        try
                        {
                            String filename = (String) i.next();
                            RegistryFragment fragment =
                                (RegistryFragment) subscriber
                                    .getFragmentMap()
                                    .get(
                                    filename);
                            // if fragment has some uncommitted changes
                            if (fragment.isDirty())
                            {
                                //and update the stored timestamp
                                Enumeration en = files.keys();
                                while (en.hasMoreElements())
                                {
                                    String f = (String) en.nextElement();
                                    //get  Current time
                                    SimpleDateFormat sdf =
                                        new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
                                    java.util.Date now = new java.util.Date();
                                    String currentTime = sdf.format(now);
                                    if (filename.equals(f))
                                    {
                                        files.put(f, currentTime);
                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            Log.error(
                                "RegistryWatcher: exception during update",
                                e);
                        }
                    }
                    if (Log.getLogger().isDebugEnabled())
                    {
                        Log.debug(
                            "RegistryWatcher: Checking for updated files.");
                    }
                    Enumeration en = files.keys();
                    while (en.hasMoreElements())
                    {
                        try
                        {
                            String f = (String) en.nextElement();
                            String modified = (String) files.get(f);
                            subscriber.loadFragment(f);
                            RegistryFragment frag =
                                (RegistryFragment) fragments.get(f);
                            if (frag != null)
                            {
                                frag.setChanged(true);
                            }
                            needRefresh = true;
                        }
                        catch (Exception e)
                        {
                            Log.error(
                                "RegistryWatcher: exception during update",
                                e);
                        }
                    }
                    if (needRefresh)
                    {
                        subscriber.refresh();
                        needRefresh = false;
                    }
                    // make sure to reset the state of all fragments
                    i = fragments.keySet().iterator();
                    while (i.hasNext())
                    {
                        RegistryFragment frag =
                            (RegistryFragment) fragments.get((String) i.next());
                        frag.setDirty(false);
                        frag.setChanged(false);
                    }
                }
                sleep(refreshRate);
            }
        }
        catch (InterruptedException e)
        {
            Log.error("RegistryWatcher: Stopping monitor: ");
            Log.error(e);
            return;
        }
    }
    /**
        * Mark that the watching thread should be stopped
        */
    public void setDone()
    {
        done = true;
        Log.info("RegistryWatcher: Watching thread stop requested");
    }
}
