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

import org.apache.jetspeed.capability.CapabilityMap;

/**
 * The PortletSkin defines the color scheme to use for displaying a
 * specified portlet (and associated control)
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: PortletSkin.java,v 1.7 2004/02/23 04:05:35 jford Exp $
 */
public interface PortletSkin extends java.util.Map
{
    public static final String TEXT_COLOR = "text-color";
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String TITLE_TEXT_COLOR = "title-text-color";
    public static final String TITLE_BACKGROUND_COLOR = "title-background-color";
    public static final String HIGHLIGHT_TEXT_COLOR = "highlight-text-color";
    public static final String HIGHLIGHT_BACKGROUND_COLOR = "highlight-background-color";
    public static final String CONTROLLER_STYLE_CLASS = "controller-style-class";
    public static final String PORTLET_STYLE_CLASS = "portlet-style-class";
    public static final String TITLE_STYLE_CLASS = "title-style-class";
    public static final String CONTENT_STYLE_CLASS = "content-style-class";
    public static final String HIGHLIGHT_TITLE_STYLE_CLASS = "highlight-title-style-class";
    public static final String TAB_STYLE_CLASS = "tab-style-class";
    public static final String TAB_TITLE_STYLE_CLASS = "tab-title-style-class";
    public static final String TAB_CONTENT_STYLE_CLASS = "tab-content-style-class";
    public static final String PORTLET_SKIN_CLASS = "portlet-skin-class";

    /**
     * Returns the name of this color scheme
     * @return the color scheme name
     */
    String getName();

    /**
     * Returns the color to use for displaying the portlet text
     * @return the text color value in HTML format (#RRGGBB)
     */
    String getTextColor();

    /**
     * Sets the color to use for displaying the portlet text
     * @param color the text color value in HTML format (#RRGGBB)
     */
    void setTextColor(String color);

    /**
     * Returns the color to use for displaying the portlet background
     * @return the text color value in HTML format (#RRGGBB)
     */
    String getBackgroundColor();

    /**
     * Sets the color to use for displaying the portlet background
     * @param backgroundColor the background color value in HTML format (#RRGGBB)
     */
    void setBackgroundColor(String backgroundColor);

    /**
     * Returns the color to use for displaying the portlet title text
     * @return the text color value in HTML format (#RRGGBB)
     */
    String getTitleTextColor();

    /**
     * Sets the color to use for displaying the portlet title text
     * @param titleColor the title color value in HTML format (#RRGGBB)
     */
    void setTitleTextColor(String titleColor);

    /**
     * Returns the color to use for displaying the portlet title background
     * @return the background color value in HTML format (#RRGGBB)
     */
    String getTitleBackgroundColor();

    /**
     * Sets the color to use for displaying the portlet title background
     * @param titleColor the title color value in HTML format (#RRGGBB)
     */
    void setTitleBackgroundColor(String titleColor);

    /**
     * Returns the color to use for displaying an highlighted text
     * @return the text color value in HTML format (#RRGGBB)
     */
    String getHighlightTextColor();

    /**
     * Sets the color to use for displaying an highlighted text
     * @param titleColor a color value in HTML format (#RRGGBB)
     */
    void setHighlightTextColor(String titleColor);

    /**
     * Returns the color to use for displaying an highlighted background
     * @return the background color value in HTML format (#RRGGBB)
     */
    String getHighlightBackgroundColor();

    /**
     * Sets the color to use for displaying an highlighted background
     * @param titleColor the title color value in HTML format (#RRGGBB)
     */
    void setHighlightBackgroundColor(String titleColor);

    /**
     * Returns the CSS class to use for the portlet overall
     * @return the CSS class to use (PortletStyleClass)
     */
    String getPortletStyleClass();

    /**
     * Sets the CSS class to use for the portlet overall
     * @param portletStyleClass the new class to be used
     */
    void setPortletStyleClass(String portletStyleClass);

    /**
     * Returns the CSS class to use for the portlet title
     * @return the CSS class to use (TitleStyleClass)
     */
    String getTitleStyleClass();

    /**
     * Sets the CSS class to use for the portlet title
     * @param titleStyleClass the new class to be used
     */
    void setTitleStyleClass(String titleStyleClass);

    /**
     * Returns the CSS class to use for the portlet content
     * @return the CSS class to use (ContentStyleClass)
     */
    String getContentStyleClass();

    /**
     * Sets the CSS class to use for the portlet content
     * @param contentStyleClass the new class to be used
     */
     void setContentStyleClass(String contentStyleClass);

    /**
     * Returns the CSS class to use overall for the tabbed control
     * @return the CSS class to use (TabStyleClass)
     */
    String getTabStyleClass();

    /**
     * Sets the CSS class to use for overall for the tabbed control
     * @param tabStyleClass the new class to be used
     */
    void setTabStyleClass(String tabStyleClass);

    /**
     * Returns the CSS class to use on the title of the tabbed control
     * @return the CSS class to use (TabTitleStyleClass)
     */
    String getTabTitleStyleClass();

    /**
     * Sets the CSS class to use on the title of the tabbed control
     * @param tabTitleStyleClass the new class to be used
     */
    void setTabTitleStyleClass(String tabTitleStyleClass);

    /**
     * Returns the CSS class to use on the control of the tabbed control
     * @return the CSS class to use (TabContentStyleClass)
     */
    String getTabContentStyleClass();

    /**
     * Sets the CSS class to use on the control of the tabbed control
     * @param tabContentStyleClass the new class to be used
     */
    void setTabContentStyleClass(String tabContentStyleClass);

    /**
     * Returns the CSS class to use on the control of the Highlighted titles on tab or menu item
     * @return the CSS class to use (HighlightTitleStyleClass)
     */
    String getHighlightTitleStyleClass();

    /**
     * Sets the CSS class to use on the control of the Highlighted titles tab or menu item
     * @param highlightTabStyleClass the new class to be used
     */
    void setHighlightTitleStyleClass(String highlightTitleStyleClass);

    /**
     * Returns the CSS class to use for the Controller overall
     * @return the CSS class to use (ControllerStyleClass)
     */
    String getControllerStyleClass();

    /**
     * Sets the CSS class to use for the controller overall
     * @param controllerStyleClass the new class to be used
     */
    void setControllerStyleClass(String controllerStyleClass);

    /**
     * Returns a named image from this skin.  The skin property
     * must be prefixed with "image-" within the registry.
     * @param String name name of image to retreive.
     *  <strong>DO NOT PREFIX WITH "image-"</strong>
     * as getImage will do this for you.
     * @param String dftPath realtive path to a default image if
     * the named one is not in the registry.
     * @return String relative path to the image
     * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
     */
    String getImage(String name, String dftPath);

   /**
    * This allows the PortalToolKit to make the PortletSkin aware
    * of the current user-agents's capabilities
    * @param CapabilityMap cm Current capaibilities of the user-agent
    * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
    */
    void setCapabilityMap(CapabilityMap cm);

    /**
     * This returns the class to use for the entire portlet, based
     * on the cascading :
     * portlet has 9 possible class to be used in a skin :
     *                             ________________
     * - title   (+ left/right)   |__|__________|__|
     *                            |  |          |  |
     *                            |  |          |  |
     * - content (+ left/right)   |  |          |  |
     *                            |  |          |  |
     *                            |__|__________|__|
     * - bottom  (+ left/right)   |__|__________|__|
     *
     * using cascading, we can change all of those class with one parent class
     * -> this is how PortletSkinClass is used
     */
     String getPortletSkinClass();

}
