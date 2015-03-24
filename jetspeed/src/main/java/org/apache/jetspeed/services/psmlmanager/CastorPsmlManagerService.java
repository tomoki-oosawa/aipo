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

package org.apache.jetspeed.services.psmlmanager;

//Jetspeed stuff
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.util.FileCopy;
import org.apache.jetspeed.util.DirectoryUtils;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.resources.JetspeedResources;

//Castor defined API
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.*;

//turbine stuff
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.servlet.ServletService;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.JetspeedRoleFactory;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedGroupFactory;
import org.apache.jetspeed.om.security.JetspeedUserFactory;

//castor support
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

// serialization support
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;

//standard java stuff
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import javax.servlet.ServletConfig;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jetspeed.cache.FileCache;
import org.apache.jetspeed.cache.FileCacheEventListener;
import org.apache.jetspeed.cache.FileCacheEntry;


/**
 * This service is responsible for loading and saving PSML documents.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @version $Id: CastorPsmlManagerService.java,v 1.44 2004/03/31 00:23:02 jford Exp $
 */
public class CastorPsmlManagerService extends TurbineBaseService
                                      implements FileCacheEventListener,
                                                 PsmlManagerService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(CastorPsmlManagerService.class.getName());
    
    // resource path constants
    protected static final String PATH_GROUP              = "group";
    protected static final String PATH_ROLE               = "role";
    protected static final String PATH_USER               = "user";

    // configuration keys
    protected final static String CONFIG_ROOT             = "root";
    protected final static String CONFIG_EXT              = "ext";
    protected final static String CONFIG_SCAN_RATE        = "scanRate";
    protected final static String CONFIG_CACHE_SIZE       = "cacheSize";

    // default configuration values
    public final static String DEFAULT_ROOT             = "/WEB-INF/psml";
    public final static String DEFAULT_EXT              = ".psml";

    // default resource
    public final static String DEFAULT_RESOURCE         = "default.psml";

    // the root psml resource directory
    protected String root;
    // base store directory
    protected File rootDir = null;
    // file extension
    protected String ext;

    /** The documents loaded by this manager */
    protected FileCache documents = null;

    /** the output format for pretty printing when saving registries */
    protected OutputFormat format = null;

    /** the base refresh rate for documents */
    protected long scanRate = 1000 * 60; // every minute

    /** the default cache size */
    protected int cacheSize = 100;

    /** the import/export consumer service **/
    protected PsmlManagerService consumer = null;
    protected boolean importFlag = false;

    // castor mapping
    public static final String DEFAULT_MAPPING = "${webappRoot}/WEB-INF/conf/psml-mapping.xml";
    protected String mapFile = null;

    /** the Castor mapping file name */
    protected Mapping mapping = null;

    /** The default encoding used to serialize PSML files to disk */
    protected String defaultEncoding = JetspeedResources.getString(JetspeedResources.CONTENT_ENCODING_KEY, "utf-8");

    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     */
    public void init( ServletConfig conf ) throws InitializationException
    {
        if (getInit())
        {
            return;
        }

        //Ensure that the servlet service is initialized
        TurbineServices.getInstance().initService(ServletService.SERVICE_NAME, conf);

        // get configuration parameters from Jetspeed Resources
        ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                     .getResources(PsmlManagerService.SERVICE_NAME);
        // get the PSML Root Directory
        this.root = serviceConf.getString( CONFIG_ROOT, DEFAULT_ROOT );
        this.rootDir = new File(root);

        //If the rootDir does not exist, treat it as context relative
        if ( !rootDir.exists() )
        {
            try
            {
                this.rootDir = new File(conf.getServletContext().getRealPath(root));
            }
            catch (Exception e)
            {
                // this.rootDir = new File("./webapp" + this.rootDir.toString());
            }
        }
        //If it is still missing, try to create it
        if (!rootDir.exists())
        {
            try
            {
                rootDir.mkdirs();
            }
            catch (Exception e)
            {
            }
        }

        // get default extension
        this.ext = serviceConf.getString( CONFIG_EXT, DEFAULT_EXT );

        // create the serializer output format
        this.format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(4);
        format.setLineWidth(0);

        // psml castor mapping file
        mapFile = serviceConf.getString("mapping",DEFAULT_MAPPING);
        mapFile = TurbineServlet.getRealPath( mapFile );
        loadMapping();

        this.scanRate = serviceConf.getLong(CONFIG_SCAN_RATE, this.scanRate);
        this.cacheSize= serviceConf.getInt(CONFIG_CACHE_SIZE, this.cacheSize);

        documents = new FileCache(this.scanRate, this.cacheSize);
        documents.addListener(this);
        documents.startFileScanner();


        //Mark that we are done
        setInit(true);

        // Test
        //testCases();

    }


    /** Late init method from Turbine Service model */
    public void init() throws InitializationException
    {
        while( !getInit() )
        {
            //Not yet...
            try
            {
                Thread.sleep( 500 );
            }
            catch (InterruptedException ie )
            {
                logger.error("Exception", ie);
            }
        }
    }


    /**
     * This is the shutdown method called by the
     * Turbine <code>Service</code> framework
     */
    public void shutdown()
    {
        documents.stopFileScanner();
    }

    /**
     * Returns a PSML document of the given name.
     * For this implementation, the name must be the document
     * URL or absolute filepath
     *
     * @deprecated
     * @param name the name of the document to retrieve
     */
    public PSMLDocument getDocument( String name )
    {
        if (name == null)
        {
            String message = "PSMLManager: Must specify a name";
            logger.error( message );
            throw new IllegalArgumentException( message );
        }

        if (logger.isDebugEnabled())
        {
            logger.debug( "PSMLManager: asked for " + name );
        }

        PSMLDocument doc = null;

        doc = (PSMLDocument)documents.getDocument(name);

        if (doc == null)
        {
            doc = loadDocument(name);

            synchronized (documents)
            {
                // store the document in the hash and reference it to the watcher
                try
                {
                    documents.put(name, doc);
                }
                catch (java.io.IOException e)
                {
                    logger.error("Error putting document", e);
                }
            }
        }

        return doc;
    }

    /**
     * Returns a cached PSML document for the given locator
     * 
     * @param locator The locator descriptor of the document to be retrieved.
     * @return PSML document  from cache (or disk if not yet cached)
     */
    public PSMLDocument getDocument( ProfileLocator locator)
    {
        return getDocument(locator, true);
    }

    /**
     * Returns a PSML document for the given locator
     * 
     * @param locator   The locator descriptor of the document to be retrieved.
     * @param getCached Look in the cache (true) or umarshall a fresh copy from disk (false)
     * @return 
     */
    protected PSMLDocument getDocument( ProfileLocator locator, boolean getCached )
    {
        if (locator == null)
        {
            String message = "PSMLManager: Must specify a name";
            logger.error( message );
            throw new IllegalArgumentException( message );
        }
        File base = this.rootDir;
        String path = mapLocatorToFile(locator);
        File file = new File(base, path);
        String name = null;

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            logger.error("PSMLManager: unable to resolve file path for "+ file);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("PSMLManager: calculated resource:" + path + ". Base: " + base + " File: " + name);
        }

        PSMLDocument doc = null;
        Profile profile = null;

        if (getCached == true)
        {
            profile = (Profile)documents.getDocument(name);
        }

        if (profile == null)
        {
            doc = loadDocument(name);
            if (null == doc)
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn( "PSMLManager: " + name + " not found, returning null document" );
                }
                return null;
            }

            synchronized (documents)
            {
                // store the document in the hash and reference it to the watcher
                Profile newProfile = createProfile(locator);
                newProfile.setDocument(doc);
                try
                {
                    documents.put(name, newProfile);
                }
                catch (IOException e)
                {
                    logger.error("Error putting document", e);
                }
            }
        }
        else
        {
            doc = profile.getDocument();
        }

        return doc;
    }

    /**
     * Loads a PSML document from disk bypassing the cache
     * 
     * @param locator
     * @return PSML document from disk
     */
    public PSMLDocument refresh(ProfileLocator locator)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("CastorPsmlManagerService: psml document refreshed from disk: " + locator.getPath());
        }
        return getDocument(locator, false);
    }

    /**
     * Load a PSMLDOcument from disk
     *
     * @param fileOrUrl a String representing either an absolute URL or an
     * absolute filepath
     */
    protected PSMLDocument loadDocument(String fileOrUrl)
    {
        PSMLDocument doc = null;

        if (fileOrUrl!=null)
        {
            if (!fileOrUrl.endsWith(DEFAULT_EXT))
            {
                fileOrUrl = fileOrUrl.concat(DEFAULT_EXT);
            }

            // load the document and add it to the watcher
            // we'll assume the name is the the location of the file

            File f = getFile(fileOrUrl);
            if (null == f)
                return null;

            doc = new BasePSMLDocument();
            doc.setName(fileOrUrl);

            // now that we have a file reference, try to load the serialized PSML
            Portlets portlets = null;
            try
            {
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbfactory.newDocumentBuilder();

                Document d = builder.parse(f);

                Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
                portlets = (Portlets)unmarshaller.unmarshal((Node) d);

                doc.setPortlets(portlets);

            }
            catch (IOException e)
            {
                logger.error("PSMLManager: Could not load the file "+f.getAbsolutePath(), e);
                doc = null;
            }
            catch (MarshalException e)
            {
                logger.error("PSMLManager: Could not unmarshal the file "+f.getAbsolutePath(), e);
                doc = null;
            }
            catch (MappingException e)
            {
                logger.error("PSMLManager: Could not unmarshal the file "+f.getAbsolutePath(), e);
                doc = null;
            }
            catch (ValidationException e)
            {
                logger.error("PSMLManager: document "+f.getAbsolutePath()+" is not valid", e);
                doc = null;
            }
            catch (ParserConfigurationException e)
            {
                logger.error("PSMLManager: Could not load the file "+f.getAbsolutePath(), e);
                doc = null;
            }
            catch (SAXException e)
            {
                logger.error("PSMLManager: Could not load the file "+f.getAbsolutePath(), e);
                doc = null;
            }
        }

        return doc;
    }

    /** Store the PSML document on disk, using its locator
     *
     * @param profile the profile locator description.
     * @return true if the operation succeeded
     */
    public boolean store(Profile profile)
    {
        PSMLDocument doc = profile.getDocument();

        File base = this.rootDir;
        String path = mapLocatorToFile(profile);

        File file = new File(base, path);
        String fullpath = null;

        try
        {
            fullpath = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            logger.error("PSMLManager: unable to resolve file path for "+ file);
        }

        boolean ok = saveDocument(fullpath, doc);

        // update it in cache
        synchronized (documents)
        {
            try
            {
                documents.put(fullpath, profile);
            }
            catch (IOException e)
            {
                logger.error("Error storing document", e);
            }
        }

        return ok;
    }

    /** Save the PSML document on disk, using its name as filepath
     * @deprecated
     * @param doc the document to save
     */
    public boolean saveDocument(PSMLDocument doc)
    {
        return saveDocument(doc.getName(), doc);
    }

    /** Save the PSML document on disk to the specififed fileOrUrl
     *
     * @param fileOrUrl a String representing either an absolute URL
     * or an absolute filepath
     * @param doc the document to save
     */
    public boolean saveDocument(String fileOrUrl, PSMLDocument doc)
    {
        boolean success = false;

        if (doc == null) return false;
        File f = getFile(fileOrUrl);
        if (f == null)
        {
            f = new File(fileOrUrl);
        }

        OutputStreamWriter writer = null;
        FileOutputStream fos = null;
        try
        {
            String encoding = this.defaultEncoding;
            fos = new FileOutputStream(f);
            writer = new OutputStreamWriter(fos, encoding);

            save(writer, doc.getPortlets());
            success = true;
        }
        catch (MarshalException e)
        {
            logger.error("PSMLManager: Could not marshal the file "+f.getAbsolutePath(), e);
        }
        catch (MappingException e)
        {
            logger.error("PSMLManager: Could not marshal the file "+f.getAbsolutePath(), e);
        }
        catch (ValidationException e)
        {
            logger.error("PSMLManager: document "+f.getAbsolutePath()+" is not valid", e);
        }
        catch (IOException e)
        {
            logger.error("PSMLManager: Could not save the file "+f.getAbsolutePath(), e);
        }
        catch (Exception e)
        {
            logger.error("PSMLManager: Error while saving  "+f.getAbsolutePath(), e);
        }
        finally
        {
            try { writer.close(); } catch (IOException e) {}
            try { if(fos != null) { fos.close(); } } catch (IOException e) {}
        }

        return success;
    }

    /** Deserializes a PSML structure read from the reader using Castor
     *  XML unmarshaller
     *
     * @param reader the reader to load the PSML from
     * @param the loaded portlets structure or null
     */
    protected Portlets load(Reader reader)
        throws IOException, MarshalException, ValidationException, MappingException
    {
        Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
        Portlets portlets = (Portlets)unmarshaller.unmarshal(reader);
        return portlets;
    }

    protected void loadMapping()
        throws InitializationException
    {
        // test the mapping file and create the mapping object

        if (mapFile != null)
        {
            File map = new File(mapFile);
            if (logger.isDebugEnabled())
            {
                logger.debug("PSMLManager: Loading psml mapping file "+mapFile);
            }
            if (map.exists() && map.isFile() && map.canRead())
            {
                try
                {
                    mapping = new Mapping();
                    InputSource is = new InputSource( new FileReader(map) );
                    is.setSystemId( mapFile );
                    mapping.loadMapping( is );
                }
                catch (Exception e)
                {
                    logger.error("PSMLManager: Error in psml mapping creation", e);
                    throw new InitializationException("Error in mapping",e);
                }
            }
            else
            {
                throw new InitializationException("PSML Mapping not found or not a file or unreadable: "+mapFile);
            }
        }
    }

    /** Serializes a PSML structure using the specified writer with Castor
     *  XML marshaller and a Xerces serializer for pretty printing
     *
     * @param writer the writer to use for serialization
     * @param portlets the structure to save
     */
    protected void save(Writer writer, Portlets portlets)
        throws IOException, MarshalException, ValidationException, MappingException
    {
        String encoding = this.defaultEncoding;

        if (portlets != null)
        {
            format.setEncoding(encoding);
            Serializer serializer = new XMLSerializer(writer, format);
            Marshaller marshaller = new Marshaller(serializer.asDocumentHandler());
            marshaller.setMapping(this.mapping);
            marshaller.marshal(portlets);
        }
    }

    /** Tests wether the passed argument is an URL string or a file name
     *  and returns the corresponding file object, using diskcache for
     *  remote URLs
     *
     *  @param fileOrUrl the URL string or file path
     *  @return a File object. This file may not exist on disk.
     */
    protected File getFile(String fileOrUrl)
    {
        File f = null;

        f = new File(fileOrUrl);

        if (f.exists())
        {
            return f;
        }

        return null;
    }

    /** Create a new document.
     *
     * @param profile The description and default value for the new document.
     * @return The newly created document;
     */
    public PSMLDocument createDocument( Profile profile )
    {
        File base = this.rootDir;
        String path = mapLocatorToFile((ProfileLocator)profile);

        if (logger.isDebugEnabled())
        {
            logger.debug("PSMLManager: Create document for profile " + profile +", calculated path: " + path);
        }

        File file = new File(base, path);
        String name = null;

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            logger.error("PSMLManager: unable to resolve file path for "+ file);
        }

        PSMLDocument template = profile.getDocument();
        PSMLDocument doc = new BasePSMLDocument( name, template.getPortlets() );
        try
        {
            String parent = file.getParent();
            File filePath = new File(parent);
            filePath.mkdirs();
            if (template.getName() != null)
            {
                try
                {
                    File source = new File(template.getName());
                    if (source.exists())
                    {
                        FileCopy.copy( template.getName(), name );
                    }
                }
                catch (Exception e)
                {}
            }
            else
            {
                doc.setName(name);
            }
            saveDocument(doc);
        }
        catch (Exception e)
        {
            logger.error("PSMLManager: Failed to save document: " , e);
        }
        return doc;
    }

    /** Given a ordered list of locators, find the first document matching
     *  a profile locator, starting from the beginning of the list and working
     *  to the end.
     *
     * @param locator The ordered list of profile locators.
     */
    public PSMLDocument getDocument( List locators )
    {
        PSMLDocument doc=null;

        Iterator i = locators.iterator();
        while ((doc==null)&&(i.hasNext()))
        {
            doc=getDocument((ProfileLocator)i.next());
        }

        return doc;
    }

    /** Removes a document.
     *
     * @param locator The description of the profile resource to be removed.
     */
    public void removeDocument( ProfileLocator locator )
    {
        // remove a single document
        String fileName = mapLocatorToFile(locator);

        File base = this.rootDir;
        File file = new File(base, fileName);
        String name = null;

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            logger.error("PSMLManager: unable to resolve file path for "+ file);
        }


        synchronized (documents)
        {
            documents.remove(name);
        }

        file.delete();

    }

    /** Removes all documents for a given user.
     *
     * @param user The user object.
     */
    public void removeUserDocuments( JetspeedUser user )
    {
        ProfileLocator locator = Profiler.createLocator();
        locator.setUser(user);
        StringBuffer buffer = new StringBuffer();
        buffer.append(PATH_USER);
        String name = user.getUserName();
        if (null != name && name.length() > 0)
        {
            buffer.append(File.separator)
                .append(name);
        }
        else
            return; // don't delete the entire user directories

        String path = buffer.toString();
        File base = this.rootDir;
        File file = new File(base, path);

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            logger.error("PSMLManager: unable to resolve file path for "+ file);
        }


        synchronized (documents)
        {
            DirectoryUtils.rmdir(name);
            Iterator it = documents.getIterator();
            while (it.hasNext())
            {
                FileCacheEntry entry = (FileCacheEntry)it.next();
                if (null == entry)
                {
                    continue;
                }
                Profile profile = (Profile)entry.getDocument();
                if (null == profile)
                {
                    continue;
                }
                JetspeedUser pUser = profile.getUser();
                if (null != pUser && pUser.getUserName().equals(user.getUserName()))
                {
                    documents.remove(profile.getDocument().getName());
                }
            }
        }

    }

    /** Removes all documents for a given role.
     *
     * @param role The role object.
     */
    public void removeRoleDocuments( Role role )
    {
        ProfileLocator locator = Profiler.createLocator();
        locator.setRole(role);
        StringBuffer buffer = new StringBuffer();
        buffer.append(PATH_ROLE);
        String name = role.getName();
        if (null != name && name.length() > 0)
        {
            buffer.append(File.separator)
                .append(name);
        }
        else
            return; // don't delete the entire role directories

        String path = buffer.toString();
        File base = this.rootDir;
        File file = new File(base, path);

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            logger.error("PSMLManager: unable to resolve file path for "+ file);
        }


        synchronized (documents)
        {
            DirectoryUtils.rmdir(name);
            Iterator it = documents.getIterator();
            while (it.hasNext())
            {
                FileCacheEntry entry = (FileCacheEntry)it.next();
                if (null == entry)
                {
                    continue;
                }
                Profile profile = (Profile)entry.getDocument();
                if (null == profile)
                {
                    continue;
                }
                Role pRole = profile.getRole();
                if (null != pRole && pRole.getName().equals(role.getName()))
                {
                    documents.remove(profile.getDocument().getName());
                }
            }
        }
    }

    /** Removes all documents for a given group.
     *
     * @param group The group object.
     */
    public void removeGroupDocuments( Group group )
    {
        ProfileLocator locator = Profiler.createLocator();
        locator.setGroup(group);
        StringBuffer buffer = new StringBuffer();
        buffer.append(PATH_GROUP);
        String name = group.getName();
        if (null != name && name.length() > 0)
        {
            buffer.append(File.separator)
                .append(name);
        }
        else
            return; // don't delete the entire group directories

        String path = buffer.toString();
        File base = this.rootDir;
        File file = new File(base, path);

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            logger.error("PSMLManager: unable to resolve file path for "+ file);
        }


        synchronized (documents)
        {
            DirectoryUtils.rmdir(name);
            Iterator it = documents.getIterator();
            while (it.hasNext())
            {
                FileCacheEntry entry = (FileCacheEntry)it.next();
                if (null == entry)
                {
                    continue;
                }
                Profile profile = (Profile)entry.getDocument();
                if (null == profile)
                {
                    continue;
                }
                Group pGroup = profile.getGroup();
                if (null != pGroup && pGroup.getName().equals(group.getName()))
                {
                    documents.remove(profile.getDocument().getName());
                }
            }
        }

    }


    /**
     * Maps a ProfileLocator to a file.
     *
     * @param locator The profile locator describing the PSML resource to be found.
     * @return the String path of the file.
     */
    protected String mapLocatorToFile(ProfileLocator locator)
    {
        StringBuffer path = new StringBuffer();

        // move the base dir is either user or role is specified
        Role role = locator.getRole();
        Group group = locator.getGroup();
        JetspeedUser user = locator.getUser();

        if (user != null)
        {
            path.append(PATH_USER);
            String name = user.getUserName();
            if (null != name && name.length() > 0)
            {
                path.append(File.separator)
                    .append(name);
            }
        }
        else if (group != null)
        {
            path.append(PATH_GROUP);
            String name = group.getName();
            if (null != name && name.length() > 0)
            {
                path.append(File.separator)
                    .append(name);
            }
        }
        else if (null != role)
        {
            path.append(PATH_ROLE);
            String name = role.getName();
            if (null != name && name.length() > 0)
            {
                path.append(File.separator)
                    .append(name);
            }
        }

        // Media
        if (null != locator.getMediaType())
        {
            path.append(File.separator)
                .append(locator.getMediaType());
        }
        // Language
        if (null != locator.getLanguage() && (! locator.getLanguage().equals("-1")))
        {
            path.append(File.separator)
                .append(locator.getLanguage());
        }
        // Country
        if (null != locator.getCountry() && (! locator.getCountry().equals("-1")))
        {
            path.append(File.separator)
                .append(locator.getCountry());
        }
        // Resource Name
        if (null != locator.getName())
        {
            if (!(locator.getName().endsWith(CastorPsmlManagerService.DEFAULT_EXT)))
            {
                path.append(File.separator)
                    .append(locator.getName()).append(CastorPsmlManagerService.DEFAULT_EXT);
            }
            else
            {
                path.append(File.separator)
                    .append(locator.getName());
            }
        }
        else
        {
            path.append(File.separator)
                .append(DEFAULT_RESOURCE);
        }

        return  path.toString();
    }

    protected static int STATE_INIT = 0;
    protected static int STATE_BASE = 1;
    protected static int STATE_NAME = 2;
    protected static int STATE_MEDIA = 3;
    protected static int STATE_LANGUAGE = 4;
    protected static int STATE_COUNTRY = 5;

    /** Query for a collection of profiles given a profile locator criteria.
     *
     * @param locator The profile locator criteria.
     */
    public Iterator query( QueryLocator locator )
    {
        List list = new LinkedList();

        Role role = locator.getRole();
        Group group = locator.getGroup();
        JetspeedUser user = locator.getUser();

        // search thru anonymous directories?
        int qm = locator.getQueryMode();
        if ((qm & QueryLocator.QUERY_USER) == QueryLocator.QUERY_USER)
        {
            Profile profile = createProfile();
            StringBuffer path = new StringBuffer();
            path.append(PATH_USER);
            String name = null;
            int state = STATE_INIT;
            if (null != user)
            {
                name = user.getUserName();
                profile.setUser( user );
                if (null != name)
                {
                    path.append(File.separator).append(name);
                    state = STATE_BASE;
                }
            }
            File base = this.rootDir;
            File file = new File(base, path.toString());
            String absPath = file.getAbsolutePath();
            QueryState qs = new QueryState( QUERY_BY_USER,
                                             profile,
                                             locator,
                                             list,
                                             name,
                                             state);
            subQuery(qs, absPath);
        }
        if ((qm & QueryLocator.QUERY_ROLE) == QueryLocator.QUERY_ROLE)
        {
            Profile profile = createProfile();
            StringBuffer path = new StringBuffer();
            path.append(PATH_ROLE);
            String name = null;
            int state = STATE_INIT;
            if (null != role)
            {
                name = role.getName();
                profile.setRole( role );
                if (null != name)
                {
                    path.append(File.separator).append(name);
                    state = STATE_BASE;
                }
            }
            File base = this.rootDir;
            File file = new File(base, path.toString());
            String absPath = null;

            try
            {
                absPath = file.getCanonicalPath();
            }
            catch (IOException e)
            {
                logger.error("PSMLManager: unable to resolve file path for "+ file);
            }

            QueryState qs = new QueryState( QUERY_BY_ROLE,
                                             profile,
                                             locator,
                                             list,
                                             name,
                                             state);
            subQuery(qs, absPath);
        }
        if ((qm & QueryLocator.QUERY_GROUP) == QueryLocator.QUERY_GROUP)
        {
            Profile profile = createProfile();
            StringBuffer path = new StringBuffer();
            path.append(PATH_GROUP);
            String name = null;
            int state = STATE_INIT;
            if (null != group)
            {
                name = group.getName();
                profile.setGroup( group );
                if (null != name)
                {
                    path.append(File.separator).append(name);
                    state = STATE_BASE;
                }
            }
            File base = this.rootDir;
            File file = new File(base, path.toString());
            String absPath = null;

            try
            {
                absPath = file.getCanonicalPath();
            }
            catch (IOException e)
            {
                logger.error("PSMLManager: unable to resolve file path for "+ file);
            }

            QueryState qs = new QueryState( QUERY_BY_GROUP,
                                             profile,
                                             locator,
                                             list,
                                             name,
                                             state);
            subQuery(qs, absPath);
        }

        return list.iterator();
    }

    /** Create a profile based on import flag.
     *
     */
    protected Profile createProfile()
    {
        if (importFlag)
            return new ImportProfile(this, this.consumer);
        else
            return Profiler.createProfile();
    }

    protected Profile createProfile(ProfileLocator locator)
    {
        if (importFlag)
            return new ImportProfile(this, this.consumer, locator);
        else
            return Profiler.createProfile(locator);
    }

    /** Query for a collection of profiles given a profile locator criteria.
     *  This method should be used when importing or exporting profiles between services.
     *
     * @param locator The profile locator criteria.
     * @return The count of profiles exported.
     */
    public int export(PsmlManagerService consumer, QueryLocator locator)
    {
        importFlag = true;
        Iterator profiles = null;
        int count = 0;
        try
        {
            this.consumer = consumer;
            profiles = query(locator);

            while (profiles.hasNext() )
            {
                Profile profile = (Profile)profiles.next();
                //dumpProfile(profile);
                try
                {
                    consumer.createDocument(profile);
                    count++;
                }
                catch (Exception ex)
                {
                    try
                    {
                        consumer.store(profile);
                        count++;
                    }
                    catch (Exception e)
                    {
                        logger.error("PSMLManager: Failed to export profiles to DB: " + profile, ex );
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("PSMLManager: Failed to export profiles to DB: " , e );

        }
        finally
        {
            importFlag = false;
        }
        return count;
    }


    /** Query for a collection of profiles given a profile locator criteria.
     *  To specify 'all' - use '*' in the criteria
     *
     * @param locator The profile locator criteria.
     */
    protected void subQuery(QueryState qs, String path)
    {
        File file = new File(path);
        if (file.isFile())
        {
            try
            {
                String filename = file.getName();
                if (!filename.endsWith(this.ext))
                    return;

                Profile clone = (Profile)qs.profile.clone();
                clone.setName(filename);
                qs.list.add( clone );
            }
            catch (Exception e)
            {
                logger.error("PSMLManager: Failed to clone profile: " + path + " : " + e, e);
            }
        }
        else if (file.isDirectory())
        {
            String dirName = file.getName();
            qs.state++;

            // filter out based on name, mediatype, language, country
            if (qs.state == STATE_NAME)
            {
                if (null != qs.name)
                {
                    if (!dirName.equals(qs.name))
                        return;
                }
                try
                {
                    if (QUERY_BY_USER == qs.queryBy)
                    {
                        JetspeedUser user = (JetspeedUser)qs.profile.getUser();
                        if (null == user)
                        {
                            user = JetspeedUserFactory.getInstance();
                            user.setUserName(file.getName());
                            qs.profile.setUser(user);
                            qs.clearName = true;
                        }
                    }
                    else if (QUERY_BY_ROLE == qs.queryBy)
                    {
                        Role role = qs.profile.getRole();
                        if (null == role)
                        {
                            role = JetspeedRoleFactory.getInstance();
                            role.setName(file.getName());
                            qs.profile.setRole(role);
                            qs.clearName = true;
                        }
                    }
                    else if (QUERY_BY_GROUP == qs.queryBy)
                    {
                        Group group = qs.profile.getGroup();
                        if (null == group)
                        {
                            group = JetspeedGroupFactory.getInstance();
                            group.setName(file.getName());
                            qs.profile.setGroup(group);
                            qs.clearName = true;
                        }
                    }
                }
                catch (Exception e)
                {}


            }
            else if (qs.state == STATE_MEDIA)
            {
                String media = qs.locator.getMediaType();
                if (null != media)
                {
                    if (!dirName.equals(media))
                        return;
                }
                else
                {
                    qs.profile.setMediaType(dirName);
                    qs.clearMedia = true;
                }
            }
            else if (qs.state == STATE_LANGUAGE)
            {
                String language = qs.locator.getLanguage();
                if (null != language)
                {
                    if (!dirName.equals(language))
                        return;
                }
                else
                {
                    qs.profile.setLanguage(dirName);
                    qs.clearLanguage = true;
                }
            }
            else if (qs.state == STATE_COUNTRY)
            {
                String country = qs.locator.getCountry();
                if (null != country)
                {
                    if (!dirName.equals(country))
                        return;
                }
                else
                {
                    qs.profile.setCountry(dirName);
                    qs.clearCountry = true;
                }
            }

            if (!path.endsWith(File.separator))
                path += File.separator;

            String files[] = file.list();


            // Process all files recursivly
            for(int ix = 0; files != null && ix < files.length; ix++)
            {
                subQuery(qs, path + files[ix]);
            }

            // clear state
            if (qs.state == STATE_NAME && true == qs.clearName)
            {
                if (QUERY_BY_USER == qs.queryBy)
                    qs.profile.setUser(null);
                else if (QUERY_BY_ROLE == qs.queryBy)
                    qs.profile.setRole(null);
                else if (QUERY_BY_GROUP == qs.queryBy)
                    qs.profile.setGroup(null);
                qs.clearName = false;
            }
            else if (qs.state == STATE_MEDIA && true == qs.clearMedia)
            {
                qs.profile.setMediaType(null);
                qs.clearMedia = false;
            }
            else if (qs.state == STATE_LANGUAGE && true == qs.clearLanguage)
            {
                qs.profile.setLanguage(null);
                qs.clearLanguage = false;
            }
            else if (qs.state == STATE_COUNTRY && true == qs.clearCountry)
            {
                qs.profile.setCountry(null);
                qs.clearCountry = false;
            }

            qs.state--;

        }

    }

     static int QUERY_BY_USER = 0;
     static int QUERY_BY_ROLE = 1;
     static int QUERY_BY_GROUP = 2;

    protected class QueryState
    {

        QueryState( int queryBy,
                    Profile profile,
                    ProfileLocator locator,
                    List list,
                    String name,
                    int state)
        {
            this.queryBy = queryBy;
            this.profile = profile;
            this.locator = locator;
            this.list = list;
            this.name = name;
            this.state = state;
        }

        protected int queryBy;
        protected Profile profile;
        protected ProfileLocator locator;
        protected List list;
        protected String name;
        protected int state;

        protected boolean clearName = false;
        protected boolean clearMedia = false;
        protected boolean clearLanguage = false;
        protected boolean clearCountry = false;

    }

    protected void testCases()
    {
        try
        {
            QueryLocator locator = new QueryLocator( QueryLocator.QUERY_USER );
            Iterator x1 = query( locator );
            dump( x1 );

            QueryLocator locator2 = new QueryLocator( QueryLocator.QUERY_USER );
            locator2.setUser( JetspeedSecurity.getUser("turbine") );
            Iterator x2 = query( locator2 );
            dump( x2 );


            QueryLocator locator4 = new QueryLocator( QueryLocator.QUERY_GROUP );
//            locator4.setGroup( JetspeedSecurity.getGroup("apache") );
            Iterator x4 = query( locator4 );
            dump( x4 );
          }
        catch (Exception e)
        {
            System.out.println( "Exception in Debug:" + e);
        }
    }

    protected void dump( Iterator it )
    {
        System.out.println("===============================================");
        while (it.hasNext() )
        {
            Profile profile = (Profile)it.next();
            dumpProfile(profile);
        }
        System.out.println("===============================================");
    }

    protected void dumpProfile(Profile profile)
    {
        JetspeedUser user = profile.getUser();
        Group group = profile.getGroup();
        Role role = profile.getRole();
        if (profile.getAnonymous() == true)
            System.out.println("ANON USER");
        System.out.println("RESOURCE = " + profile.getName());
        if (null != user)
            System.out.println("USER = " + user.getUserName() );
        if (null != group)
            System.out.println("GROUP = " + group.getName() );
        if (null != role)
            System.out.println("ROLE = " + role.getName() );
        System.out.println("MEDIA TYPE = " + profile.getMediaType());
        System.out.println("LANGUAGE = " + profile.getLanguage());
        System.out.println("COUNTRY = " + profile.getCountry());
        PSMLDocument doc = profile.getDocument();
        if (null == doc)
            System.out.println("Document is null");
        else
        {
            if (null == profile.getName())
                System.out.println("profile name is null");
            else
                System.out.println("Doc.name=" + profile.getName());
        }

        System.out.println("----------------------");
    }

    /**
     * Refresh event, called when the entry is being refreshed from file system.
     *
     * @param entry the entry being refreshed.
     */
    public void refresh(FileCacheEntry entry)
    {
        if (logger.isInfoEnabled())
        {
            logger.info("CastorPsmlManager: Entry is refreshing: " + entry.getFile().getPath());
        }

        Profile profile = (Profile) entry.getDocument();
        String path = null;

        if (profile != null)
        {
            try
            {
                path = entry.getFile().getCanonicalPath();
                profile.setDocument(loadDocument(path));
            }
            catch(java.io.IOException e)
            {
                logger.error("CastorPsmlManager: Failed to refresh document "+path);
            }
        }
    }

    /**
     * Evict event, called when the entry is being evicted out of the cache
     *
     * @param entry the entry being refreshed.
     */
    public void evict(FileCacheEntry entry)
    {
        System.out.println("entry is evicting: " + entry.getFile().getName());
    }

}

