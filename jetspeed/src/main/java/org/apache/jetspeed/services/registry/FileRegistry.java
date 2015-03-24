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

package org.apache.jetspeed.services.registry;

import java.io.Reader;
import java.util.Map;

/**
 * Interface for manipulating RegistryFragments in a fragment based
 * registry implementation.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: FileRegistry.java,v 1.4 2004/02/23 03:31:50 jford Exp $
 */
public interface FileRegistry {

    public static final int DEFAULT_REFRESH = 300;

    /** Refresh the state of the registry implementation. Should be called
     *   whenever the underlying fragments are modified
     */
    public void refresh();

    /**
     * @return a Map of all fragments keyed by file names
     */
    public Map getFragmentMap();

    /**
     * Read and unmarshal a fragment in memory
     * @param name the name of this fragment
     * @param reader the reader to use for creating this fragment
     * @param persistent whether this fragment should be persisted on disk in
     * the registry
     */
    public void createFragment(String name, Reader reader, boolean persistent);

    /**
     * Load and unmarshal a RegistryFragment from the file
     * @param file the absolute file path storing this fragment
     */
    public void loadFragment(String file);

    /**
     * Marshal and save a RegistryFragment to disk
     * @param file the absolute file path storing this fragment
     */
    public void saveFragment(String file);

    /**
     * Remove a fragment from storage
     * @param file the absolute file path storing this fragment
     */
    public void removeFragment(String file);
}
