/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.osgi.xml;

//$Id$

import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.jboss.osgi.common.log.LogServiceTracker;
import org.jboss.osgi.xml.internal.XMLParserActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Activate the XML parser using {@link XMLParserActivatorExt}
 * 
 * @author thomas.diesler@jboss.com
 * @since 29-Apr-2009
 */
public class XMLParserActivatorExt extends XMLParserActivator
{
   /*
    * Service property specifying if factory is configured to support XInclude aware parsers. 
    * The value is of type <code>Boolean</code>.
    */
   public static final String PARSER_XINCLUDEAWARE = "parser.xincludeAware";
   
   private LogService log;

   public void start(BundleContext context) throws Exception
   {
      log = new LogServiceTracker(context);
      
      super.start(context);
      
      logSAXParserFactory(context);
      logDOMParserFactory(context);
   }
   
   @Override
   @SuppressWarnings("unchecked")
   public void setDOMProperties(DocumentBuilderFactory factory, Hashtable props)
   {
      // check if this parser can be configured to validate
      boolean validating = true;
      factory.setValidating(true);
      factory.setNamespaceAware(false);
      try
      {
         factory.newDocumentBuilder();
      }
      catch (Exception pce_val)
      {
         validating = false;
      }

      // check if this parser can be configured to be namespaceaware
      boolean namespaceaware = true;
      factory.setValidating(false);
      factory.setNamespaceAware(true);
      try
      {
         factory.newDocumentBuilder();
      }
      catch (Exception pce_nsa)
      {
         namespaceaware = false;
      }

      // check if this parser can be configured to be xinclude aware
      boolean xinclude = true;
      factory.setValidating(validating);
      factory.setNamespaceAware(namespaceaware);
      factory.setXIncludeAware(true);
      try
      {
         factory.newDocumentBuilder();
      }
      catch (Exception pce_inc)
      {
         xinclude = false;
      }

      // set the factory values
      factory.setValidating(validating);
      factory.setNamespaceAware(namespaceaware);
      factory.setXIncludeAware(xinclude);

      // set the OSGi service properties
      props.put(PARSER_VALIDATING, new Boolean(validating));
      props.put(PARSER_NAMESPACEAWARE, new Boolean(namespaceaware));
      props.put(PARSER_XINCLUDEAWARE, new Boolean(xinclude));
   }


   @Override
   @SuppressWarnings("unchecked")
   public void setSAXProperties(SAXParserFactory factory, Hashtable props)
   {
      // check if this parser can be configured to validate
      boolean validating = true;
      factory.setValidating(true);
      factory.setNamespaceAware(false);
      try
      {
         factory.newSAXParser();
      }
      catch (Exception pce_val)
      {
         validating = false;
      }

      // check if this parser can be configured to be namespaceaware
      boolean namespaceaware = true;
      factory.setValidating(false);
      factory.setNamespaceAware(true);
      try
      {
         factory.newSAXParser();
      }
      catch (Exception pce_nsa)
      {
         namespaceaware = false;
      }

      // check if this parser can be configured to be xinclude aware
      boolean xinclude = true;
      factory.setValidating(validating);
      factory.setNamespaceAware(namespaceaware);
      factory.setXIncludeAware(true);
      try
      {
         factory.newSAXParser();
      }
      catch (Exception pce_inc)
      {
         xinclude = false;
      }

      // set the factory values
      factory.setValidating(validating);
      factory.setNamespaceAware(namespaceaware);
      factory.setXIncludeAware(xinclude);

      // set the OSGi service properties
      props.put(PARSER_NAMESPACEAWARE, new Boolean(namespaceaware));
      props.put(PARSER_VALIDATING, new Boolean(validating));
      props.put(PARSER_XINCLUDEAWARE, new Boolean(xinclude));
   }

   private void logSAXParserFactory(BundleContext context)
   {
      ServiceReference saxRef = context.getServiceReference(SAXParserFactory.class.getName());
      if (saxRef != null)
      {
         Object factory = context.getService(saxRef);
         log.log(LogService.LOG_DEBUG, "SAXParserFactory: " + factory.getClass().getName());
         
         for (String key : saxRef.getPropertyKeys())
         {
            Object value = saxRef.getProperty(key);
            log.log(LogService.LOG_DEBUG, "   " + key + "=" + value);
         }
      }
      else
      {
         log.log(LogService.LOG_WARNING, "No SAXParserFactory registered");
      }
   }


   private void logDOMParserFactory(BundleContext context)
   {
      ServiceReference domRef = context.getServiceReference(DocumentBuilderFactory.class.getName());
      if (domRef != null)
      {
         Object factory = context.getService(domRef);
         log.log(LogService.LOG_DEBUG, "DocumentBuilderFactory: " + factory.getClass().getName());
         
         for (String key : domRef.getPropertyKeys())
         {
            Object value = domRef.getProperty(key);
            log.log(LogService.LOG_DEBUG, "   " + key + "=" + value);
         }
      }
      else
      {
         log.log(LogService.LOG_WARNING, "No DocumentBuilderFactory registered");
      }
   }
}