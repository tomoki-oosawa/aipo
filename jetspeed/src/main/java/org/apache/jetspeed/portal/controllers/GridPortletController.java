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

package org.apache.jetspeed.portal.controllers;

//ECS stuff
import org.apache.ecs.html.Table;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;

//turbine RunData
import org.apache.turbine.util.RunData;

//jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletControllerConfig;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

//standard Java stuff
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Enumeration;


/**
Layouts the portlets in a grid and apply the following constraints
<ul>
<li>all cells within the same column have the same width</li>
<li>all cells within the same row have the same minimum height</li>
</ul>

<p>This controller expects the following parameters in its configuration
file :

<ul>
<li><b>columns<b> optional, number of columns of the grid. If not specified,
determined by the layout information in each portlet</li>
<li><b>columnWidths</b> optional, the size of the columns, separated by a colon</li>
<li><b>rows<b> optional, number of rows of the grid. If not specified,
determined by the layout information in each portlet</li>
<li><b>rowHeights</b> optional, the minimum size of the rows, separated by a colon</li>
</ul>

</p>
<p>The controller expects each portlet to have a row number and a column number
layout information. If this information is not found, put the Portlet in the
first cell of the table</p>

@author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
@version $Id: GridPortletController.java,v 1.17 2004/02/23 03:25:06 jford Exp $
*/
public class GridPortletController extends AbstractPortletController 
{

    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(GridPortletController.class.getName());
    
    private int columns = 0;
    private int rows = 0;
    private Vector rowHeights = null;
    private Vector colWidths = null;
    
    /**
    */
    public GridPortletController() {
        rowHeights = new Vector();
        colWidths = new Vector();
    }

    /**
    */
    private void calculateControllerLayout( Portlet portlet ) {

        if ( portlet instanceof PortletSet ) {

            Enumeration more = ((PortletSet)portlet).getPortlets();

            while ( more.hasMoreElements() ) {
                calculateControllerLayout( (Portlet) more.nextElement() );
            }

            return;
        }                

           
        PortletConfig portletConf = portlet.getPortletConfig();
        Integer colObj = portletConf.getConstraints().getColumn();
        Integer rowObj = portletConf.getConstraints().getRow();

        int col = (colObj!=null)?colObj.intValue():0;
        int row = (rowObj!=null)?rowObj.intValue():0;
        
        if ( col + 1 > this.getColumn() ) {
            this.setColumn( col + 1 );
        }
        
        if ( row + 1 > this.getRow() ) {
            this.setRow( row + 1 );
        }

        
    }
    
    /**
    Get the content for this PortletController
    */
    public ConcreteElement getContent( RunData rundata ) {

        ElementContainer base = new ElementContainer();

        try 
        {
        PortletSet portlets = getPortlets();
        PortletConfig pc = portlets.getPortletConfig();

        // first get the number of columns and rows to display
        Enumeration en = portlets.getPortlets();
      
        //see if any or the Portlets you want to add have a larger column or
        //row number than that defined in PSML
        while ( en.hasMoreElements() ) {

            Portlet portlet = (Portlet)en.nextElement();

            calculateControllerLayout( portlet );
            
        }

        setWidth( pc.getLayout( "width", getWidth() ) );

        int rows = getRow();
        int cols = getColumn();

        if (0 == rows || 0 == cols)
            return base; // empty container

        Table t = new Table()
                       .setWidth( this.getWidth() )
                       .setCellPadding( this.getPadding() )
                       .setAlign("center");

        base.addElement( t );

        ElementContainer[][] elements = new ElementContainer[rows][cols];

        for( int i = 0; i < rows; i++ )  {
            for ( int j = 0 ; j < cols; j++ ) {
                elements[i][j]=new ElementContainer();
            }
        }

        // populate the elements array
        en = portlets.getPortlets();
        while (en.hasMoreElements() ) {

            Portlet p = (Portlet)en.nextElement();
            PortletConfig pConf = p.getPortletConfig();

            Integer colObj = pConf.getConstraints().getColumn();
            Integer rowObj = pConf.getConstraints().getRow();
            int colnum = (colObj!=null)?colObj.intValue():0;
            int rownum = (rowObj!=null)?rowObj.intValue():0;

            elements[rownum % rows][colnum % cols]
                .addElement( p.getContent( rundata ) );

        }

        // build the table

        for (int i = 0; i < rows; ++i) {

            TR row = new TR();
            TD td = null;

            for(int j=0; j < cols ; ++j) {
                row.addElement( td= new TD().setVAlign("top")
                                       .addElement( elements[i][j] ) );
                if (getRowHeight(i)!=null) td.setHeight(getRowHeight(i));
                if (getColumnWidth(j)!=null) td.setWidth(getColumnWidth(j));
            }

            t.addElement(row);
        }

        }
        catch (Exception e)
        {
            logger.error("getContent():", e);
        }
        
        return base;

    }

    /**
    */
    public void init() {
        super.init();
        PortletControllerConfig conf = getConfig();
        
        if (conf!=null) {
            setColumn(Integer.parseInt(conf.getInitParameter("column","0")));
            setRow(Integer.parseInt(conf.getInitParameter("row","0")));
            setColumnsWidth(parseList(conf.getInitParameter("columnWidths")));
            setRowsHeight(parseList(conf.getInitParameter("rowHeights")));
        }
            
    }
    
    /**
    Set the number of columns used in this controller
    */
    public void setColumn(int col) {
        this.columns=col;
    }
    
    /**
    Get the number of columns used in this controller
    */
    public int getColumn() {
        return this.columns;
    }
    
    /**
    Set the number of rows used by this controller
    */
    public void setRow(int row) {
        this.rows=row;
    }
    
    /**
    Get the number of rows used by this controll
    */
    public int getRow() {
        return this.rows;
    }
    
    /**
    */
    public void setColumnsWidth(Vector widths) {
        this.colWidths = widths;
    }
    
    /**
    */
    public Enumeration getColumnsWidth() {
        return colWidths.elements();
    }
    
    /**
    */
    public String getColumnWidth(int pos) {
        if (pos < colWidths.size()) return (String)colWidths.elementAt(pos);
        return null;
    }
    
    /**
    */
    public void setRowsHeight(Vector heights) {
        this.rowHeights = heights;
    }
    
    /**
    */
    public Enumeration getRowsHeight() {
        return rowHeights.elements();
    }
    
    /**
    */
    public String getRowHeight(int pos) {
        if (pos < rowHeights.size()) return (String)rowHeights.elementAt(pos);
        return null;
    }
    
    /**
    */
    private Vector parseList(String list) {
        Vector v = new Vector();
        if (list!=null) {
            StringTokenizer st = new StringTokenizer(list,",");
            while (st.hasMoreTokens())
                v.addElement(st.nextToken());
        }

        return v;
    }

}

