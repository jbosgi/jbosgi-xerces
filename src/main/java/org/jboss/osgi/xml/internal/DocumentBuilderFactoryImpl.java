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
package org.jboss.osgi.xml.internal;

//$Id: XercesParserActivator.java 91417 2009-07-20 09:25:44Z thomas.diesler@jboss.com $

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * A DocumentBuilderFactory that gets loaded from this bundle.
 * 
 * @author thomas.diesler@jboss.com
 * @since 21-Jul-2009
 */
public class DocumentBuilderFactoryImpl extends org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
{
   @Override
   public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException
   {
      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
         return super.newDocumentBuilder();
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(ctxLoader);
      }
   }
}