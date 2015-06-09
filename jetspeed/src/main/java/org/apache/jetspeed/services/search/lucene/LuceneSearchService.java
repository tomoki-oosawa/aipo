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

package org.apache.jetspeed.services.search.lucene;

// Java imports
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletConfig;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// Jetspeed imports
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.search.HandlerFactory;
import org.apache.jetspeed.services.search.ObjectHandler;
import org.apache.jetspeed.services.search.ParsedObject;
import org.apache.jetspeed.services.search.BaseParsedObject;
import org.apache.jetspeed.services.search.SearchResults;
import org.apache.jetspeed.services.search.SearchService;

// Turbine imports
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;

// Lucene imports
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

/**
 * Lucene implementation of search service.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean taylor</a>
 * @author <a href="mailto:caius1440@hotmail.com">Jeremy Ford</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: LuceneSearchService.java,v 1.10 2004/03/05 03:49:15 jford Exp $
 */
public class LuceneSearchService extends TurbineBaseService implements SearchService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(LuceneSearchService.class.getName());
    
    private static final int KEYWORD = 0;
    private static final int TEXT = 1;
    
    private static final String CONFIG_DIRECTORY = "directory";
    private File rootDir = null;
    private String indexRoot = null;

    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public synchronized void init(ServletConfig conf) throws InitializationException
    {

        // already initialized
        if (getInit())
        {
            return;
        }

        initConfiguration(conf);

        // initialization done
        setInit(true);

    }

    /**
     * This is the lateinitialization method called by the
     * Turbine <code>Service</code> framework
     *
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public void init() throws InitializationException
    {
        logger.info("Late init for " + SearchService.SERVICE_NAME + " called");
        while (!getInit())
        {
            //Not yet...
            try
            {
                Thread.sleep(100);
                logger.info("Waiting for init of " + SearchService.SERVICE_NAME + "...");
            }
            catch (InterruptedException ie)
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
    }

    /**
     * Loads the configuration parameters for this service from the
     * JetspeedResources.properties file.
     *
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    private void initConfiguration(ServletConfig conf) throws InitializationException
    {
        if (getInit())
        {
            return;
        }

        // get configuration parameters from Jetspeed Resources
        ResourceService serviceConf = ((TurbineServices) TurbineServices.getInstance())
                                      .getResources(SearchService.SERVICE_NAME);

        // Get config properties
        indexRoot = serviceConf.getString(CONFIG_DIRECTORY);
        //
        // The following section opens or creates the search index
        //
        //
        rootDir = new File(indexRoot);

        //If the rootDir does not exist, treat it as context relative
        if (!rootDir.exists())
        {
            if (indexRoot != null)
            {
                String rootDirPath = TurbineServlet.getRealPath("") + indexRoot;
                rootDir = new File(rootDirPath);
                if (!rootDir.exists())
                {
                    rootDir.mkdir();
                    logger.info("Created index directory '" + rootDir.getPath() + "'");
                }
            }
        }

        try
        {
            Searcher searcher = null;
            searcher = new IndexSearcher(rootDir.getPath());
            searcher.close();
        }
        catch (Exception e)
        {
            try
            {
                IndexWriter indexWriter = new IndexWriter(rootDir, new StandardAnalyzer(), true);
                indexWriter.close();
                indexWriter = null;
                logger.info("Created Lucene Index in " + rootDir.getPath());
            }
            catch (Exception e1)
            {
                logger.error(this.getClass().getName() + ".initConfiguration - Getting or creating IndexSearcher", e);
                throw new InitializationException("Getting or creating Index Searcher");
            }
        }

        //Mark that we are done
        setInit(true);
    }

    /**
     * Search
     * 
     * @task Parse content into title and description fields
     * @param searchString
     *               is the what is being searched for
     * @return Hits, if no hits then null.
     */
    public SearchResults search(String searchString)
    {
        Searcher searcher = null;
        Hits hits = null;
        
        try
        {
            searcher = new IndexSearcher(rootDir.getPath());
        }
        catch (IOException e)
        {
            logger.error("Failed to create index search using path " + rootDir.getPath());
            return null;
        }
        
        Analyzer analyzer = new StandardAnalyzer();
        
        String[] searchFields = {ParsedObject.FIELDNAME_CONTENT, ParsedObject.FIELDNAME_DESCRIPTION, ParsedObject.FIELDNAME_FIELDS,
                           ParsedObject.FIELDNAME_KEY, ParsedObject.FIELDNAME_KEYWORDS, ParsedObject.FIELDNAME_LANGUAGE,
                           ParsedObject.FIELDNAME_SCORE, ParsedObject.FIELDNAME_TITLE, ParsedObject.FIELDNAME_TYPE,
                           ParsedObject.FIELDNAME_URL, ParsedObject.FIELDNAME_CLASSNAME};
                            
        Query query= null;
        try
        {
            query = MultiFieldQueryParser.parse(searchString, searchFields, analyzer);
//          Query query = QueryParser.parse(searchString, ParsedObject.FIELDNAME_CONTENT, analyzer);
        }
        catch (ParseException e)
        {
            logger.info("Failed to parse query " + searchString);
            return null;
        }
        
        try
        {
            hits = searcher.search(query);
        }
        catch (IOException e)
        {
           logger.error("Error while peforming search.", e);
           return null;
        }

        // Copy hits to the result list
        int hitCount = hits.length();
        Document doc = null;
        SearchResults results = new SearchResults(hitCount);
        for (int counter = 0; counter < hitCount; counter++)
        {            
            ParsedObject result = new BaseParsedObject();
            try
            {
                doc = hits.doc(counter);
                addFieldsToParsedObject(doc, result);
                
                result.setScore(hits.score(counter));
                result.setType(doc.getField(ParsedObject.FIELDNAME_TYPE).stringValue());
                result.setKey(doc.getField(ParsedObject.FIELDNAME_KEY).stringValue());
                result.setDescription(doc.getField(ParsedObject.FIELDNAME_DESCRIPTION).stringValue());
                result.setTitle(doc.getField(ParsedObject.FIELDNAME_TITLE).stringValue());
                result.setContent(doc.getField(ParsedObject.FIELDNAME_CLASSNAME).stringValue());
                Field language = doc.getField(ParsedObject.FIELDNAME_LANGUAGE);
                if (language != null)
                {
                	result.setLanguage(language.stringValue());
                }
                Field classname = doc.getField(ParsedObject.FIELDNAME_CLASSNAME);
                if (classname != null)
                {
                	result.setClassName(classname.stringValue());
                }
                Field url = doc.getField(ParsedObject.FIELDNAME_URL);
                if (url != null)
                {
                    result.setURL(new URL(url.stringValue()));
                }
                
                results.add(counter, result);
            }
            catch (Exception ioe)
            {
                logger.error("Exception", ioe);
            }
        }

        if (searcher != null)
        {
            try
            {
                searcher.close();
            }
            catch (IOException ioe)
            {
                logger.error("Closing Searcher", ioe);
            }
        }
        return results;
    }
    
    private void addFieldsToParsedObject(Document doc, ParsedObject o)
    {
        try
        {
            MultiMap multiKeywords = new MultiHashMap();
            MultiMap multiFields = new MultiHashMap();
            HashMap fieldMap = new HashMap();
            
            Field classNameField = doc.getField(ParsedObject.FIELDNAME_CLASSNAME);
            if(classNameField != null)
            {
                String className = classNameField.stringValue();
                o.setClassName(className);
                ObjectHandler handler = HandlerFactory.getHandler(className);
                
                Set fields = handler.getFields();
                addFieldsToMap(doc, fields, multiFields);
                addFieldsToMap(doc, fields, fieldMap);
                
                Set keywords = handler.getKeywords();
                addFieldsToMap(doc, keywords, multiKeywords);
            }
            
            o.setMultiKeywords(multiKeywords);
            o.setMultiFields(multiFields);
            o.setFields(fieldMap);
        }
        catch(Exception e)
        {
            logger.error("Error trying to add fields to parsed object.", e);
        }
    }
    
    private void addFieldsToMap(Document doc, Set fieldNames, Map fields)
    {
        Iterator fieldIter = fieldNames.iterator();
        while(fieldIter.hasNext())
        {
            String fieldName = (String)fieldIter.next();
            Field[] docFields = doc.getFields(fieldName);
            if(fields != null)
            {
                for(int i=0; i<docFields.length; i++)
                {
                    Field field = docFields[i];
                    if(field != null)
                    {
                        String value = field.stringValue();
                        fields.put(fieldName, value);
                    }
                }
            }
        }
    }

    /**
     * 
     * @return 
     */
    public String[] getSearchSets()
    {
        return null;
    }

    /**
     * 
     * @see org.apache.jetspeed.services.search.SearchService#add(java.lang.Object)
     * @param o
     * @return 
     */
    public boolean add(Object o)
    {
        Collection c = new ArrayList(1);
        c.add(o);

        return add(c);
    }

    /**
     * 
     * @see org.apache.jetspeed.services.search.SearchService#add(java.lang.Collection)
     * @param c
     * @return 
     */
    public boolean add(Collection c)
    {
        boolean result = false;

        IndexWriter indexWriter;
        try
        {
            indexWriter = new IndexWriter(rootDir, new StandardAnalyzer(), false);
        }
        catch (IOException e)
        {
            logger.error("Error while creating index writer. Skipping add...", e);
            return result;
        }

        Iterator it = c.iterator();
        while (it.hasNext()) 
        {
            Object o = it.next();
            // Look up appropriate handler
            ObjectHandler handler = null;
            try
            {
                handler = HandlerFactory.getHandler(o);
            }
            catch (Exception e)
            {
                logger.error("Failed to create hanlder for object " + o.getClass().getName());
                continue;
            }

            // Parse the object
            ParsedObject parsedObject = handler.parseObject(o);

            // Create document
            Document doc = new Document();

            // Populate document from the parsed object
            if (parsedObject.getKey() != null)
            {
                doc.add(Field.Keyword(ParsedObject.FIELDNAME_KEY, parsedObject.getKey()));
            }
            if (parsedObject.getType() != null)
            {
                doc.add(Field.Text(ParsedObject.FIELDNAME_TYPE, parsedObject.getType()));
            }
            if (parsedObject.getTitle() != null)
            {
                doc.add(Field.Text(ParsedObject.FIELDNAME_TITLE, parsedObject.getTitle()));
            }
            if (parsedObject.getDescription() != null)
            {
                doc.add(Field.Text(ParsedObject.FIELDNAME_DESCRIPTION, parsedObject.getDescription()));
            }
            if (parsedObject.getContent() != null)
            {
                doc.add(Field.Text(ParsedObject.FIELDNAME_CONTENT, parsedObject.getContent()));
            }
            if (parsedObject.getLanguage() != null)
            {
                doc.add(Field.Text(ParsedObject.FIELDNAME_LANGUAGE, parsedObject.getLanguage()));   
            }
            if (parsedObject.getURL() != null)
            {
                doc.add(Field.Text(ParsedObject.FIELDNAME_URL, parsedObject.getURL().toString()));
            }
            if(parsedObject.getClassName() != null)
            {
                doc.add(Field.Text(ParsedObject.FIELDNAME_CLASSNAME, parsedObject.getClassName()));
            }

            MultiMap multiKeywords = parsedObject.getMultiKeywords();
            addFieldsToDocument(doc, multiKeywords, KEYWORD);
            
            MultiMap multiFields = parsedObject.getMultiFields();
            addFieldsToDocument(doc, multiFields, TEXT);
            
            Map fields = parsedObject.getFields();
            addFieldsToDocument(doc, fields, TEXT);

            // Add the document to search index
            try
            {
                indexWriter.addDocument(doc);
            }
            catch (IOException e)
            {
               logger.error("Error adding document to index.", e);
            }
            logger.debug("Index Document Count = " + indexWriter.docCount());
            logger.info("Added '" + parsedObject.getTitle() + "' to index");
            result = true;
        }

        try
        {
            indexWriter.optimize();
        }
        catch (IOException e)
        {
            logger.error("Error while trying to optimize index.");
        }
        finally
        {
            try
            {
                indexWriter.close();
            }
            catch (IOException e)
            {
               logger.error("Error while closing index writer.", e);
            }
        }

        return result;
    }
    
    private void addFieldsToDocument(Document doc, Map fields, int type)
    {
        if(fields != null)
        {
            Iterator keyIter = fields.keySet().iterator();
            while(keyIter.hasNext())
            {
                Object key = keyIter.next();
                if(key != null)
                {
                    Object values = fields.get(key);
                    if(values != null)
                    {
                        if(values instanceof Collection)
                        {
                            Iterator valueIter = ((Collection)values).iterator();
                            while(valueIter.hasNext())
                            {
                                Object value = valueIter.next();
                                if(value != null)
                                {
                                    if(type == TEXT)
                                    {
                                        doc.add(Field.Text(key.toString(), value.toString()));
                                    }
                                    else
                                    {
                                        doc.add(Field.Keyword(key.toString(), value.toString()));
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(type == TEXT)
                            {
                                doc.add(Field.Text(key.toString(), values.toString()));
                            }
                            else
                            {
                                doc.add(Field.Keyword(key.toString(), values.toString()));
                            }
                        }
                    }
                }
            } 
        }
    }

    /**
     * 
     * @see org.apache.jetspeed.services.search.SearchService#remove(java.lang.Object)
     * @param o
     * @return 
     */
    public boolean remove(Object o)
    {
        Collection c = new ArrayList(1);
        c.add(o);

        return remove(c);
    }

    /**
     * 
     * @see org.apache.jetspeed.services.search.SearchService#remove(java.lang.Collection)
     * @param c
     * @return 
     */
    public boolean remove(Collection c)
    {
        boolean result = false;

        try 
        {
            IndexReader indexReader = IndexReader.open(this.rootDir);

            Iterator it = c.iterator();
            while (it.hasNext()) 
            {
                Object o = it.next();
                // Look up appropriate handler
                ObjectHandler handler = HandlerFactory.getHandler(o);

                // Parse the object
                ParsedObject parsedObject = handler.parseObject(o);

                // Create term
                Term term = null;

                if (parsedObject.getKey() != null)
                {
                    term = new Term(ParsedObject.FIELDNAME_KEY, parsedObject.getKey());
                    // Remove the document from search index
                    int rc = indexReader.delete(term);
                    logger.info("Attempted to delete '" + term.toString() + "' from index, documents deleted = " + rc);
                    //System.out.println("Attempted to delete '" + term.toString() + "' from index, documents deleted = " + rc);
                    result = rc > 0;
                }
            }

            indexReader.close();

            IndexWriter indexWriter = new IndexWriter(rootDir, new StandardAnalyzer(), false);
            indexWriter.optimize();
            indexWriter.close();

        }
        catch (Exception e)
        {
            logger.error("Exception", e);
            result = false;
        }

        return result;
    }

    /**
     * 
     * @see org.apache.jetspeed.services.search.SearchService#update(java.lang.Object)
     * @param o
     * @return 
     */
    public boolean update(Object o)
    {
        Collection c = new ArrayList(1);
        c.add(o);

        return update(c);
    }
    /**
     * Updates an index entry. For now, it's a remove and add.
     * 
     * @param c
     * @return 
     * @see org.apache.jetspeed.services.search.SearchService#update(java.lang.Collection)
     */
    public boolean update(Collection c)
    {
        boolean result = false;

        try
        {
            // Delete entries from index
            remove(c);
            result = true;
        }
        catch (Throwable e)
        {
            logger.error("Exception",  e);
        }

        try
        {
            // Add entries to index
            add(c);
            result = true;
        }
        catch (Throwable e)
        {
            logger.error("Exception",  e);
        }

        return false;
    }

}
