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

import javax.xml.parsers.SAXParserFactory;

import org.jboss.osgi.spi.capability.Capability;
import org.jboss.osgi.spi.testing.OSGiRuntime;

/**
 * Adds the XML parser capability to the {@link OSGiRuntime}
 * under test. 
 * 
 * It is ignored if the {@link SAXParserFactory} is already registered.
 * 
 * Installed bundles: jboss-osgi-apache-xerces.jar
 * 
 * @author thomas.diesler@jboss.com
 * @since 05-May-2009
 */
public class XMLParserCapability extends Capability
{
   public XMLParserCapability()
   {
      super(SAXParserFactory.class.getName());
      
      addBundle("bundles/jboss-osgi-apache-xerces.jar");
   }
}