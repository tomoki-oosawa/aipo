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

import org.apache.turbine.services.Service;
import java.rmi.RemoteException;

/**
    StockQuoteService provides a web service for getting stock quotes.
        
    @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
    @version $Id: StockQuoteService.java,v 1.4 2004/02/23 03:15:29 jford Exp $
*/

public interface StockQuoteService extends Service
{
    /** The name of this service */
    public String SERVICE_NAME = "StockQuoteService";


    /**
        Get a single stock quote, given a symbol return the current price.

        @param symbol The stock symbol.
        @return String The current price.
      */
    public String quote( String symbol ) 
        throws RemoteException;

    /**
        Get a single stock quote record, given a symbol return a StockQuote object.

        @param symbol The stock symbol.
        @return StockQuote A full stock quote record.
      */

    public StockQuote fullQuote( String symbol )
            throws RemoteException;

    /**
        Get an array of quote records, given a array of stock symbols.

        @param symbols[] The array of stock symbols.
        @return StockQuote[] An array of full stock quotes for each stock symbol.
      */
    public StockQuote[] fullQuotes( String [] symbol )
            throws RemoteException;

    /**
        Set the name of the web service used by this service to retrieve stock quotes.

        @param service The name of the web service.
      */
    public void setWebService( String service );

    /**
        Get the name of the web service used by this service to retrieve stock quotes.

        @return String The name of the web service.
      */
    public String getWebService();

}
