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

import javax.xml.parsers.SAXParserFactory;

/**
 * Adds the XML parser capability.
 * <p/>
 * It is ignored if the {@link SAXParserFactory} is already registered.
 * <p/>
 * Installed bundles: jboss-osgi-xerces.jar
 *
 * @author thomas.diesler@jboss.com
 * @since 05-May-2009
 */
public class XMLParserCapability {
    /*
    * Service property key for the 'provider' of this service.
    */
    public static final String PARSER_PROVIDER = "provider";
    /*
    * Service property value for the 'provider' of this service.
    * The value is <code>jboss.osgi</code>.
    */
    public static final String PROVIDER_JBOSS_OSGI = "jboss";
    /*
    * Service property specifying if factory is configured to support XInclude aware parsers.
    * The value is of type <code>Boolean</code>.
    */
    public static final String PARSER_XINCLUDEAWARE = "parser.xincludeAware";

}