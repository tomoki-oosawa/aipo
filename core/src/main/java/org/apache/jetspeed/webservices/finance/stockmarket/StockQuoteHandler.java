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
    standard SAX events. This class maps the XML represention of a stock into a java class.

    @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
    @version $Id: StockQuoteHandler.java,v 1.3 2004/02/23 03:15:29 jford Exp $
*/

public class StockQuoteHandler extends XMLFilterImpl
{
    StringBuffer resultBuffer = null;
    StockQuote result = null;
    XMLFilterImpl handler = null;

    public void startElement(String uri, String localName, String qName, Attributes attributes) 
        throws SAXException 
    {
        if (attributes.getValue("href") != null) 
            throw new SAXNotSupportedException("href attributes not supported");
        if (localName.equals("Price")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else             if (localName.equals("Name")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else             if (localName.equals("Symbol")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else             if (localName.equals("Time")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else             if (localName.equals("Date")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else             if (localName.equals("High")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else             if (localName.equals("Volume")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else             if (localName.equals("Change")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else             if (localName.equals("Opening")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else             if (localName.equals("Low")) {
            if (result == null) result = new BaseStockQuote();
            resultBuffer = new StringBuffer();
        } else {
            throw new SAXException("Unexpected element "+localName);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("Price")) {
            result.setPrice(resultBuffer.toString());
        } else if (localName.equals("Name")) {
            result.setName(resultBuffer.toString());
        } else if (localName.equals("Symbol")) {
            result.setSymbol(resultBuffer.toString());
        } else if (localName.equals("Time")) {
            result.setTime(resultBuffer.toString());
        } else if (localName.equals("Date")) {
            result.setDate(resultBuffer.toString());
        } else if (localName.equals("High")) {
            result.setHigh(resultBuffer.toString());
        } else if (localName.equals("Volume")) {
            result.setVolume(resultBuffer.toString());
        } else if (localName.equals("Change")) {
            result.setChange(resultBuffer.toString());
        } else if (localName.equals("Opening")) {
            result.setOpening(resultBuffer.toString());
        } else if (localName.equals("Low")) {
            result.setLow(resultBuffer.toString());
        } else {
            ContentHandler handler = (ContentHandler)getParent();
            getParent().setContentHandler(handler);
            handler.endElement(uri,localName,qName);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (resultBuffer != null) resultBuffer.append(new String(ch,start,length));
    }
    public void setContentHandler(ContentHandler handler) {
        ((XMLReader)getParent()).setContentHandler(handler);
    }
    public StockQuote getResult() {
        return result;
    }
}
