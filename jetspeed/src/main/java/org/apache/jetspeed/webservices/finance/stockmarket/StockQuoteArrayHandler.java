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

package org.apache.jetspeed.webservices.finance.stockmarket;

import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.*;

/**
    SAX-based XML event handler (filter). Works with org.xml.sax.XMLReader to filter
    standard SAX events. This class maps the XML represention of a stock array into a java classes.

    @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
    @version $Id: StockQuoteArrayHandler.java,v 1.3 2004/02/23 03:15:29 jford Exp $
*/

public class StockQuoteArrayHandler extends XMLFilterImpl
{
    StockQuote[] array = new BaseStockQuote[0];
    StockQuoteHandler currentVal = null;
    boolean inElement;

    public void startElement(String uri, String localName, String qName, Attributes attributes) 
        throws SAXException 
    {
        if (attributes.getValue("href") != null) 
            throw new SAXNotSupportedException("href attributes not supported");
        inElement = true;
        currentVal = new StockQuoteHandler();
        currentVal.setParent(this);
        setContentHandler(currentVal);
    }

    public void endElement(String uri, String localName, String qName) 
        throws SAXException 
    {
        if (inElement) 
        {
            inElement = false;
            StockQuote theVal = currentVal.getResult();
            StockQuote[] newar = new BaseStockQuote[array.length+1];
            System.arraycopy(array,0,newar,0,array.length);
            newar[array.length] = theVal;
            array = newar;
        } else 
        {
            ContentHandler handler = (ContentHandler)getParent();
            getParent().setContentHandler(handler);
            handler.endElement(uri,localName,qName);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException 
    {
    }

    public StockQuote[] getResult() 
    {
        return array;
    }

    public void setContentHandler(ContentHandler handler) 
    {
        ((XMLReader)getParent()).setContentHandler(handler);
    }

}

