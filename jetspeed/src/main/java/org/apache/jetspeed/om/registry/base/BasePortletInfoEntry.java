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

package org.apache.jetspeed.om.registry.base;

import org.apache.jetspeed.om.registry.*;

import java.util.Vector;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;

/**
 * The BasePortletInfoEntry is a bean like implementation of the PortletInfoEntry
 * interface suitable for Castor XML serialization
 *
 * @see org.apache.jetspeed.om.registry.PortletInfoEntry
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BasePortletInfoEntry.java,v 1.9 2004/03/29 19:28:24 jford Exp $
 */
public abstract class BasePortletInfoEntry extends BaseRegistryEntry
{

    protected String classname;

    protected Vector parameter = new Vector();

    protected transient Map nameIdx = null;

    protected Vector medias = new Vector();

    protected transient Map mediasIdx = null;

    protected Vector tools = new Vector();

    protected transient Map toolsIdx = null;

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object==null)
        {
            return false;
        }

        BasePortletInfoEntry obj = (BasePortletInfoEntry)object;

        if (classname!=null)
        {
            if (!classname.equals(obj.getClassname()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getClassname()!=null)
            {
                return false;
            }
        }

        Iterator i = parameter.iterator();
        Iterator i2 = obj.getParameters().iterator();
        while(i.hasNext())
        {
            BaseParameter p1 = (BaseParameter)i.next();
            BaseParameter p2 = null;

            if (i2.hasNext())
            {
                p2 = (BaseParameter)i2.next();
            }
            else
            {
                return false;
            }

            if (!p1.equals(p2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        i = medias.iterator();
        i2 = obj.getMediaTypes().iterator();
        while(i.hasNext())
        {
            BaseMediaType m1 = (BaseMediaType)i.next();
            BaseMediaType m2 = null;

            if (i2.hasNext())
            {
                m2 = (BaseMediaType)i2.next();
            }
            else
            {
                return false;
            }

            if (!m1.equals(m2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        i = tools.iterator();
        i2 = obj.getTools().iterator();
        while(i.hasNext())
        {
            BaseToolDescriptor t1 = (BaseToolDescriptor)i.next();
            BaseToolDescriptor t2 = null;

            if (i2.hasNext())
            {
                t2 = (BaseToolDescriptor)i2.next();
            }
            else
            {
                return false;
            }

            if (!t1.equals(t2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        return super.equals(object);
    }

    /** @return the classname associated to this entry */
    public String getClassname()
    {
        return this.classname;
    }

    /** Sets the classname for this entry. This classname is used for instanciating
     *  the associated element
     *
     *  @param classname the classname used for instanciating the component associated with
     *  this entry
     */
    public void setClassname( String classname )
    {
        this.classname = classname;
    }

    /** @return an enumeration of this entry parameter names */
    public Iterator getParameterNames()
    {
        synchronized (parameter)
        {
            if (nameIdx == null)
            {
                buildNameIndex();
            }
        }

        return nameIdx.keySet().iterator();
    }


    /** Search for a named parameter and return the associated
     *  parameter object. The search is case sensitive.
     *
     *  @return the parameter object for a given parameter name
     *  @param name the parameter name to look for
     */
    public Parameter getParameter( String name )
    {
        synchronized (parameter)
        {
            if (nameIdx == null)
            {
                buildNameIndex();
            }
        }

        if (name != null)
        {
            Integer pos = (Integer)nameIdx.get(name);

            if (pos != null)
            {
                return (Parameter)parameter.elementAt(pos.intValue());
            }
        }

        return null;
    }


    /** Returns a map of parameter values keyed on the parameter names
     *  @return the parameter values map
     */
    public Map getParameterMap()
    {
        Hashtable params = new Hashtable();
        Enumeration en = parameter.elements();
        while(en.hasMoreElements())
        {
            Parameter param = (Parameter)en.nextElement();
            String key = param.getName();
            String value = param.getValue();
            if(key != null && value != null)
            {
            	params.put(key, value);
            }
        }
        return params;
    }

    /** Adds a new parameter for this entry
     *  @param name the new parameter name
     *  @param value the new parameter value
     */
    public void addParameter( String name, String value )
    {
        if (name != null)
        {
            Parameter p = getParameter(name);
            if (p == null)
            {
                if (this instanceof PortletEntry)
                    p = new BaseCachedParameter();
               else
                    p = new BaseParameter();
                p.setName(name);
            }

            p.setValue(value);

            addParameter(p);

        }
    }

    /** Adds a new parameter for this entry
     *  @param parameter the new parameter to add
     */
    public void addParameter( Parameter param )
    {
        synchronized (parameter)
        {
            if (parameter == null)
                parameter = new Vector();

            if (nameIdx == null)
                buildNameIndex();

            parameter.addElement( param );
            nameIdx.put( param.getName(), new Integer( parameter.size()-1 ) );
        }
    }

    /** Removes all parameter values associated with the
     *  name
     *
     * @param name the parameter name to remove
     */
    public void removeParameter( String name )
    {
        if (name == null) return;

        synchronized (parameter)
        {
            Iterator i = parameter.iterator();
            while(i.hasNext())
            {
                Parameter param = (Parameter)i.next();
                if (param.getName().equals(name))
                {
                    i.remove();
                }
            }

            buildNameIndex();
        }
    }

    /**
     * Returns a list of the supported media type names
     *
     * @return an iterator on the supported media type names
     */
    public Iterator listMediaTypes()
    {
        if (mediasIdx == null)
        {
            synchronized( medias )
            {
                buildMediasIndex();
            }
        }

        return mediasIdx.keySet().iterator();
    }

    /**
     * Test if a given media type is supported by this entry.
     * The test is done by a case sensitive name comparison
     *
     * @param name the media type name to test for.
     * @return true is the media type is supported false otherwise
     */
    public boolean hasMediaType(String name)
    {
        if (mediasIdx == null)
        {
            synchronized( medias )
            {
                buildMediasIndex();
            }
        }
        return ((name!=null)&&(mediasIdx.get(name)!=null));
    }

    /**
     * Add a new supported media type
     *
     * @param name the media type name to add.
     */
    public void addMediaType(String name)
    {
        if (name!=null)
        {
            synchronized (medias)
            {
                if (mediasIdx == null)
                {
                    buildMediasIndex();
                }
                
                BaseMediaType m = new BaseMediaType();
                m.setRef(name);
                mediasIdx.put(name,new Integer(medias.size()));
                medias.add(m);
            }
        }
    }

    /**
     * Remove support for a given media type
     *
     * @param name the media type name to remove.
     */
    public void removeMediaType(String name)
    {
        if (name != null)
        {
            synchronized (medias)
            {
                mediasIdx.remove(name);
                
                BaseMediaType m = new BaseMediaType();
                m.setRef(name);

                Iterator i = medias.iterator();
                while (i.hasNext())
                {
                    if (i.next().equals(m))
                    {
                        i.remove();
                        return;
                    }
                }
            }
        }
    }

    // Castor serialization accessor methods

    /** Needed for Castor 0.8.11 XML serialization for retrieving the
     *  parameters objects associated to this object
     */
    public Vector getParameters()
    {
        return this.parameter;
    }

    public void setParameters(Vector parameters)
    {
        this.parameter = parameters;
    }

    public void setMediaTypes(Vector mediaTypes)
    {
        this.medias = mediaTypes;
    }

    /** Needed for Castor 0.8.11 XML serialization for retrieving the
     *  media type names associated to this object
     */
    public Vector getMediaTypes()
    {
        return this.medias;
    }

    public Vector getTools()
    {
        return this.tools;
    }

    public void setTools(Vector tools)
    {
        this.tools = tools;
    }

    /** This method recreates the paramter name index for quick retrieval
     *  of parameters by name. Shoule be called whenever a complete index
     *  of parameter should be rebuilt (eg removing a parameter or setting
     *  a parameters vector)
     */
    protected void buildNameIndex()
    {
        Hashtable idx = new Hashtable();

        Iterator i = parameter.iterator();
        int count = 0;
        while( i.hasNext() )
        {
            Parameter p = (Parameter)i.next();
            idx.put( p.getName(), new Integer(count) );
            count++;
        }

        this.nameIdx = idx;
    }

    /** This method recreates the media name index for quick retrieval
     *  by name.
     */
    private void buildMediasIndex()
    {
        Hashtable idx = new Hashtable();

        Iterator i = medias.iterator();
        int count = 0;
        while( i.hasNext() )
        {
            BaseMediaType b = (BaseMediaType)i.next();
            idx.put( b.getRef(), new Integer(count) );
            count++;
        }

        this.mediasIdx = idx;
    }

    /** @return an enumeration of this entry parameter names */
    public Iterator getToolNames()
    {
        synchronized (tools)
        {
            if (toolsIdx == null)
            {
                buildToolsIndex();
            }
        }

        return toolsIdx.keySet().iterator();
    }


    /** Search for a named parameter and return the associated
     *  parameter object. The search is case sensitive.
     *
     *  @return the parameter object for a given parameter name
     *  @param name the parameter name to look for
     */
    public ToolDescriptor getTool( String name )
    {
        synchronized (tools)
        {
            if (toolsIdx == null)
            {
                buildToolsIndex();
            }
        }

        if (name != null)
        {
            Integer pos = (Integer)toolsIdx.get(name);

            if (pos != null)
            {
                return (ToolDescriptor)tools.elementAt(pos.intValue());
            }
        }

        return null;
    }


    /** Returns a map of parameter values keyed on the parameter names
     *  @return the parameter values map
     */
    public Map getToolMap()
    {
        Hashtable map = new Hashtable();
        Enumeration en = tools.elements();
        while(en.hasMoreElements())
        {
            ToolDescriptor desc = (ToolDescriptor)en.nextElement();
            map.put(desc.getName(),desc);
        }
        return map;
    }

    /** Adds a new parameter for this entry
     *  @param parameter the new parameter to add
     */
    public void addTool( ToolDescriptor tool )
    {
        synchronized (tools)
        {
            if (tools == null)
                tools = new Vector();

            if (toolsIdx == null)
                buildToolsIndex();

            tools.addElement( tool );
            toolsIdx.put( tool.getName(), new Integer( tools.size()-1 ) );
        }
    }

    /** Removes all parameter values associated with the
     *  name
     *
     * @param name the parameter name to remove
     */
    public void removeTool( String name )
    {
        if (name == null) return;

        synchronized (tools)
        {
            Iterator i = tools.iterator();
            while(i.hasNext())
            {
                ToolDescriptor tool = (ToolDescriptor)i.next();
                if (tool.getName().equals(name))
                {
                    i.remove();
                }
            }

            buildToolsIndex();
        }
    }

    /** This method recreates the media name index for quick retrieval
     *  by name.
     */
    private void buildToolsIndex()
    {
        Hashtable idx = new Hashtable();

        Iterator i = tools.iterator();
        int count = 0;
        while( i.hasNext() )
        {
            ToolDescriptor b = (ToolDescriptor)i.next();
            idx.put( b.getName(), new Integer(count) );
            count++;
        }

        this.toolsIdx = idx;
    }

}