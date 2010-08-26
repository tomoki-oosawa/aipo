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
package org.apache.jetspeed.portal.portlets;

//Element Construction Set
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;

//Jetspeed stuff
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

//turbine
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.util.DynamicURI;

//JDK stuff
import java.util.Hashtable;

/**
 *   A Portlet that displays the contents of a source URL in an IFRAME tag.
 *   portlets.xreg Usage example:
 *     <PRE>
 *           <portlet-entry name="IFrame" hidden="false" type="abstract" application="false">
 *               <classname>org.apache.jetspeed.portal.portlets.IFramePortlet</classname>
 *           </portlet-entry>
 *     </PRE>
 * 
 *  local-portlets.xreg Usage example:
 *     <PRE>
 *           <portlet-entry name="SomeSite" hidden="false" type="ref" parent="IFramePortlet" application="false">
 *             &lt;meta-info&gt;
 *                 &lt;title&gt;SomeSite Info&lt;/title&gt;
 *                 <description>Navigate SomeSite within an IFRAME</description>
 *             &lt;/meta-info&gt;
 *             <parameter name="source" value="http://somesite" hidden="false"/>
 *             <media-type ref="html"/>
 *           </portlet-entry>
 *     </PRE>
 * 
 *     <P>The following parameters are accepted: </P>
 *     <UL>
 *         <LI> source - The target of the IFRAME, where it grabs it's content from. Can use ${webappRoot}.
 *             Default is "http://127.0.0.1" </LI>
 *         <LI> width - The width of the IFRAME, or null to let the browser decide.
 *             Default is null.</LI>
 *         <LI> height - The height of the IFRAME, or null to let the browser decide.
 *             Default is null.</LI>
 *         <LI> scrolling - How to display a scrollbar.
 *             Default is "auto", to let the browser decide.</LI>
 *         <LI> frameborder - Whether or not to display a border around the IFRAME.
 *             Default is 1 (yes).</LI>
 *         <LI> <code>refresh</code> - value in seconds to auto refresh contents of the IFRAME. </LI>
 *         <LI> <code>align</code> - top | bottom | middle | left | <i>right</i> - How to align the IFRAME in relation to surrounding content.</LI>
 *         <LI> <code>marginwidth</code> - size of the top and bottom margin inside the iframe. </LI>
 *         <LI> <code>marginheight</code> - size of the left and right margin inside the iframe.</LI>
 *     </UL>
 * 
 * @created February 23, 2002
 * @author <a href="mailto:wbarnhil@twcny.rr.com">Bill Barnhill</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: IFramePortlet.java,v 1.7 2004/02/23 04:03:34 jford Exp $
 * @see AbstractPortlet
 */

public class IFramePortlet extends AbstractInstancePortlet
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(IFramePortlet.class.getName());
    
    static final String DEFAULT_NOTSUPP_MSG =
            "[Your user agent does not support inline frames or is currently" 
            + " configured not to display frames]";

    static final String NO_SOURCE_MSG = "Please customize source for this IFrame";

    static final String DEFAULT_SOURCE = "http://127.0.0.1";
    static final String DEFAULT_WIDTH = null;
    static final String DEFAULT_HEIGHT = null;
    static final String DEFAULT_SCROLLING = "auto";
    static final String DEFAULT_FRAMEBORDER = "1";

    static final String PARAM_SOURCE = "source";
    static final String PARAM_WIDTH = "width";
    static final String PARAM_HEIGHT = "height";
    static final String PARAM_SCROLLING = "scrolling";
    static final String PARAM_FRAMEBORDER = "frameborder";
    static final String PARAM_NAME = "name";
    static final String PARAM_STYLE = "style";    
    static final String PARAM_MARGINWIDTH = "marginwidth";
    static final String PARAM_MARGINHEIGHT = "marginheight";
    static final String PARAM_REFRESH = "refresh";
    static final String PARAM_ALIGN = "align";
    static final String WEBAPPROOT = "${" + TurbineConstants.WEBAPP_ROOT + "}";

    private String iSource = DEFAULT_SOURCE;
    private String iWidth = DEFAULT_WIDTH;
    private String iHeight = DEFAULT_HEIGHT;
    private String iScrolling = DEFAULT_SCROLLING;
    private String iFrameBorder = DEFAULT_FRAMEBORDER;
    private String iMarginWidth = null;
    private String iMarginHeight = null;
    private String iStyle = null;
    private String iName = null;
    private String iRefresh = null;
    private String iAlign = null;

    /**
     *  Sets the source attribute of the IFramePortlet object
     *
     * @param  source  The new source value
     * @since
     */
    public void setSource(String source)
    {
        if (source != null)
        {
            // Handle replacement variables
            Hashtable parms = new Hashtable();
            if (source.indexOf("${") >= 0) 
            {
                // Add all portlet parms
                parms.putAll(this.getPortletConfig().getInitParameters());

                // Add web app root variable replacement
                try 
                {
                    ServerData sd = new ServerData(TurbineServlet.getServerName(),
                                                Integer.parseInt(TurbineServlet.getServerPort()),
                                                TurbineServlet.getServerScheme(),
                                                TurbineServlet.getContextPath(),
                                                TurbineServlet.getContextPath());
                    DynamicURI uri = new DynamicURI(sd);                
                    parms.put(TurbineConstants.WEBAPP_ROOT, uri.toString() + "/");
                } 
                catch (Exception e) 
                {
                    logger.error("Exception",  e);
                }
                // Add portlet name variable replacement
                parms.put("portlet", this.getName());
            }

            this.iSource = org.apache.jetspeed.util.StringUtils.replaceVars(source, parms);
        }

    }

    /**
     *  Sets the scrolling attribute of the IFramePortlet object
     *
     * @param  scrolling  The new scrolling value
     * @since
     */
    public void setScrolling(String scrolling)
    {
        iScrolling = scrolling;
    }


    /**
     *  Sets the width attribute of the IFramePortlet object
     *
     * @param  width  The new width value
     * @since
     */
    public void setWidth(String width)
    {
        iWidth = width;
    }


    /**
     *  Sets the height attribute of the IFramePortlet object
     *
     * @param  height  The new height value
     * @since
     */
    public void setHeight(String height)
    {
        iHeight = height;
    }


    /**
     *  Sets the frameBorder attribute of the IFramePortlet object
     *
     * @param  frameBorder  The new frameBorder value
     * @since
     */
    public void setFrameBorder(String frameBorder)
    {
        iFrameBorder = frameBorder;
    }

    /**
     *  Sets the width attribute of the IFramePortlet object
     * 
     * @param width  The new width value
     */
    public void setMarginWidth(String width) 
    {

        iMarginWidth = width;
    }

    /**
     *  Sets the marginheight attribute of the IFramePortlet object
     * 
     * @param height The new height value
     */
    public void setMarginHeight(String height) 
    {

        iMarginHeight = height;
    }

    /**
     *  Sets the marginheight attribute of the IFramePortlet object
     * 
     * @param height The new height value
     */
    public void setAlign(String value) 
    {

        iAlign = value;
    }

    /**
     *  Sets the refresh meta tag
     * 
     * @param value in seconds
     */
    public void setRefresh(String value) 
    {

        iRefresh = value;
    }


    /**
     * Sets the style of iframe. Some useful style effects:
     * <UL>
     * <LI>border:5px dashed purple
     * <LI>border:5px dotted red
     * <LI>border:5px double red
     * <LI>border:5px inset red
     * </UL>
     * 
     * @param value
     */
    public void setStyle(String value) 
    {

        iStyle = value;
    }

    /**
     * Sets the name of iframe. This is useful when referencing
     * the iframe as a target from another link.
     * 
     * @param value
     */
    public void setFrameName(String value) 
    {

        iName = value;
    }

    /**
     *  This methods outputs the content of the portlet for a given request.
     *
     * @param  runData  the RunData object for the request
     * @return          the content to be displayed to the user-agent
     */
    public ConcreteElement getContent(RunData runData)
    {

        // Reinitialize if user customized the portlet (this will be useful
        // when portlet preferences include user name and password for authentication or
        // when other exposed iframe attributes are changed)
        if (org.apache.jetspeed.util.PortletSessionState.getPortletConfigChanged(this, runData))
        {
            try {
                this.init();
            }
            catch (PortletException pe)
            {
                logger.error("Exception",  pe);
            }
        }

        StringBuffer text = new StringBuffer();

        if (getSource() == null || getSource().trim().length() == 0)
        {
            text.append(NO_SOURCE_MSG);
            return (new StringElement(text.toString()));
        }

        text.append("<IFRAME ");

        text.append("src = \"" + getSource() + "\" ");
        if (getWidth() != null)
        {
            text.append("width = \"" + getWidth() + "\" ");
        }

        if (getHeight() != null)
        {
            text.append("height = \"" + getHeight() + "\" ");
        }

        if (getFrameName() != null) 
        {
            text.append("name = \"" + getFrameName() + "\" ");
        }

        if (getStyle() != null) 
        {
            text.append("style = \"" + getStyle() + "\" ");
        }

        if (getMarginWidth() != null) 
        {
            text.append("marginwidth = \"" + getMarginWidth() + "\" ");
        }

        if (getMarginHeight() != null) 
        {
            text.append("marginheight = \"" + getMarginHeight() + "\" ");
        }

        if (getAlign() != null) 
        {
            text.append("align = \"" + getAlign() + "\" ");
        }

        text.append("scrolling = \"" + getScrolling() + "\" ");
        text.append("frameborder = \"" + getFrameBorder() + "\" ");
        text.append(">");

        text.append("</IFRAME>");
        return (new StringElement(text.toString()));
    }


    /**
     *  Gets the source attribute of the IFramePortlet object
     *
     * @return    The source value
     */
    public String getSource()
    {
        return iSource;
    }


    /**
     *  Gets the scrolling attribute of the IFramePortlet object
     *
     * @return    The scrolling value
     */
    public String getScrolling()
    {
        return iScrolling;
    }


    /**
     *  Gets the width attribute of the IFramePortlet object
     *
     * @return    The width value
     */
    public String getWidth()
    {
        return iWidth;
    }


    /**
     *  Gets the height attribute of the IFramePortlet object
     *
     * @return    The height value
     */
    public String getHeight()
    {
        return iHeight;
    }


    /**
     *  Gets whether to display a border around the IFRAME. "1" == yes.
     *
     * @return    The frameBorder value
     */
    public String getFrameBorder()
    {
        String trueValues = "1,yes,true";
        if (iFrameBorder != null && trueValues.indexOf(iFrameBorder) >= 0)
        {
            return "1";
        }
        return "0";
    }


    /**
     *  Gets the message displayed when IFRAME is not supported
     *  This includes when Frames are turned off.
     *
     * @todo        This should be localized
     * @return    The notSupportedMsg value
     */
    public String getNotSupportedMsg()
    {
        return DEFAULT_NOTSUPP_MSG;
    }

    /**
     *  Gets the aling attribute of the IFramePortlet object
     * 
     * @return The marginheight value
     */
    public String getAlign() 
    {

        return iAlign;
    }

    /**
     *  Gets iframe style
     * 
     * @return The style value
     */
    public String getStyle() 
    {

        return iStyle;
    }

    /**
     *  Gets iframe name
     * 
     * @return The name value
     */
    public String getFrameName() 
    {

        return iName;
    }

    /**
     *  Gets iframe refresh
     * 
     * @return The refresh value
     */
    public String getRefresh() 
    {

        return iRefresh;
    }

    /**
     *  Gets the marginheight attribute of the IFramePortlet object
     * 
     * @return The marginheight value
     */
    public String getMarginHeight() 
    {

        return iMarginHeight;
    }

    /**
     *  Gets the marginwidth attribute of the IFramePortlet object
     * 
     * @return The marginwidth value
     */
    public String getMarginWidth() 
    {

        return iMarginWidth;
    }

    /**
     *  Initialize this portlet by setting inst. vars from InitParamaters.
     *
     * @throws  PortletException  Initialization failed
     */
    public void init() throws PortletException
    {
        // first make sure we propagate init
        super.init();

        try
        {
            PortletConfig config = this.getPortletConfig();
            String param = null;

            param = config.getInitParameter(PARAM_SOURCE);
            if (param != null)
            {
                setSource(param);
            }

            param = config.getInitParameter(PARAM_WIDTH);
            if (param != null)
            {
                setWidth(param);
            }

            param = config.getInitParameter(PARAM_HEIGHT);
            if (param != null)
            {
                setHeight(param);
            }

            param = config.getInitParameter(PARAM_SCROLLING);
            if (param != null)
            {
                setScrolling(param);
            }

            param = config.getInitParameter(PARAM_FRAMEBORDER);
            if (param != null)
            {
                setFrameBorder(param);
            }

            param = config.getInitParameter(PARAM_STYLE);
            if (param != null) 
            {
                setStyle(param);
            }

            param = config.getInitParameter(PARAM_NAME);
            if (param != null) 
            {
                setFrameName(param);
            }

            param = config.getInitParameter(PARAM_REFRESH);
            if (param != null) 
            {
                setRefresh(param);
            }

            param = config.getInitParameter(PARAM_MARGINWIDTH);
            if (param != null) 
            {
                setMarginWidth(param);
            }

            param = config.getInitParameter(PARAM_MARGINHEIGHT);
            if (param != null) 
            {
                setMarginHeight(param);
            }

            param = config.getInitParameter(PARAM_ALIGN);
            if (param != null) 
            {
                setAlign(param);
            }

        }
        catch (Exception e)
        {
            logger.error("Exception in init()", e);
            throw new PortletException(e.getMessage());
        }
    }
}


