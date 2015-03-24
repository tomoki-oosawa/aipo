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

//standard java stuff
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;

//Element Construction Set
import org.apache.jetspeed.util.JetspeedClearElement;

//standard Jetspeed stuff
import org.apache.jetspeed.util.SimpleTransform;
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.xml.JetspeedXMLEntityResolver;

//JAXP support
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//XML stuff
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
Portlet to change RDF Site Summary into a portlet format for HTML presentation.

@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@author <A HREF="mailto:sgala@hisitech.com">Santiago Gala</A>
@version $Id: RSSPortlet.java,v 1.52 2004/02/23 04:03:33 jford Exp $
*/
public class RSSPortlet extends FileWatchPortlet 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(RSSPortlet.class.getName());    
    
    public final static String ERROR_NOT_VALID = "This does not appear to be an RSS document";

    /**
    The
    */
    private Item[]              items = new Item[0];

    /**
    @author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
    @version $Id: RSSPortlet.java,v 1.52 2004/02/23 04:03:33 jford Exp $
    */
    public void init( ) throws PortletException {

        DocumentBuilder parser = null;
        Document document = null;
        String url = null;

        try {
            final DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
            //Have it non-validating
            docfactory.setValidating(false);
            parser= docfactory.newDocumentBuilder();
            //SGP Changing Resolver to enable Reading through cache
            parser.setEntityResolver(new JetspeedXMLEntityResolver() );

            url = this.getPortletConfig().getURL();

            String content = JetspeedDiskCache.getInstance().getEntry( url ).getData();
            InputSource is = new InputSource( this.cleanse( content ) );

            //SGP Should make no difference ...
            is.setEncoding( "UTF8" );
            is.setSystemId( url );

            //parser.setFeature( "http://apache.org/xml/features/allow-java-encodings",
            //                   true );
            document = parser.parse( is );

        } catch ( Throwable t )
        {

            String message = "RSSPortlet:  Couldn't parse out XML document -> " +
                              url;

            logger.error( message, t );
            throw new PortletException( t.getMessage() );

        }

        //SGP giving NullPointer
        try {
            //now that we have the document set the items for this

            this.setItems( this.parseItems( document ) );

            String title = null;
            String description = null;

            //this a hack until DOM2 namespace support becomes better in Xerces.
            Node root = document.getFirstChild();
            //now find the channel node.
            Node channel = null;

            NodeList list = document.getElementsByTagName( "channel" );

            if ( list.getLength() != 1 ) {
                throw new PortletException( ERROR_NOT_VALID );
        }

            channel = list.item( 0 );

            Node tn = getNode( channel, "title" );

            if ( tn == null ) {
                throw new PortletException( ERROR_NOT_VALID );
            } else {
                title = tn.getFirstChild().getNodeValue();
            }

            Node dn = getNode( channel, "description" );

            if ( dn != null ) {
                description = dn.getFirstChild().getNodeValue();
            }

            this.setTitle( title );
            this.setDescription( description );


            //now that we have the DOM we should be able to do a transform here.

            String stylesheet = this.getPortletConfig().getInitParameter( "stylesheet" );

            if ( stylesheet == null ) {
                throw new PortletException( "The 'stylesheet' parameter was not defined." );
        }

            try {
                //Set encoding for the document to utf-8...
                String content = SimpleTransform.transform( document,
                                                            stylesheet,
                                                            this.getPortletConfig().getInitParameters() );

                this.setContent( new JetspeedClearElement( content ) );


            } catch ( SAXException e ) {
                logger.error("Exception",  e);
                throw new PortletException( e.getMessage() );
            }
        } catch (Throwable t) {
            String message = "RSSPortlet:  Couldn't set items for XML document -> " +
                url;


            logger.error( message, t );
            throw new PortletException( t.getMessage() );
        }


    }



    /**
    Given a base node... search this for a node with the given name and return
    it or null if it does not exist
    */
    private static final Node getNode( Node start, String name ) {

        NodeList list = start.getChildNodes();

        for ( int i = 0; i < list.getLength(); ++i ) {

            Node node = list.item( i );

            if ( node.getNodeName().equals( name ) ) {
                return node;
            }
        }
        return null;
    }

    /**
    Given a URL to some content, clean the content to Xerces can handle it
    better.  Right now this involves:
    <ul>
        <li>
            If the document doesn't begin with "<?xml version=" truncate the
            content until this is the first line
        </li>

    </ul>

    */
    /**
    test it...
    */
    private Reader cleanse( String content ) throws IOException, SAXException {

        String filtered = null;

        //specify the XML declaration to search for... this is just a subset
        //of the content but it will always exist.
        String XMLDECL = "<?xml version=";

        int start = content.indexOf( XMLDECL );

        if ( start <= 0 ) {
            filtered = content;
        } else {
            filtered = content.substring( start, content.length() );
        }

        return new StringReader( filtered );
    }

    /**
    Get the items that were defined by this XML content
    */
    public Item[] getItems() {
        return this.items;
    }

    public void setItems( Item[] items ) {
        this.items = items;
    }


    //Cacheable interface..

    /**
    @author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
    @version $Id: RSSPortlet.java,v 1.52 2004/02/23 04:03:33 jford Exp $
    */
    public boolean isCacheable() {
        return true;
    }

    /**
    Given a Document, find all the <item>'s and return them as peer's
    */
    private Item[] parseItems( Document doc ) {

        String root = doc.getDocumentElement().getTagName();

        if ( root.equals( "rdf:RDF" ) ) {

            NodeList list = doc.getElementsByTagName( "item" );

            return getItems( list );

            //parse out each nodelist item and create

        } else if ( root.equals( "rss" ) ) {

            NodeList list = doc.getElementsByTagName( "channel" );

            if ( list.getLength() != 1 ) {
                //if there aren't any channels there can't be any items.
                return new Item[0];
            }

            Node channel = list.item( 0 );

            //how get the items as a nodelist

            return getItems( channel.getChildNodes() );


          } else if ( root.equals( "xml" ) ) {

            NodeList list = doc.getElementsByTagName( "item" );

            return getItems( list );

            //parse out each nodelist item and create

          } else {
            //don't know what to do...
            return new Item[0];
        }

    }

    /**
    After you find the nodelist for items parse it out and get some Items
    */
    private Item[] getItems( NodeList items ) {


        Vector v = new Vector();

        for ( int i = 0; i < items.getLength(); ++i ) {

            Node node = items.item( i );

            //just make sure this is an <item> element
            if ( node.getNodeName().equals( "item" ) == false ) {
                continue;
            }

            NodeList itemChildren = node.getChildNodes();

            String title = null;
            String link = null;
            String description = null;

            for ( int j = 0; j < itemChildren.getLength(); ++j ) {


                Node child = itemChildren.item( j );

                if ( child.getNodeName().equals( "title" ) ) {
                    if ( child.getFirstChild() != null )
                        title = child.getFirstChild().getNodeValue();
                }

                if ( child.getNodeName().equals( "link" ) ) {
                    if ( child.getFirstChild() != null )
                        link = child.getFirstChild().getNodeValue();
                }

                if ( child.getNodeName().equals( "description" ) ) {
                    if (child.getFirstChild() != null) {
                       description = child.getFirstChild().getNodeValue();
                    }
                }


            }

            v.addElement( new Item( title, link, description ) );

        }

        Item[] foundItems = new Item[ v.size() ];
        v.copyInto( foundItems );
        return foundItems;

    }

	/**
	Represents an RSS item.
	*/
	public static class Item {

	    private String title;
	    private String link;
	    private String description;

	    public Item( String title,
	                 String link,
	                 String description ) {
	        this( title, link );
	        this.description = description;

	    }

	    public Item ( String title,
	                  String link ) {
	        this.title = title;
	        this.link = link;
	    }

	    public String getTitle() {
	        return this.title;
	    }

	    public String getLink() {
	        return this.link;
	    }

	    /**
	    Get the description for this item... it may be null.
	    */
	    public String getDescription() {
	        return this.description;
	    }

	}

}
