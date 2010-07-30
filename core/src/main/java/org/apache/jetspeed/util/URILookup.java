package org.apache.jetspeed.util;

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

//jetspeed support
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;

//turbine
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateLink;


/**
 * <p>
 * URI lookup related functions.
 * </p>
 * 
 * <p>
 *  Functions to get an URI based on a type like info, edit, save
 *  and to identify the type of an URI.
 * </p>
 * 
 * <p>
 * To overwrite the default behaviour, specify one
 * of the following parameters in the JR.p file. URILookup
 * will then return the uri specified in the properties file.
 * </p>
 * <p>
 * all possible property parameters:
 * </p>
 * 
 * <UL>
 * <LI>URILookup.home.uri</LI>
 * <LI>URILookup.home.acceptlogin.uri</LI>
 * <LI>URILookup.home.restore.uri</LI>
 * <LI>URILookup.home.logout.uri</LI>
 * </UL>
 * <UL>
 * <LI>URILookup.info.uri</LI>
 * <LI>URILookup.info.mark.uri</LI>
 * </UL>
 * <UL>
 * <LI>URILookup.login.uri</LI>
 * </UL>
 * <UL>
 * <LI>URILookup.editaccount.uri</LI>
 * <LI>URILookup.editaccount.mark.uri</LI>
 * </UL>
 * <UL>
 * <LI>URILookup.back.uri</LI>
 * </UL>
 * <UL>
 * <LI>URILookup.enrollment.uri</LI>
 * </UL>
 * <UL>
 * <LI>URILookup.customizer.uri</LI>
 * <LI>URILookup.customizer.save.uri</LI>
 * </UL>
 * 
 * @author <A HREF="shesmer@raleigh.ibm.com">Stephan Hesmer</A>
 * @author <A HREF="sgala@apache.org">Santiago Gala</A>
 * @version $Id: URILookup.java,v 1.24 2004/02/23 03:23:42 jford Exp $
 */

public class URILookup 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(URILookup.class.getName());
    /**
     * <P>show Jetspeed Home page<BR>
     * allowed Subtypes:<BR>
     * <UL>
     * <LI>SUBTYPE_NONE</LI>
     * <LI>SUBTYPE_RESTORE</LI>
     * <LI>SUBTYPE_MAXIMIZE</LI>
     * <LI>SUBTYPE_LOGOUT</LI>
     * <LI>SUBTYPE_ACCEPT_LOGIN</LI>
     * </UL>
     */
    public static final int TYPE_HOME               = 0;
    /**
     * <P>show some additional information about the portlet</P>
     * allowed Subtypes:<BR>
     * <UL>
     * <LI>SUBTYPE_NONE</LI>
     * <LI>SUBTYPE_MARK</LI>
     * </UL>
     */
    public static final int TYPE_INFO               = 1;

    /**
     * <P>show the edit page of the account</P>
     * allowed Subtypes:<BR>
     * <UL>
     * <LI>SUBTYPE_NONE</LI>
     * <LI>SUBTYPE_MARK</LI>
     * </UL>
     */
    public static final int TYPE_EDIT_ACCOUNT       = 3;
    /**
     * <P>show portlet customization</P>
     * allowed Subtypes:<BR>
     * <UL>
     * <LI>SUBTYPE_NONE</LI>
     * <LI>SUBTYPE_SAVE</LI>
     * </UL>
     */
    public static final int TYPE_CUSTOMIZE          = 4;
    /**
     * <P>show login screen</P>
     * allowed Subtypes:<BR>
     * <UL>
     * <LI>SUBTYPE_NONE</LI>
     * </UL>
     */
    public static final int TYPE_LOGIN              = 5;
    /**
     * <P>show the marked page<BR>
     * only used in function getURI, not in getURIType</P>
     * allowed Subtypes:<BR>
     * <UL>
     * <LI>SUBTYPE_NONE</LI>
     * </UL>
     */
    public static final int TYPE_BACK               = 6;

    /**
     * <P>creates new account<BR>
     * allowed Subtypes:<BR>
     * <UL>
     * <LI>SUBTYPE_NONE</LI>
     * </UL>
     */
    public static final int TYPE_ENROLLMENT         = 7;

    /**
     * apply no subtype to the url
     */
    public static final int SUBTYPE_NONE            = 0;
    /**
     * restore portlet to default size
     */
    public static final int SUBTYPE_RESTORE         = 1;
    /**
     * show the current portlet maximized<BR>
     * additionally, the current page is marked for restoring
     */
    public static final int SUBTYPE_MAXIMIZE        = 2;
    /**
     * mark the current page before processing portlet
     */
    public static final int SUBTYPE_MARK            = 3;
    /**
     * logs out the user
     */
    public static final int SUBTYPE_LOGOUT          = 4;
    /**
     * trues to login, after entering username and password
     */
    public static final int SUBTYPE_ACCEPT_LOGIN    = 5;
    /**
     * save user settings before processing portlet
     */
    public static final int SUBTYPE_SAVE            = 6;

    /**
     * Gets the URI for the specified type
     * 
     * @param aType    type of the URI
     * @param aSubType subtype of the URI
     * @param rundata  the RunData object
     * @return the URI
     * @exception JetspeedException
     */
    public static String getURI(int aType,
                                int aSubType,
                                RunData rundata)
        throws JetspeedException
    {
        return getURI(aType, 
                      aSubType,
                      null,
                      (String)null,
                      rundata);
    }

    /**
     * Gets the URI of the specified portlet with the specified type
     * 
     * @param aType    type of the URI
     * @param aSubType subtype of the URI
     * @param aPortlet Portlet the URI points to
     * @param rundata  the RunData object
     * @return the URI
     */
    public static String getURI(int aType, 
                                int aSubType, 
                                Portlet aPortlet, 
                                RunData rundata)
        throws JetspeedException
    {
        return getURI(aType,
                      aSubType,
                      null,
                      aPortlet, 
                      rundata);
    }

    /**
     * Gets the URI of the specified portlet with the specified type
     * 
     * @param aType    type of the URI
     * @param aSubType subtype of the URI
     * @param aPortletName Portlet the URI points to
     * @param rundata  the RunData object
     * @return the URI
     */
    public static String getURI(int aType,
                                int aSubType,
                                String aPortletName,
                                RunData rundata)
        throws JetspeedException
    {
        return getURI(aType,
                      aSubType,
                      null,
                      aPortletName,
                      rundata);
    }

    /**
     * Gets the URI of the specified portlet with the specified type and adds
     * given user data.
     * 
     * @param aType    type of the URI
     * @param aSubType subtype of the URI
     * @param userData string which should be added to the URL
     * @param aPortlet Portlet the URI points to
     * @param rundata  the RunData object
     * @return the URI
     */
    public static String getURI(int aType,
                                int aSubType,
                                String userData,
                                Portlet aPortlet,
                                RunData rundata)
        throws JetspeedException
    {
        if (aPortlet!=null) {
            aPortlet = getRealPortlet(aPortlet);
            return getURI(aType,
                          aSubType,
                          userData,
                          aPortlet.getName(),
                          rundata);
        }
        else {
            return getURI(aType,
                          aSubType,
                          userData,
                          (String)null,
                          rundata);
        }
    }

    /**
     * Gets the URI of the specified portlet with the specified type and adds
     * given user data.
     * 
     * @param aType    type of the URI
     * @param aSubType subtype of the URI
     * @param userData string which should be added to the URL
     * @param aPortletName Portlet the URI points to
     * @param rundata  the RunData object
     * @return the URI
     */
    public static String getURI(int aType,
                                int aSubType,
                                String userData,
                                String aPortletName,
                                RunData rundata)
        throws JetspeedException
    {
        String newURI = null;

        String propertiesParameter = "URILookup.";

        TemplateLink uri = new TemplateLink( rundata );

        if (aType==TYPE_HOME)
        {
            propertiesParameter += "home.";
            
            if (aSubType==SUBTYPE_RESTORE)
            {
                propertiesParameter += "restore.";

                newURI = getMarkedPage( rundata );
            }
            else if (aSubType==SUBTYPE_MAXIMIZE)
            {
                propertiesParameter += "maximize.";
                
                if (aPortletName==null)
                {
                    throw new JetspeedException( "A portlet is required to return an URI." );
                }
                uri.setAction( ACTION_MARKPAGE );
                uri.addPathInfo( "portlet", aPortletName );
            }
            else if (aSubType==SUBTYPE_LOGOUT)
            {
                propertiesParameter += "logout.";

                uri.setAction( ACTION_LOGOUT );
            }
            else if (aSubType==SUBTYPE_ACCEPT_LOGIN)
            {
                propertiesParameter += "acceptlogin.";

                uri.setAction( ACTION_ACCEPT_LOGIN );
            }
            else if (aSubType!=SUBTYPE_NONE)
            {
                throw new JetspeedException( "Incorrect Type / Subtype combination." );
            }
        }
        else if (aType==TYPE_INFO)
        {
            propertiesParameter += "info.";
            
            if (aPortletName==null)
            {
                throw new JetspeedException( "A portlet is required to return an URI." );
            }
            
            uri.setPage( SCREEN_INFO );
            uri.addPathInfo( "portlet", aPortletName );

            if (aSubType==SUBTYPE_MARK)
            {
                propertiesParameter += "mark.";

                uri.setAction( ACTION_MARKPAGE );
            }
            else if (aSubType!=SUBTYPE_NONE)
            {
                throw new JetspeedException( "Incorrect Type / Subtype combination." );
            }
        }
        else if (aType==TYPE_EDIT_ACCOUNT)
        {
            propertiesParameter += "editaccount.";
            
            uri.setPage( SCREEN_EDIT_ACCOUNT );

            if (aSubType==SUBTYPE_NONE)
            {
                uri.setAction( ACTION_PREPARE_SCREEN_EDIT_ACCOUNT );
            }
            else if (aSubType==SUBTYPE_MARK)
            {
                propertiesParameter += "mark.";
                
                if (aPortletName==null) 
                {
                    throw new JetspeedException( "A portlet is required to return an URI." );
                }

                // FIX ME: how can we add a prepare action and a mark action at the same time?
                //         But I think this branch is never used anyway. (?)
                uri.setAction( ACTION_MARKPAGE );
                uri.addPathInfo( "portlet", aPortletName );
            }
            else
            {
                throw new JetspeedException( "Incorrect Type / Subtype combination." );
            }
        }
        else if (aType==TYPE_CUSTOMIZE)
        {
            propertiesParameter += "customize.";
            
            uri.setPage( SCREEN_CUSTOMIZE );
            
            if( aPortletName != null )
            {
                uri.addPathInfo( "portlet", aPortletName );
            }
            if (aSubType==SUBTYPE_NONE)
            {
                if (ACTION_CUSTOMIZER!=null) uri.setAction( ACTION_CUSTOMIZER );
            }
            else if (aSubType==SUBTYPE_SAVE)
            {
                propertiesParameter += "save.";
                
                uri.setAction( ACTION_CUSTOMIZER_SAVE );
            }
            else
            {
                throw new JetspeedException( "Incorrect Type / Subtype combination." );
            }
        }
        else if (aType==TYPE_LOGIN)
        {
            propertiesParameter += "login.";
            
            if (aSubType==SUBTYPE_NONE)
            {
                uri.setPage( SCREEN_LOGIN );
            }
            else
            {
                throw new JetspeedException( "Incorrect Type / Subtype combination." );
            }
        }
        else if (aType==TYPE_BACK)
        {
            propertiesParameter += "back.";
            
            if (aSubType==SUBTYPE_NONE)
            {
                newURI = getMarkedPage( rundata );
            }
            else
            {
                throw new JetspeedException( "Incorrect Type / Subtype combination." );
            }
        }
        else if (aType==TYPE_ENROLLMENT)
        {
            propertiesParameter += "enrollment.";
            uri.setPage( SCREEN_NEWACCOUNT );
        }
        else
        {
            throw new JetspeedException( "Incorrect Type / Subtype combination." );
        }

        if (newURI==null)
        {
            newURI = uri.toString();
        }

        propertiesParameter += "uri";
        String propertiesParameterValue = JetspeedResources.getString( propertiesParameter, null );

        if (propertiesParameterValue!=null)
        {
            // found the parameter value, so replace the newURI with this one
            if ( logger.isInfoEnabled() )
            {
                logger.info("URILookup: replaced uri "+newURI+" with "+propertiesParameterValue);
            }
            newURI = propertiesParameterValue;
        }

        if (userData!=null)
        {
            newURI = addURIParameter(newURI, userData);
        }

        // remove sessionid, if exists
        if (newURI.indexOf(";jsessionid")!=-1)
        {
            newURI = newURI.substring(0,newURI.indexOf(";jsessionid"));
        }

        // adds sessionid if necessary
        newURI = rundata.getResponse().encodeURL( newURI );

        // remove starting slash, so that the URI is relative, and the Base-Tag is used
        // Note: if there is no starting slash, the function encodeURL inserts one slash in front of the URI
        if (newURI.startsWith("/"))
        {
            newURI = newURI.substring(1);
        }

        return newURI;
    }

    /**
     * Gets the type of the URI (e.g. TYPE_INFO, TYPE_EDIT).
     * 
     * @param rundata the RunData object
     * @return the type
     */
    public static int getURIType(RunData rundata)
    {
        return getURIType( null, rundata );
    }

    /**
     * Gets the type of the URI (e.g. TYPE_INFO, TYPE_EDIT).
     * The default return value is TYPE_HOME
     * <P>Hint:<BR>
     * Portlets should check for TYPE_EDIT_PORTLET and in any other case render the content</P>
     * 
     * @param aPortlet the associated portlet
     * @param rundata  the RunData object
     * @return the type
     */
    public static int getURIType(Portlet aPortlet, RunData rundata)
    {
        if (aPortlet!=null)
        {
            aPortlet = getRealPortlet(aPortlet);

            if (aPortlet.getName().equals(((JetspeedRunData)rundata).getPortlet()))
            {
                if (rundata.getScreen()!=null)
                {
                    if (rundata.getScreen().equals(SCREEN_INFO)) return TYPE_INFO;
                }
            }
        }
        
        if (rundata.getScreen()!=null)
        {
            if (rundata.getScreen().equals(SCREEN_CUSTOMIZE)) return TYPE_CUSTOMIZE;
            else if (rundata.getScreen().equals(SCREEN_NEWACCOUNT)) return TYPE_ENROLLMENT;
            else if (rundata.getScreen().equals(SCREEN_EDIT_ACCOUNT)) return TYPE_EDIT_ACCOUNT;
            else if (rundata.getScreen().equals(SCREEN_LOGIN)) return TYPE_LOGIN;
        }

        return TYPE_HOME;
    }

    /**
     * <P>Gets the subtype of the URI (e.g. SUBTYPE_SAVE).</P>
     * returns only the values<BR>
     * <UL>
     * <LI>SUBTYPE_NONE</LI>
     * <LI>SUBTYPE_MAXIMIZE</LI>
     * <LI>SUBTYPE_SAVE</LI>
     * </UL>
     * 
     * @param aPortlet the related portlet
     * @param rundata  the RunData object
     * @return the type
     * @exception JetspeedException
     */
    public static int getURISubType(Portlet aPortlet, RunData rundata)
        throws JetspeedException
    {
        if (rundata.getAction()!=null)
        {
            if (rundata.getAction().equals(ACTION_ACCEPT_LOGIN)) return SUBTYPE_ACCEPT_LOGIN;
            else if (rundata.getAction().equals(ACTION_LOGOUT)) return SUBTYPE_LOGOUT;
        }

        String value = (String)rundata.getRequest().getParameter("type");

        if (value!=null)
        {
            if (value.equalsIgnoreCase("save")) return SUBTYPE_SAVE;
        }

        if (aPortlet==null) throw new JetspeedException( "A portlet is required." );
        
        aPortlet = getRealPortlet(aPortlet);

        if (aPortlet.getName().equals(((JetspeedRunData)rundata).getPortlet()))
        {
            if ((rundata.getScreen()==null) || // no screen
                ( SCREEN_HOME.equals(rundata.getScreen())) ) // or Home-screen
            {
                return SUBTYPE_MAXIMIZE;
            }
        }

        return SUBTYPE_NONE;
    }

    /**
     * Gets the user specific data stored in the URI.
     * 
     * @param rundata the RunData object
     * @return the previous added user data
     * @see #getURI
     */
    public static String getURIUserData(RunData rundata)
    {
        return rundata.getParameters().getString("info");
    }

    /**
     * returns the WebApplication base directory.
     * 
     * @param rundata the rundata object
     * @return the URI
     */
    public static String getWebAppBaseDirURI(RunData rundata)
    {
        String ctxtPath = JetspeedResources.getString( JetspeedResources.CONTENT_ROOT_URL_KEY, "");
        // Add port only if it is not default port for protocol
        String port = "";
        if( "http".equals( rundata.getServerScheme() ) &&
            rundata.getServerPort() != 80 ) {
            port += ":" + rundata.getServerPort();
        }
        if( "https".equals( rundata.getServerScheme() ) &&
            rundata.getServerPort() != 443 ) {
            port += ":" + rundata.getServerPort();
        }
        try {
           ctxtPath = rundata.getRequest().getContextPath()+ctxtPath;
        } catch (Exception e) {
            // not servlet 2.2
            logger.error( "Servlet container probably not 2.2", e );
        }
        return rundata.getServerScheme()+"://"+
               rundata.getServerName()+
               port + ctxtPath;
    }

    /**
     * Marks the current URI and stores it internally for later usage.
     *
     * @param rundata the RunData object
     */
    public static void markCurrentPage(RunData rundata)
    {
        javax.servlet.http.HttpSession session = rundata.getSession();
        if (session != null) {
            // delete action, if exists
            String uri = replaceTurbineURIParameter(rundata.getRequest().getRequestURI(), "action", null );
            
            session.setAttribute("URILookup_MarkedPage",uri);
// for servlet api 2.0
//            session.putValue("URILookup_MarkedPage",uri);
        }
    }

    /**
     * Marks the URI and stores it internally for later usage.
     *
     * @param aURI    the URI to store
     * @param rundata the RunData object
     */
    public static void markPage(String aURI, RunData rundata)
    {
        javax.servlet.http.HttpSession session = rundata.getSession();
        if (session != null) {
           // delete action, if exists
           aURI = replaceTurbineURIParameter(aURI, "action", null );

            session.setAttribute("URILookup_MarkedPage",aURI);
// for servlet api 2.0
//            session.putValue("URILookup_MarkedPage",aURI);
        }
    }

    /**
     * Gets the previous marked page as relative url.<br>
     * If no page was marked, the Jetspeed Home page is returned.
     * 
     * @return the marked page URI
     */
    public static String getMarkedPage(RunData rundata)
    {
        return getMarkedPage( rundata, true );
    }

    /**
     * Gets the previous marked page.<br>
     * If no page was marked, the Jetspeed Home page is returned.
     * 
     * @param relative specifies whether the returing URI should be relative
     * @return the marked page URI
     */
    public static String getMarkedPage(RunData rundata, boolean relative)
    {
        javax.servlet.http.HttpSession session = rundata.getSession();
        if (session != null) {
            String markedPage = (String)session.getAttribute("URILookup_MarkedPage");
// for servlet api 2.0
//            String markedPage = (String)session.getValue("URILookup_MarkedPage");
            if ((markedPage!=null) && (relative)) {
                // check if the URL is absolute. If so, than make it relative
                int idx = markedPage.indexOf("://");
                if (idx!=-1) { // found it
                    idx = markedPage.indexOf("/",idx+3); // search the next slash
                    if (idx!=-1) { // this is the slash after host and port
                        idx = markedPage.indexOf("/",idx+1); // search the next slash
                        if (idx!=-1) { // this is the slash after context
                            markedPage = markedPage.substring(idx);
                        }
                    }
                }
            }
            return markedPage;
        }
        return null;
    }

    /**
    <p>
    Given a ParameterParser, get a PortletEntry.  This is used so that when you have a
    URI created from PortletURIManager you can get back the PortletEntry that created
    it originally.
    </p>
    <p>
    Return null if we aren't able to figure out the PortletEntry
    </p>
    */
    public static final PortletEntry getEntry( ParameterParser params ) throws Exception
    {

        String name = params.getString("portlet");

        return (PortletEntry)Registry.getEntry(Registry.PORTLET, name );

    }

    /**
    * <p>
    * Checks that a Portlet is not a PortletControl. If it's a Control returns
    * the non-controlled Portlet entry.
    * </p>
    *
    * @return the portlet
    */
    private static Portlet getRealPortlet( Portlet portlet )
    {

        while (portlet instanceof PortletControl) {
            portlet = ((PortletControl)portlet).getPortlet();
        }

        return portlet;
    }

    /**
     * Replaces a turbine-based parameter in the URI.<BR>
     * /paramater/value/paramater/value/...
     * 
     * @param uri       the URI to modify
     * @param parameter the parameter to be replaced
     * @param value     the value
     * @return the new URI
     */
    private static String replaceTurbineURIParameter( String uri, String parameter, String value)
    {
        int idx = uri.indexOf("/" + parameter + "/");
        if (idx!=-1) {
            int idx2 = uri.indexOf("/",idx+parameter.length()+2);
            if (idx2==-1) // end of string
              idx2 = uri.length();
            uri = uri.substring(0,idx) + uri.substring(idx2);
        }
        if (value!=null) {
            if (!uri.endsWith("/")) uri += "/";
            uri += parameter + "/" + value;
        }
        return uri;
    }

    /**
     * removes all parameters of the URI (after the questionmark)<BR>
     * (i.e. http://localhost/jetspeed?type=save to http://localhost/jetspeed)
     * 
     * @param uri    the URI t be modified
     * @return the URI
     */
    private static String resetURIParameter( String uri )
    {
        if (uri.indexOf("?")!=-1) {
            uri = uri.substring(0,uri.indexOf("?"));
        }
        return uri;
    }

    /**
     * appends the parameter/value pair to the URI
     * 
     * @param uri       the URI to be modified
     * @param parameter the parameter to be added
     * @param value     the parameter value
     * @return the modified URI
     */
    private static String addURIParameter( String uri, String parameter, String value)
    {
        parameter = URIEncoder.encode( parameter );
        value = URIEncoder.encode( value );
        if (uri.indexOf("?")!=-1) {
            int idx = uri.indexOf( parameter + "=", uri.indexOf("?"));
            if (idx!=-1) { // parameter already in URI. remove it
                int idx2 = uri.indexOf("&", idx);
                if (idx2==-1) // end of string
                    idx2 = uri.length();
                uri = uri.substring(0,idx) + uri.substring(idx2);
            }
        }
        return addURIParameter( uri, parameter + "=" + value );
    }

    /**
     * appends the parameter/value pair to the URI
     * 
     * @param uri  the URI to be modified
     * @param data the data to be added (has to be the correct format - paramater=value)    
     * @return the modified URI
     */
    private static String addURIParameter( String uri,
                                           String data)
    {
        if (uri.indexOf("?")!=-1) uri += "&";
        else uri += "?";
        uri += data;
        return uri;
    }
    
    private static final String SCREEN_INFO                        = "Info";
    private static final String SCREEN_HOME                        = JetspeedResources.getString( "template.homepage" );
//    private static final String SCREEN_CUSTOMIZE                   = "Customize";
    private static final String SCREEN_CUSTOMIZE                   = JetspeedResources.getString( "customizer.screen" );
    private static final String SCREEN_LOGIN                       = JetspeedResources.getString( "template.login" );
    private static final String SCREEN_NEWACCOUNT                  = "NewAccount";
    private static final String SCREEN_EDIT_ACCOUNT                = "EditAccount";

    private static final String ACTION_CUSTOMIZER                  = JetspeedResources.getString( "customizer.action" );
    private static final String ACTION_MARKPAGE                    = "MarkRefPage";
    private static final String ACTION_LOGOUT                      = JetspeedResources.getString( "action.logout" );
    private static final String ACTION_ACCEPT_LOGIN                = JetspeedResources.getString( "action.login" );
    private static final String ACTION_CUSTOMIZER_SAVE             = "SavePageConfig";
    private static final String ACTION_PREPARE_SCREEN_EDIT_ACCOUNT = "PrepareScreenEditAccount";
}
