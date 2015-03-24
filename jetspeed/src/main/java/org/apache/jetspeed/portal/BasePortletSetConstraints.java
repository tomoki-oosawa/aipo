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

/**
 * Trivial implementation of PortletSetConstraints
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BasePortletSetConstraints.java,v 1.3 2004/02/23 04:05:35 jford Exp $
 */
public class BasePortletSetConstraints extends java.util.HashMap
    implements PortletSet.Constraints
{
    /** Get the column the portlet should be displayed in
     *
     * @return a positive column number or null
     */
    public Integer getColumn()
    {
        Object column = get("column");
        if (column instanceof String)
        {
            try
            {
                column = new Integer(Integer.parseInt((String)column));
                put("column", column);
            }
            catch (Exception e)
            {
                remove("column");
                column=null;
            }
        }
        return (Integer)column;
    }
        
    /** Set the column the portlet should be displayed in. This
     *  integer must be positive
     *
     * @param col the column position
     */
    public void setColumn(Integer col) throws IllegalArgumentException
    {
        if (col.intValue() < 0)
        {
            throw new IllegalArgumentException("Column coordinate must be positive");
        }
        
        put("column",col);
    }

    /** Get the row the portlet should be displayed in
     *
     * @return a positive row number or null
     */
    public Integer getRow()
    {
        Object row = get("row");
        if (row instanceof String)
        {
            try
            {
                row = new Integer(Integer.parseInt((String)row));
                put("row", row);
            }
            catch (Exception e)
            {
                remove("row");
                row = null;
            }
        }
        return (Integer)row;
    }
        
    /** Set the row the portlet should be displayed in. This
     *  integer must be positive
     *
     * @param row the column position
     */
    public void setRow(Integer row) throws IllegalArgumentException
    {
        if (row.intValue() < 0)
        {
            throw new IllegalArgumentException("Row coordinate must be positive");
        }
        
        put("row",row);
    }
}
