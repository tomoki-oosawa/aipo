/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2014 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aimluck.eip.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometEvent.EventType;
import org.apache.catalina.CometProcessor;

import com.aimluck.eip.common.ALBaseUser;

/**
 *
 */
public class ALCometServlet extends HttpServlet implements CometProcessor {

  private static final long serialVersionUID = -3319969086920456957L;

  public static final String KEY_MESSAGE_SENDER =
    "com.aimluck.eip.http.ALCometServlet.MessageSender";

  public static final String KEY_EVENT_WORKER =
    "com.aimluck.eip.http.ALCometServlet.EventWorker";

  protected transient ConcurrentLinkedQueue<Event> events = null;

  protected transient ConcurrentHashMap<HttpServletResponse, String> connections =
    null;

  protected transient MessageSender sender = null;

  protected transient EventWorker worker = null;

  @Override
  public void init() {
    events = new ConcurrentLinkedQueue<Event>();
    connections = new ConcurrentHashMap<HttpServletResponse, String>();

    this.sender = new MessageSender();
    this.sender.setDaemon(true);
    this.sender.start();

    this.worker = new EventWorker();
    this.worker.setDaemon(true);
    this.worker.start();

    getServletContext().setAttribute(KEY_EVENT_WORKER, worker);
    getServletContext().setAttribute(KEY_MESSAGE_SENDER, sender);

  }

  @Override
  public void destroy() {
    sender.quit();
    sender = null;
    worker.quit();
    worker = null;
    events.clear();
    connections.clear();
  }

  /**
   * @param event
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void event(CometEvent event) throws IOException, ServletException {

    try {
      HttpServletRequest request = event.getHttpServletRequest();
      HttpServletResponse response = event.getHttpServletResponse();
      ALBaseUser user = getUser(request);
      if (user == null) {
        return;
      }
      enqueue(new Event(event.getEventType(), request, response));
    } catch (Throwable t) {
      log(t.getMessage(), t);
    }
  }

  protected void enqueue(Event event) {
    events.add(event);
  }

  protected ALBaseUser getUser(HttpServletRequest httpServletRequest) {
    try {
      HttpSession session = httpServletRequest.getSession(false);
      if (session == null) {
        return null;
      }

      return (ALBaseUser) session.getAttribute("turbine.user");
    } catch (Throwable ignore) {
      // ignore
    }
    return null;
  }

  public class Event {

    private final EventType eventType;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    public Event(EventType eventType, HttpServletRequest request,
        HttpServletResponse response) {
      this.eventType = eventType;
      this.request = request;
      this.response = response;
    }

    public EventType getEventType() {
      return eventType;
    }

    public HttpServletRequest getRequest() {
      return request;
    }

    public HttpServletResponse getResponse() {
      return response;
    }
  }

  public class EventWorker extends Thread {

    protected boolean running = true;

    public EventWorker() {
    }

    public void quit() {
      running = false;
      this.interrupt();
    }

    public void open(String name, HttpServletResponse response) {
      connections.put(response, name);
    }

    public void close(HttpServletResponse response) {
      connections.remove(response);
    }

    @Override
    public void run() {
      while (running) {
        try {
          Event event = events.poll();
          if (event != null) {
            EventType type = event.getEventType();
            ALBaseUser user = getUser(event.getRequest());
            if (user != null) {
              String name = user.getUserName();
              switch (type) {
                case BEGIN:
                  open(name, event.getResponse());
                  break;
                case READ:
                  open(name, event.getResponse());
                  break;
                case END:
                  close(event.getResponse());
                  break;
                case ERROR:
                  close(event.getResponse());
                  break;
                default:
                  break;
              }
            }
          }
          try {
            Thread.sleep(100);
          } catch (Throwable ignore) {
            //
          }
        } catch (Throwable t) {
          log(t.getMessage(), t);
        }
      }
    }
  }

  public class Message {

    private final List<String> recipients;

    private final String message;

    public Message(List<String> recipients, String message) {
      this.recipients = recipients;
      this.message = message;
    }

    /**
     * @return message
     */
    public String getMessage() {
      return message;
    }

    /**
     * @return recipients
     */
    public List<String> getRecipients() {
      return recipients;
    }

  }

  public class MessageSender extends Thread {

    private final BlockingQueue<Message> messages =
      new LinkedBlockingQueue<Message>();

    protected boolean running = true;

    public void quit() {
      running = false;
      messages.clear();
      this.interrupt();
    }

    public synchronized void sendMessage(List<String> recipients, String message) {
      try {
        messages.put(new Message(recipients, message));
      } catch (InterruptedException e) {
        //
      }
    }

    @Override
    public void run() {
      while (running) {
        try {
          Message message = messages.poll(1000, TimeUnit.SECONDS);
          if (message != null) {
            Iterator<Entry<HttpServletResponse, String>> iterator =
              connections.entrySet().iterator();
            while (iterator.hasNext()) {
              Entry<HttpServletResponse, String> next = iterator.next();
              HttpServletResponse response = next.getKey();
              response.setContentType("text/plain");
              response.setCharacterEncoding("UTF-8");
              try {
                PrintWriter writer = response.getWriter();
                writer.write(next.getValue());
                writer.flush();
                writer.close();
                response.flushBuffer();
              } catch (IOException e) {
                log(e.getMessage(), e);
              }
            }
          }
          try {
            Thread.sleep(100);
          } catch (Throwable ignore) {
            //
          }
        } catch (Throwable t) {
          log(t.getMessage(), t);
          //
        }
      }
    }
  }
}
