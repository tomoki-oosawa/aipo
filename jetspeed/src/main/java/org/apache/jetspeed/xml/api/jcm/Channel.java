/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * $Id$
 */

package org.apache.jetspeed.xml.api.jcm;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision$ $Date$
**/
public class Channel implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private Topics _topics;

    private java.util.Vector _itemList;

    private java.lang.String _title;

    private java.lang.String _link;

    private java.lang.String _description;

    private Image _image;

    private Textinput _textinput;

    private java.lang.String _rating;

    private java.lang.String _copyright;

    private java.lang.String _pubDate;

    private java.lang.String _lastBuildDate;

    private java.lang.String _docs;

    private java.lang.String _managingEditor;

    private java.lang.String _webMaster;

    private java.lang.String _language;


      //----------------/
     //- Constructors -/
    //----------------/

    public Channel() {
        super();
        _itemList = new Vector();
    } //-- org.apache.jetspeed.xml.api.jcm.Channel()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param vItem
    **/
    public void addItem(Item vItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _itemList.addElement(vItem);
    } //-- void addItem(Item) 

    /**
    **/
    public java.util.Enumeration enumerateItem()
    {
        return _itemList.elements();
    } //-- java.util.Enumeration enumerateItem() 

    /**
    **/
    public java.lang.String getCopyright()
    {
        return this._copyright;
    } //-- java.lang.String getCopyright() 

    /**
    **/
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
    **/
    public java.lang.String getDocs()
    {
        return this._docs;
    } //-- java.lang.String getDocs() 

    /**
    **/
    public Image getImage()
    {
        return this._image;
    } //-- Image getImage() 

    /**
     * 
     * @param index
    **/
    public Item getItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _itemList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Item) _itemList.elementAt(index);
    } //-- Item getItem(int) 

    /**
    **/
    public Item[] getItem()
    {
        int size = _itemList.size();
        Item[] mArray = new Item[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Item) _itemList.elementAt(index);
        }
        return mArray;
    } //-- Item[] getItem() 

    /**
    **/
    public int getItemCount()
    {
        return _itemList.size();
    } //-- int getItemCount() 

    /**
    **/
    public java.lang.String getLanguage()
    {
        return this._language;
    } //-- java.lang.String getLanguage() 

    /**
    **/
    public java.lang.String getLastBuildDate()
    {
        return this._lastBuildDate;
    } //-- java.lang.String getLastBuildDate() 

    /**
    **/
    public java.lang.String getLink()
    {
        return this._link;
    } //-- java.lang.String getLink() 

    /**
    **/
    public java.lang.String getManagingEditor()
    {
        return this._managingEditor;
    } //-- java.lang.String getManagingEditor() 

    /**
    **/
    public java.lang.String getPubDate()
    {
        return this._pubDate;
    } //-- java.lang.String getPubDate() 

    /**
    **/
    public java.lang.String getRating()
    {
        return this._rating;
    } //-- java.lang.String getRating() 

    /**
    **/
    public Textinput getTextinput()
    {
        return this._textinput;
    } //-- Textinput getTextinput() 

    /**
    **/
    public java.lang.String getTitle()
    {
        return this._title;
    } //-- java.lang.String getTitle() 

    /**
    **/
    public Topics getTopics()
    {
        return this._topics;
    } //-- Topics getTopics() 

    /**
    **/
    public java.lang.String getWebMaster()
    {
        return this._webMaster;
    } //-- java.lang.String getWebMaster() 

    /**
    **/
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * 
     * @param out
    **/
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler) 

    /**
    **/
    public void removeAllItem()
    {
        _itemList.removeAllElements();
    } //-- void removeAllItem() 

    /**
     * 
     * @param index
    **/
    public Item removeItem(int index)
    {
        Object obj = _itemList.elementAt(index);
        _itemList.removeElementAt(index);
        return (Item) obj;
    } //-- Item removeItem(int) 

    /**
     * 
     * @param copyright
    **/
    public void setCopyright(java.lang.String copyright)
    {
        this._copyright = copyright;
    } //-- void setCopyright(java.lang.String) 

    /**
     * 
     * @param description
    **/
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * 
     * @param docs
    **/
    public void setDocs(java.lang.String docs)
    {
        this._docs = docs;
    } //-- void setDocs(java.lang.String) 

    /**
     * 
     * @param image
    **/
    public void setImage(Image image)
    {
        this._image = image;
    } //-- void setImage(Image) 

    /**
     * 
     * @param index
     * @param vItem
    **/
    public void setItem(int index, Item vItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _itemList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _itemList.setElementAt(vItem, index);
    } //-- void setItem(int, Item) 

    /**
     * 
     * @param itemArray
    **/
    public void setItem(Item[] itemArray)
    {
        //-- copy array
        _itemList.removeAllElements();
        for (int i = 0; i < itemArray.length; i++) {
            _itemList.addElement(itemArray[i]);
        }
    } //-- void setItem(Item) 

    /**
     * 
     * @param language
    **/
    public void setLanguage(java.lang.String language)
    {
        this._language = language;
    } //-- void setLanguage(java.lang.String) 

    /**
     * 
     * @param lastBuildDate
    **/
    public void setLastBuildDate(java.lang.String lastBuildDate)
    {
        this._lastBuildDate = lastBuildDate;
    } //-- void setLastBuildDate(java.lang.String) 

    /**
     * 
     * @param link
    **/
    public void setLink(java.lang.String link)
    {
        this._link = link;
    } //-- void setLink(java.lang.String) 

    /**
     * 
     * @param managingEditor
    **/
    public void setManagingEditor(java.lang.String managingEditor)
    {
        this._managingEditor = managingEditor;
    } //-- void setManagingEditor(java.lang.String) 

    /**
     * 
     * @param pubDate
    **/
    public void setPubDate(java.lang.String pubDate)
    {
        this._pubDate = pubDate;
    } //-- void setPubDate(java.lang.String) 

    /**
     * 
     * @param rating
    **/
    public void setRating(java.lang.String rating)
    {
        this._rating = rating;
    } //-- void setRating(java.lang.String) 

    /**
     * 
     * @param textinput
    **/
    public void setTextinput(Textinput textinput)
    {
        this._textinput = textinput;
    } //-- void setTextinput(Textinput) 

    /**
     * 
     * @param title
    **/
    public void setTitle(java.lang.String title)
    {
        this._title = title;
    } //-- void setTitle(java.lang.String) 

    /**
     * 
     * @param topics
    **/
    public void setTopics(Topics topics)
    {
        this._topics = topics;
    } //-- void setTopics(Topics) 

    /**
     * 
     * @param webMaster
    **/
    public void setWebMaster(java.lang.String webMaster)
    {
        this._webMaster = webMaster;
    } //-- void setWebMaster(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static org.apache.jetspeed.xml.api.jcm.Channel unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.apache.jetspeed.xml.api.jcm.Channel) Unmarshaller.unmarshal(org.apache.jetspeed.xml.api.jcm.Channel.class, reader);
    } //-- org.apache.jetspeed.xml.api.jcm.Channel unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
