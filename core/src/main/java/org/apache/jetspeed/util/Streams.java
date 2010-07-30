/* ====================================================================
 * BBC Syndication Server 1.0
 *
 * Copyright (c) 2002-2003 The BBC.  All rights reserved.
 *
 */

package org.apache.jetspeed.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * <P>Utility functions related to Streams</P>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: Streams.java,v 1.2 2003/04/23 01:44:52 taylor Exp $
 */
public class Streams
{
  static final int BLOCK_SIZE=4096;

  public static void drain(InputStream r,OutputStream w) throws IOException
  {
      byte[] bytes=new byte[BLOCK_SIZE];
      try
      {
        int length=r.read(bytes);
        while(length!=-1)
        {
            if(length!=0)
                {
                    w.write(bytes,0,length);
                }
            length=r.read(bytes);
        }
    }
    finally
    {
      bytes=null;
    }

  }

  public static void drain(Reader r,Writer w) throws IOException
  {
    char[] bytes=new char[BLOCK_SIZE];
    try
    {
        int length=r.read(bytes);
        while(length!=-1)
        {
            if(length!=0)
            {
                w.write(bytes,0,length);
            }
            length=r.read(bytes);
        }
    }
    finally
    {
        bytes=null;
    }

  }

  public static void drain(Reader r,OutputStream os) throws IOException
  {
        Writer w=new OutputStreamWriter(os);
        drain(r,w);
        w.flush();
  }

  public static byte[] drain(InputStream r) throws IOException
  {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        drain(r,bytes);
        return bytes.toByteArray();
  }

  public static void drain(InputStream is, Writer w) throws IOException
  {
      Reader r = new InputStreamReader(is);
      drain(r,w);
      w.flush();
  }

  public static String getAsString(InputStream is)
  {
      int c=0;
      char lineBuffer[]=new char[128], buf[]=lineBuffer;
      int room= buf.length, offset=0;
      try
      {
          loop: while (true)
          {
            // read chars into a buffer which grows as needed
                switch (c = is.read() )
                {
                    case -1: break loop;

                    default: if (--room < 0)
                             {
                                 buf = new char[offset + 128];
                                 room = buf.length - offset - 1;
                                 System.arraycopy(lineBuffer, 0,
                                          buf, 0, offset);
                                 lineBuffer = buf;
                             }
                             buf[offset++] = (char) c;
                             break;
                }
          }
      }
      catch(IOException ioe)
      {
          ioe.printStackTrace();
      }
      if ((c == -1) && (offset == 0))
      {
          return null;
      }
      return String.copyValueOf(buf, 0, offset);
  }

  public static String getAsString(Reader is)
  {
      int c=0;
      char lineBuffer[]=new char[128], buf[]=lineBuffer;
      int room= buf.length, offset=0;
      try
      {
          loop: while (true)
          {
            // read chars into a buffer which grows as needed
                switch (c = is.read() )
                {
                    case -1: break loop;

                    default: if (--room < 0)
                             {
                                 buf = new char[offset + 128];
                                 room = buf.length - offset - 1;
                                 System.arraycopy(lineBuffer, 0,
                                          buf, 0, offset);
                                 lineBuffer = buf;
                             }
                             buf[offset++] = (char) c;
                             break;
                }
          }
      }
      catch(IOException ioe)
      {
          ioe.printStackTrace();
      }
      if ((c == -1) && (offset == 0))
      {
          return null;
      }
      return String.copyValueOf(buf, 0, offset);
  }



}

