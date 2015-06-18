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

package org.apache.jetspeed.capability;

//standard Jetspeed stuff
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.MediaTypeEntry;
import org.apache.jetspeed.om.registry.MediaTypeRegistry;
import org.apache.jetspeed.services.Registry;

//standard Java stuff
import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;

/**
 * Read only wrapper around a ClientEntry registry entry that
 * implements the CapabilityMap interface
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: BaseCapabilityMap.java,v 1.8 2004/02/23 02:46:39 jford Exp $
 */
public class BaseCapabilityMap implements CapabilityMap
{

    private String      useragent;
    private ClientEntry entry;

    protected BaseCapabilityMap(String agent, ClientEntry entry)
    {
        this.useragent = agent;
        this.entry = entry;
    }

    /**
    @see CapabilityMap#getPreferredType
    */
    public MimeType getPreferredType()
    {
        return entry.getMimetypeMap().getPreferredMimetype();
    }

    /**
    Returns the preferred media type for the current user-agent
    */
    public String getPreferredMediaType()
    {
        Iterator i = listMediaTypes();

        if (i.hasNext())
        {
            return (String)i.next();
        }

        return null;
    }

    /**
     * Returns an ordered list of supported media-types, from most preferred
     * to least preferred
     */
    public Iterator listMediaTypes()
    {
        Vector results = new Vector();
        Vector types = new Vector();

        // first copy the current media type list, ordered by global preference
        Enumeration en = ((MediaTypeRegistry)Registry.get(Registry.MEDIA_TYPE)).getEntries();
        while (en.hasMoreElements())
        {
            types.add(en.nextElement());
        }

        //then retrieve a list of supported mime-types, ordered by
        //preference

        Iterator mimes = entry.getMimetypeMap().getMimetypes();

        //now, for each mime-type test if the media is supported
        while(mimes.hasNext())
        {
            String mime = ((MimeType)mimes.next()).getContentType();
            Iterator i = types.iterator();

            while(i.hasNext())
            {
                MediaTypeEntry mte = (MediaTypeEntry)i.next();

                if (mime.equals(mte.getMimeType()))
                {
                    if (entry.getCapabilityMap().containsAll(mte.getCapabilityMap()))
                    {
                        results.add(mte.getName());
                    }
                }
            }
        }

        return results.iterator();
    }

    /**
    @see CapabilityMap#getAgent
    */
    public String getAgent()
    {
        return this.useragent;
    }

    /**
    @see CapabilityMap#hasCapability
    */
    public boolean hasCapability( int cap )
    {
        return false;
    }

    /**
    @see CapabilityMap#hasCapability
    */
    public boolean hasCapability( String capability )
    {
        Iterator i = entry.getCapabilityMap().getCapabilities();

        while (i.hasNext())
        {
            String cap = (String)i.next();

            if (cap.equals(capability))
            {
                return true;
            }
        }

        return false;
    }

    /**
    @see CapabilityMap#getMimeTypes
    */
    public MimeType[] getMimeTypes()
    {
        Vector v = new Vector();
        Iterator i = entry.getMimetypeMap().getMimetypes();

        while (i.hasNext())
        {
            MimeType mime = (MimeType)i.next();
            v.add(mime);
        }

        return (MimeType[])v.toArray();
    }

    /**
    @see CapabilityMap#supportsMimeType
    */
    public boolean supportsMimeType( MimeType mimeType )
    {
        Iterator i = entry.getMimetypeMap().getMimetypes();

        while (i.hasNext())
        {
            MimeType mime = (MimeType)i.next();

            if (mime.equals(mimeType))
            {
                return true;
            }
        }

        return false;

    }

    /**
    @see CapabilityMap#supportsMimeType
    */
    public boolean supportsMediaType( String media )
    {
        if (media == null)
        {
            return true;
        }

        MediaTypeEntry mte = (MediaTypeEntry)Registry.getEntry(Registry.MEDIA_TYPE, media);

        if (!supportsMimeType(new MimeType(mte.getMimeType())))
        {
            return false;
        }

        return entry.getCapabilityMap().containsAll(mte.getCapabilityMap());

    }

    /**
    Create a map string representation
    */
    public String toString()
    {
        StringBuffer desc = new StringBuffer(entry.getName());

        Iterator i = entry.getMimetypeMap().getMimetypes();

        while (i.hasNext())
        {
            MimeType mime = (MimeType)i.next();
            desc.append( mime ).append("-");
        }

        i = entry.getCapabilityMap().getCapabilities();

        while ( i.hasNext() )
        {
          String capa = (String)i.next();
          desc.append(capa).append("/");
        }

        return desc.toString();
    }

}

