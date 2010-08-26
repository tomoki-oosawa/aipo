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

package org.apache.jetspeed.modules.parameters;

/**
 * Returns texarea control intialized with the value parameter using the following options:
 * <UL>
 * <LI><code>rows</code>: number of rows (default = 3)</LI>
 * <LI><code>cols</code>: number of columns (default = 80)</LI> 
 *</UL>
 * 
 * @author <a href="mailto:mark_orciuch@ngsltd.com">Mark Orciuch</a>
 * @version $Id: TextArea.java,v 1.3 2004/02/23 03:01:20 jford Exp $
 */
public class TextArea extends JspParameterPresentationStyle
{

    public static final String ROWS = "rows";
    public static final String COLS = "cols";

}
