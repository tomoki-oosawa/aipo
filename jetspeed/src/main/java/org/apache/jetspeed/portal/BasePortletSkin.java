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

import java.io.File;
import java.util.StringTokenizer;

import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.turbine.services.servlet.TurbineServlet;

/**
 * This default implementation of PortletSkin stores every property
 * as a Map of text properties
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: BasePortletSkin.java,v 1.7 2004/02/23 04:05:35 jford Exp $
 */
public class BasePortletSkin extends java.util.HashMap implements PortletSkin
{

    public String name = null;
    private CapabilityMap cm;
    private static final String[] VALID_EXTENSIONS = new String[] { "gif", "jpg", "png" };

    /**
     * Returns the name of this color scheme
     * @return the color scheme name
     */
    public String getName()
    {
        return this.name;
    }

    /** Sets the name of this Skin
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the color to use for displaying the portlet text
     * @return the text color value in HTML format (#RRGGBB)
     */
    public String getTextColor()
    {
        return (String)get(TEXT_COLOR);
    }

    /**
     * Sets the color to use for displaying the portlet text
     * @param color the text color value in HTML format (#RRGGBB)
     */
    public void setTextColor(String color)
    {
        if (color!=null)
        {
            put(TEXT_COLOR,color);
        }
    }

    /**
     * Returns the color to use for displaying the portlet background
     * @return the text color value in HTML format (#RRGGBB)
     */
    public String getBackgroundColor()
    {
        return (String)get(BACKGROUND_COLOR);
    }

    /**
     * Sets the color to use for displaying the portlet background
     * @param backgroundColor the background color value in HTML format (#RRGGBB)
     */
    public void setBackgroundColor(String color)
    {
        if (color!=null)
        {
            put(BACKGROUND_COLOR,color);
        }
    }


    /**
     * Returns the color to use for displaying the portlet title text
     * @return the text color value in HTML format (#RRGGBB)
     */
    public String getTitleTextColor()
    {
        return (String)get(TITLE_TEXT_COLOR);
    }


    /**
     * Sets the color to use for displaying the portlet title text
     * @param titleColor the title color value in HTML format (#RRGGBB)
     */
    public void setTitleTextColor(String color)
    {
        if (color!=null)
        {
            put(TITLE_TEXT_COLOR,color);
        }
    }

    /**
     * Returns the color to use for displaying the portlet title background
     * @return the background color value in HTML format (#RRGGBB)
     */
    public String getTitleBackgroundColor()
    {
        return (String)get(TITLE_BACKGROUND_COLOR);
    }

    /**
     * Sets the color to use for displaying the portlet title background
     * @param titleColor the title color value in HTML format (#RRGGBB)
     */
    public void setTitleBackgroundColor(String color)
    {
        if (color!=null)
        {
            put(TITLE_BACKGROUND_COLOR,color);
        }
    }

    /**
     * Returns the color to use for displaying an highlighted text
     * @return the text color value in HTML format (#RRGGBB)
     */
    public String getHighlightTextColor()
    {
        return (String)get(HIGHLIGHT_TEXT_COLOR);
    }

    /**
     * Sets the color to use for displaying an highlighted text
     * @param titleColor a color value in HTML format (#RRGGBB)
     */
    public void setHighlightTextColor(String color)
    {
        if (color!=null)
        {
            put(HIGHLIGHT_TEXT_COLOR,color);
        }
    }

    /**
     * Returns the color to use for displaying an highlighted background
     * @return the background color value in HTML format (#RRGGBB)
     */
    public String getHighlightBackgroundColor()
    {
        return (String)get(HIGHLIGHT_BACKGROUND_COLOR);
    }

    /**
     * Sets the color to use for displaying an highlighted background
     * @param titleColor the title color value in HTML format (#RRGGBB)
     */
    public void setHighlightBackgroundColor(String color)
    {
        if (color!=null)
        {
            put(HIGHLIGHT_BACKGROUND_COLOR,color);
        }
    }

    /**
     * Returns the CSS class to use for the portlet overall
     * @return the CSS class to use (PortletStyleClass)
     */
    public String getPortletStyleClass()
    {
        return (String)get(PORTLET_STYLE_CLASS);
    }

    /**
     * Sets the CSS class to use for the portlet overall
     * @param portletStyleClass the new class to be used
     */
    public void setPortletStyleClass(String portletStyleClass)
    {
        if (portletStyleClass!=null)
        {
            put(PORTLET_STYLE_CLASS,portletStyleClass);
        }
    }

    /**
     * Returns the CSS class to use for the portlet title
     * @return the CSS class to use (TitleStyleClass)
     */
    public String getTitleStyleClass()
    {
        return (String)get(TITLE_STYLE_CLASS);
    }

    /**
     * Sets the CSS class to use for the portlet title
     * @param titleStyleClass the new class to be used
     */
    public void setTitleStyleClass(String titleStyleClass)
    {
        if (titleStyleClass!=null)
        {
            put(TITLE_STYLE_CLASS,titleStyleClass);
        }
    }

    /**
     * Returns the CSS class to use for the portlet content
     * @return the CSS class to use (ContentStyleClass)
     */
    public String getContentStyleClass()
    {
        return (String)get(CONTENT_STYLE_CLASS);
    }

    /**
     * Sets the CSS class to use for the portlet content
     * @param contentStyleClass the new class to be used
     */
    public void setContentStyleClass(String contentStyleClass)
    {
        if (contentStyleClass!=null)
        {
            put(CONTENT_STYLE_CLASS,contentStyleClass);
        }
    }

    /**
     * Returns the CSS class to use overall for the tabbed control
     * @return the CSS class to use (TabStyleClass)
     */
    public String getTabStyleClass()
    {
        return (String)get(TAB_STYLE_CLASS);
    }

    /**
     * Sets the CSS class to use for overall for the tabbed control
     * @param tabStyleClass the new class to be used
     */
    public void setTabStyleClass(String tabStyleClass)
    {
        if (tabStyleClass!=null)
        {
            put(TAB_STYLE_CLASS,tabStyleClass);
        }
    }

    /**
     * Returns the CSS class to use on the title of the tabbed control
     * @return the CSS class to use (TabTitleStyleClass)
     */
    public String getTabTitleStyleClass()
    {
        return (String)get(TAB_TITLE_STYLE_CLASS);
    }

    /**
     * Sets the CSS class to use on the title of the tabbed control
     * @param tabTitleStyleClass the new class to be used
     */
    public void setTabTitleStyleClass(String tabTitleStyleClass)
    {
        if (tabTitleStyleClass!=null)
        {
            put(TAB_TITLE_STYLE_CLASS,tabTitleStyleClass);
        }
    }

    /**
     * Returns the CSS class to use on the control of the tabbed control
     * @return the CSS class to use (TabContentStyleClass)
     */
    public String getTabContentStyleClass()
    {
        return (String)get(TAB_CONTENT_STYLE_CLASS);
    }

    /**
     * Sets the CSS class to use on the control of the tabbed control
     * @param tabContentStyleClass the new class to be used
     */
    public void setTabContentStyleClass(String tabContentStyleClass)
    {
        if (tabContentStyleClass!=null)
        {
            put(TAB_CONTENT_STYLE_CLASS,tabContentStyleClass);
        }
    }

    /**
     * Returns the CSS class to use on the control of the Highlighted title tab or menu item
     * @return the CSS class to use (HighlightTitleStyleClass)
     */
    public String getHighlightTitleStyleClass()
    {
        return (String)get(HIGHLIGHT_TITLE_STYLE_CLASS);
    }

    /**
     * Sets the CSS class to use on the control of the Highlighted title tab or menu item
     * @param highlightTitleStyleClass the new class to be used
     */
    public void setHighlightTitleStyleClass(String highlightTitleStyleClass)
    {
        if (highlightTitleStyleClass!=null)
        {
            put(HIGHLIGHT_TITLE_STYLE_CLASS, highlightTitleStyleClass);
        }
    }

    /**
     * Returns the CSS class to use for the controller overall
     * @return the CSS class to use (ControllerStyleClass)
     */
    public String getControllerStyleClass()
    {
        return (String)get(CONTROLLER_STYLE_CLASS);
    }

    /**
     * Sets the CSS class to use for the controller overall
     * @param controllerStyleClass the new class to be used
     */
    public void setControllerStyleClass(String controllerStyleClass)
    {
        if (controllerStyleClass!=null)
        {
            put(CONTROLLER_STYLE_CLASS,controllerStyleClass);
        }
    }

    /**
     * Returns the CSS class to use for the global skin rendering
     * @see org.apache.jetspeed.portal.PortletSkin#getPortletSkinClass()
     */
    public String getPortletSkinClass()
    {
        return (String)get(PORTLET_SKIN_CLASS);
    }

    /**
     * Sets the CSS class to use for the global skin rendering
     * @param portletSkinClass the new class to be used
     */
    public void setPortletSkinClass(String portletSkinClass)
    {
        if (portletSkinClass!=null)
        {
            put(PORTLET_SKIN_CLASS,portletSkinClass);
        }
    }

    /**
     * @see org.apache.jetspeed.portal.PortletSkin#getImage(String, String)
     */
    public String getImage(String name, String dftPath)
    {

        if (containsKey("image-" + name))
        {
            return buildMediaTypeSpecificPath((String) get("image-" + name));
        }

        String path = imageDiscovery(name);
        if (path != null)
        {
            return path;
        }
        else
        {
            return dftPath;
        }
    }


    /**
    * This allows the PortalToolKit to make the PortletSkin aware
    * of the current user-agents's capabilities
    * @param CapabilityMap cm Current capaibilities of the user-agent
    * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
    */
    public void setCapabilityMap(CapabilityMap cm)
    {
        this.cm = cm;
    }

    /**
     * builds a media type specific path for the relative path provided
     */
    private String buildMediaTypeSpecificPath(String relativePath)
    {
        String path = "images/" + cm.getPreferredMediaType() + "/skins/" + relativePath;
        return path;
    }

    /**
     * builds a media type specific path using this skin's name.
     */
    private String buildMediaTypeSpecificPath()
    {
        return buildMediaTypeSpecificPath(name);
    }

    private  String imageDiscovery(String imageName)
    {
        String imagePathes = (String) get("image.paths");
        boolean hasExtension = hasImageExtension(imageName);
        String fullPath = null;
        if (imagePathes != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(imagePathes, ",");
            while (tokenizer.hasMoreTokens())
            {
                fullPath =
                    buildValidImage(
                        buildMediaTypeSpecificPath(tokenizer.nextToken()),
                        imageName,
                        hasExtension);
                if (fullPath != null)
                {
                    return fullPath;
                }
            }
        }

        if (fullPath == null)
        {
            String skinBasedPath = buildMediaTypeSpecificPath();
            fullPath = buildValidImage(skinBasedPath, imageName, hasExtension);
        }
        return fullPath;
    }

    /**
     * Does the path contain a valid image extension?
     */
    private boolean hasImageExtension(String path)
    {
        return (path.indexOf(".gif") > -1)
            || (path.indexOf(".jpg") > -1)
            || (path.indexOf(".png") > -1);
    }

    /**
     * makes every attempt to locate a valid image based on the combination
     * of an absoulte path and relative path or name.  The relPath may pr may not
     * contain a valid image extension (.gif, .png, .jpg).
     */
    private String buildValidImage(String absPath, String relPath, boolean hasExtension)
    {
        String path = null;

        if (hasExtension)
        {
            path = absPath + "/" + relPath;
            if (fileExists(path))
            {
                return path;
            }
        }
        else
        {
            for (int i = 0; i < VALID_EXTENSIONS.length; i++)
            {
                path = absPath + "/" + relPath + "." + VALID_EXTENSIONS[i];
                if (fileExists(path))
                {
                    return path;
                }
            }
        }

        return null;
    }

    /**
     * Does this <code>path</code> exist in the current file system.
     */
    private boolean fileExists(String path)
    {
        File testPath = null;
        testPath = new File(TurbineServlet.getRealPath(path));
        if (testPath.exists())
        {
            testPath = null;
            return true;
        }
        else
        {
            testPath = null;
            return false;
        }
    }

}
