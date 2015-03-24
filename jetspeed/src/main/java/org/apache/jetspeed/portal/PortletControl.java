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
 * The PortletControl acts both as a decoration around a Portlet or PortletSet
 * and also as the window manager for the enclosed Portlet(Set). As such it
 * controls the operation that may be performed on this portlet and whether the
 * portlet content should be displayed or not. PortletControl also delegates all
 * Portlet and PortletSet methods to its inner object and can thus be
 * transparently cascaded or substituted to a simple portlet wherever in a PSML
 * object tree.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: PortletControl.java,v 1.24 2004/02/23 04:05:35 jford Exp $
 */
public interface PortletControl extends Portlet, PortletSet {

  public String COLOR = "#CCCCCC";

  public String BACKGROUND_COLOR = "#FFFFFF";

  public String TITLE_COLOR = "#DDDDDD";

  public String WIDTH = "100%";

  /**
   * @return the PortletControl specific configuration
   */
  public PortletControlConfig getConfig();

  /**
   * Sets the PortletControl specific configuration
   * 
   * @param conf
   *          the new PortletControl configuration
   */
  public void setConfig(PortletControlConfig conf);

  /**
   * Sets the portlet to be managed by this control
   *
   * @param portlet
   *          the new portlet to be managed by the control
   */
  public void setPortlet(Portlet portlet);

  /**
   * Retrieves the portlet managed by this control
   * 
   * @return the portlet object managed or null
   */
  public Portlet getPortlet();

  /**
   * Initializes the control and associates it with a portlet
   *
   * @param portlet
   *          the portlet to be managed by this control
   */
  public void init(Portlet portlet);

  /**
   * Returns the color to use for displaying the portlet text
   *
   * This method is deprecated and is only useful for ECS based Control
   * implementation
   * 
   * @deprecated
   * @return the text color value in HTML format (#RRGGBB)
   */
  @Deprecated
  public String getColor();

  /**
   * Sets the color to use for displaying the portlet text
   *
   * This method is deprecated and is only useful for ECS based Control
   * implementation
   * 
   * @deprecated
   * @param color
   *          the text color value in HTML format (#RRGGBB)
   */
  @Deprecated
  public void setColor(String color);

  /**
   * Returns the color to use for displaying the portlet background
   *
   * This method is deprecated and is only useful for ECS based Control
   * implementation
   * 
   * @deprecated
   * @return the text color value in HTML format (#RRGGBB)
   */
  @Deprecated
  public String getBackgroundColor();

  /**
   * Sets the color to use for displaying the portlet background
   *
   * This method is deprecated and is only useful for ECS based Control
   * implementation
   * 
   * @deprecated
   * @param backgroundColor
   *          the background color value in HTML format (#RRGGBB)
   */
  @Deprecated
  public void setBackgroundColor(String backgroundColor);

  /**
   * Returns the color to use for displaying the portlet title
   *
   * This method is deprecated and is only useful for ECS based Control
   * implementation
   * 
   * @deprecated
   * @return the text color value in HTML format (#RRGGBB)
   */
  @Deprecated
  public String getTitleColor();

  /**
   * Sets the color to use for displaying the portlet title
   *
   * This method is deprecated and is only useful for ECS based Control
   * implementation
   * 
   * @deprecated
   * @param titleColor
   *          the title color value in HTML format (#RRGGBB)
   */
  @Deprecated
  public void setTitleColor(String titleColor);

  /**
   * Returns the width of the managed portlet relative to the size of portlet
   * control. This method is deprecated and is only useful for ECS based Control
   * implementation
   * 
   * @deprecated
   * @return the size value. May be expressed in percentages (eg, "80%")
   */
  @Deprecated
  public String getWidth();

  /**
   * Sets the width of the managed portlet relative to the size of portlet
   * control.
   *
   * This method is deprecated and is only useful for ECS based Control
   * implementation
   * 
   * @deprecated
   * @param width
   *          the width of portlet. Must be a positive, non nul integer
   */
  @Deprecated
  public void setWidth(int width);

  /**
   * Sets the width of the managed portlet relative to the size of portlet
   * control.
   *
   * This method is deprecated and is only useful for ECS based Control
   * implementation
   * 
   * @deprecated
   * @param width
   *          the width of portlet. Must be parseable as a positive, non nul
   *          integer or a percentage
   */
  @Deprecated
  public void setWidth(String width);
}
