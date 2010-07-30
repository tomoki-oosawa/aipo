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

import java.io.Serializable;

/**
 * General <Name Value Selected> utility class, can be used to store info
 * regarding checkboxes, dropdowns and other html items
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: HtmlItem.java,v 1.3 2004/02/23 03:23:42 jford Exp $
 */
public class HtmlItem implements Serializable
{

    private int intValue = -1;
    private String name;
    private boolean selected = false;

    private String value = null;

    /**
    * Default constructor
    *
    */
    public HtmlItem()
    {
    }

    /**
    * Constructor that takes intValue and description.
    * Don't have to call set methods if this is used.
    *
    * @param sValue  The list item value
    * @param sDescription  The list item description
    */
    public HtmlItem(int sValue, String sDescription)
    {
      this.setIntValue(sValue);
      this.setName(sDescription);
    }

    public HtmlItem(int sValue, String sDescription, boolean selected)
    {
      this.setIntValue(sValue);
      this.setName(sDescription);
      this.setSelected(selected);
    }

    public HtmlItem(String sValue, String sDescription)
    {
      this.setValue(sValue);
      this.setName(sDescription);
    }

    public HtmlItem(String sValue, String sDescription, boolean selected)
    {
      this.setValue(sValue);
      this.setName(sDescription);
      this.setSelected(selected);
    }

    public HtmlItem(String sDescription)
    {
      this.setValue(sDescription);
      this.setName(sDescription);
    }

    public HtmlItem(String sDescription, boolean selected)
    {
      this.setValue(sDescription);
      this.setName(sDescription);
      this.setSelected(selected);
    }

    /**
    * Setter method
    *
    * @param sValue  Item's value
    */
    private void setIntValue(int sValue)
    {
      intValue = sValue;
    }

    /**
    * Setter method
    *
    * @param sValue  Item's value
    */
    private void setValue(String sValue)
    {
      this.value = sValue;
    }

    /**
    * Setter method
    *
    * @param sValue  Item's description
    */
    private void setName(String sValue)
    {
      name = sValue;
    }

    public void setSelected(boolean value)
    {
      selected = value;
    }
    /**
    * Accessor Method that returns the items value
    *
    * @return  The Item's value
    */
    public int getIntValue()
    {
      return intValue;
    }
    /**
    * Accessor Method that returns the items value
    *
    * @return  The Item's value
    */
    public String getValue()
    {
      return value;
    }

    /**
    * Accessor Method that returns the items description
    *
    * @return  The Item's description
    */
    public String getName()
    {
      return name;
    }

    public boolean getSelected()
    {
      return selected;
    }

}

