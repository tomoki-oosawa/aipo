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

package org.apache.jetspeed.portal;

//standard java stuff
import java.util.Map;
import java.util.Enumeration;

/**
 * The PortletSet is basically a wrapper around an array of portlets. It provides
 * runtime context for a set of portlets.
 * A portlet can get its current set by calling via its PortletConfig
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @version $Id: PortletSet.java,v 1.26 2004/02/23 04:05:35 jford Exp $
 */
public interface PortletSet extends Portlet
{
    /**
     * Return the current controller for this set
     */
    public PortletController getController();

    /**
     * Set the controller for this set
     */
    public void setController(PortletController controller);

    /**
     * Returns the number of portlets currently stored in this set
     */
    public int size();

    /**
     * Returns the portlet set as an array.
     */
    public Portlet[] toArray();

    /**
     * Returns the Portlet at position pos
     */
    public Portlet getPortletAt(int pos);

    /**
     * Returns the Portlet with the given id
     */
    public Portlet getPortletByID(String id);

    /**
    Returns the Portlet with the given name
    */
    public Portlet getPortletByName(String name);

    /**
     * Returns the portlet set as an Enumeration
     */
    public Enumeration getPortlets();

    /**
     * Add a portlet to this set.It updates its config to modify the current set
     */
    public void addPortlet(Portlet portlet);

    /**
     * Add a portlet to this set.It updates its config to modify the current set
     */
    public void addPortlet(Portlet portlet, int position);

    /**
     * Add a portlet to this set.It updates its config to modify the current set
     */
    public void addPortlet(Portlet portlet, Constraints constraints);

    /**
     * Add a portlet to this set.It updates its config to modify the current set
     */
    public void addPortlet(Portlet portlet, Constraints constraints, int position);

    /**
     * The PortletSetConstraints is used to associate layout constraints with a 
     * Portlet within a Set. These constraints may be used by the PortletController
     * to render the layout of any given PortletSet correctly.
     */
    public interface Constraints extends Map
    {   
        /** Get the column the portlet should be displayed in
         *
         * @return a positive column number or null
         */
        public Integer getColumn();
        
        /** Set the column the portlet should be displayed in. This
         *  integer must be positive
         *
         * @param col the column position
         */
        public void setColumn(Integer col) throws IllegalArgumentException;
        
        /** Get the row the portlet should be displayed in
         *
         * @return a positive row number or null
         */
        public Integer getRow();
        
        /** Set the row the portlet should be displayed in. This
         *  integer must be positive
         *
         * @param row the column position
         */
        public void setRow(Integer row) throws IllegalArgumentException;
    }
}
