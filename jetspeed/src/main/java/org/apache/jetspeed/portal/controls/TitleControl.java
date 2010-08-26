package org.apache.jetspeed.portal.controls;

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


//ECS stuff
import org.apache.ecs.html.IMG;
import org.apache.ecs.html.B;
import org.apache.ecs.html.Table;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;


//jetspeed support
import org.apache.jetspeed.util.JetspeedException;
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.util.URILookup;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.CapabilityMapFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

//turbine
import org.apache.turbine.util.ContentURI;
import org.apache.turbine.util.RunData;

//java imports
import java.util.Vector;

/**
 <p>
 This control renders the title of a portlet for MimeTyps WML and HTML.
 For WML only the title (represented as a link) is returned. In case that
 the device requests html the title will be rendered within a titlebar (with
 buttons for editing or maximizing the portlet).
 </p>
 @author <a href="mailto:sasalda@de.ibm.com">Sascha Alda</a>
 @author <a href="mailto:stephan.hesmer@de.ibm.com">Stephan Hesmer</a>     
 @author <a href="mailto:sgala@apache.org">Santiago Gala</a>     
 @version $Id: TitleControl.java,v 1.14 2004/02/23 03:25:35 jford Exp $
*/
public class TitleControl extends AbstractPortletControl {
                
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(TitleControl.class.getName());    
    
    /**
       Method checks, which MimeType is requested. According to this MimeTyp, the
       appropriate method is invoked (getWMLContent() or getHTMLContent() ).
       @param rundata  RunData object from Turbine.
       @return ConcreteElement object, including the complete ECS code for rendering
       the page.
    */              
    public ConcreteElement getContent( RunData rundata ) {
        CapabilityMap cm = CapabilityMapFactory.getCapabilityMap( rundata );
        if ( cm.getPreferredType().equals( MimeType.HTML ) ) {
            return getHTMLContent( rundata );
        }
        if ( cm.getPreferredType().equals( MimeType.WML ) ) {
            return getWMLContent( rundata );
        }
        logger.error("The Given MIME-Type is not supportet for this control");
        return null;
    }


    /**
       Method returns content for html, in case that the requested MimeTyp is html.
       @param rundata  RunData object from Turbine.
       @return ConcreteElement object, including the complete ECS code for rendering
       the html page.
    */        
    public ConcreteElement getHTMLContent( RunData rundata ) {
        //embed this here
        ElementContainer base = new ElementContainer();

        //the overall portlet...
        Table t = new Table()
            .setBgColor( this.getColor() )
            .setBorder(0)
            .setCellPadding(1)
            .setCellSpacing(0)
            .setWidth( getWidth() )
            .setAlign( "center" );


        ConcreteElement[] options = this.getPortletOptions( rundata );

        TR finalTitle = new TR()
            .setBgColor( this.getTitleColor() )
            .addElement( new TD()
                .setBgColor( this.getTitleColor() )
                .setNoWrap( true )
                .setWidth("100%")
                .setVAlign("middle")
                .addElement( new B()
                    .addElement( getPortlet().getTitle() )
                    .addElement("  ") ) );

        if ( options.length > 0 ) {

            ElementContainer alloptions = new ElementContainer();
            for (int i = 0; i < options.length; ++i) {
                alloptions.addElement( options[i] );
            }

            finalTitle.addElement( new TD()
                .setBgColor( this.getTitleColor() )
                .setNoWrap( true )
                .setAlign("right")
                .setVAlign("middle")
                .addElement( alloptions ) );

        }



        t.addElement( finalTitle );

        base.addElement( t );

        return base;
    }
         
    /**
       Method returns content for WML, in case that the requested MimeTyp is WML.
       @param rundata  RunData object from Turbine.
       @return ConcreteElement object, including the complete ECS code for rendering
       the html page.
    */   
    public ConcreteElement getWMLContent( RunData rundata ) {
        ElementContainer ec = new ElementContainer();
        try {
            ec.addElement(new org.apache.ecs.wml.P()
                .addElement(
                            new org.apache.ecs.wml.A(
                                                     URILookup.getURI( URILookup.TYPE_HOME, 
                                                                       URILookup.SUBTYPE_MAXIMIZE, 
                                                                       getName(),
                                                                       rundata ) )
                                .addElement( getTitle() ) ) );
        }
        catch (JetspeedException e) {
            logger.error("Exception", e);
        }
        return ec;
    }

                
    /**
       Method returns the title of the portlet, which is placed within this control.
       @return String object, representing the portlet's title.
    */      
    public String getTitle(){
        return getPortlet().getTitle();
    }

    /**
       Method checks whether the requested MimeTyp is supported by this control.
       Moreover, it checks, if the included portlet fits the given MimeTyp as well.
       Thus, the method returns true, iff both the control and the portlet(set) support
       the requested MimeType. Otherwise false is returned.
       @param mimeType   MimeType object describing the requested MimeTyp.
       @return Boolean true if MimeTyp is supported, false if not.
    */
    public boolean supportsType( MimeType mimeType ) {
        if ( (!MimeType.HTML.equals( mimeType )) &&
             (!MimeType.WML.equals( mimeType )) ){
            return false;
        }
        // Call of the same method of control's portlet
        return getPortlet().supportsType( mimeType );   
    }

    /**
       Get the options for this portlet.
    */
    private ConcreteElement[] getPortletOptions( RunData rundata ) {

        Vector v = new Vector();
        ContentURI content = new ContentURI( rundata );

        int type = URILookup.getURIType(this.getPortlet(),
                                        rundata);
        int subtype = URILookup.SUBTYPE_NONE;
        try {
            subtype = URILookup.getURISubType(this.getPortlet(),
                                              rundata);
        }
        catch (JetspeedException e) {
            logger.error("Exception", e);
        }

        if ( type != URILookup.TYPE_EDIT_ACCOUNT) {
            if ( ( rundata.getUser() != null ) &&
                 ( rundata.getUser().hasLoggedIn()) ) {
                if ( this.getPortlet().getAllowEdit( rundata ) ) {
                    if (type!=URILookup.TYPE_INFO) {
                        try {
                            org.apache.ecs.html.A edit = 
                                new org.apache.ecs.html.A( 
                                                          URILookup.getURI( URILookup.TYPE_INFO,
                                                                            URILookup.SUBTYPE_MARK,
                                                                            this.getPortlet(),
                                                                            rundata ) )
                                    .addElement( new IMG( content.getURI( JetspeedResources.INFO_IMAGE ) )
                                        .setBorder( 0 ) );

                            v.addElement( edit );
                        }
                        catch (JetspeedException e) {
                            logger.error("Exception", e);
                        }
                    }
                }
                
            }

            if ( this.getPortlet().getAllowMaximize( rundata ) ) {
                try {
                    if ( subtype != URILookup.SUBTYPE_MAXIMIZE ) {
                        org.apache.ecs.html.A max = 
                            new org.apache.ecs.html.A( 
                                                      URILookup.getURI( URILookup.TYPE_HOME,
                                                                        URILookup.SUBTYPE_MAXIMIZE,
                                                                        this.getPortlet(),
                                                                        rundata ) )
                                .addElement( new IMG( content.getURI( JetspeedResources.MAX_IMAGE ) )
                                    .setBorder( 0 ) );

                        v.addElement( max );
                    }
                }
                catch (JetspeedException e) {
                    logger.error("Exception", e);
                }
            }
        }

        ConcreteElement[] elements = new ConcreteElement[v.size()];
        v.copyInto(elements);
        return elements;
    }


                
}
