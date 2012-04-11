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

import static org.jboss.osgi.xml.internal.XMLLogger.LOGGER;

import java.util.Arrays;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.jboss.osgi.xml.XMLParserCapability;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.xml.XMLParserActivator;

/**
 * Activate the XML parser using {@link XMLParserActivatorExt}
 *
 * @author thomas.diesler@jboss.com
 * @since 29-Apr-2009
 */
public class XMLParserActivatorExt extends XMLParserActivator {

    public void start(BundleContext context) throws Exception {
        super.start(context);
        logSAXParserFactory(context);
        logDOMParserFactory(context);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setDOMProperties(DocumentBuilderFactory factory, Hashtable props) {
        super.setDOMProperties(factory, props);

        boolean xinclude = true;
        Boolean validating = (Boolean) props.get(PARSER_VALIDATING);
        Boolean namespaceaware = (Boolean) props.get(PARSER_NAMESPACEAWARE);

        // check if this parser can be configured to be xinclude aware
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceaware);
        factory.setXIncludeAware(true);
        try {
            factory.newDocumentBuilder();
        } catch (Exception pce_inc) {
            xinclude = false;
        }

        // set the factory values
        factory.setXIncludeAware(xinclude);

        // set the OSGi service properties
        props.put(XMLParserCapability.PARSER_XINCLUDEAWARE, new Boolean(xinclude));
        props.put(XMLParserCapability.PARSER_PROVIDER, XMLParserCapability.PROVIDER_JBOSS_OSGI);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setSAXProperties(SAXParserFactory factory, Hashtable props) {
        super.setSAXProperties(factory, props);

        boolean xinclude = true;
        Boolean validating = (Boolean) props.get(PARSER_VALIDATING);
        Boolean namespaceaware = (Boolean) props.get(PARSER_NAMESPACEAWARE);

        // check if this parser can be configured to be xinclude aware
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceaware);
        factory.setXIncludeAware(true);
        try {
            factory.newSAXParser();
        } catch (Exception pce_inc) {
            xinclude = false;
        }

        // set the factory values
        factory.setXIncludeAware(xinclude);

        // set the OSGi service properties
        props.put(XMLParserCapability.PARSER_XINCLUDEAWARE, new Boolean(xinclude));
        props.put(XMLParserCapability.PARSER_PROVIDER, XMLParserCapability.PROVIDER_JBOSS_OSGI);
    }

    private void logSAXParserFactory(BundleContext context) throws InvalidSyntaxException {
        ServiceReference[] saxRefs = context.getServiceReferences(SAXParserFactory.class.getName(), null);
        if (saxRefs != null) {
            for (ServiceReference sref : saxRefs) {
                Object factory = context.getService(sref);
                LOGGER.debugf("SAXParserFactory: %s", factory.getClass().getName());

                for (String key : sref.getPropertyKeys()) {
                    Object value = sref.getProperty(key);
                    if (key.equals(Constants.OBJECTCLASS)) {
                        value = Arrays.asList((String[]) value);
                    }
                    LOGGER.debugf("   %s=%s", key, value);
                }
            }
        } else {
            LOGGER.warnNoSAXParserFactoryRegistered();
        }
    }

    private void logDOMParserFactory(BundleContext context) throws InvalidSyntaxException {
        ServiceReference[] domRefs = context.getServiceReferences(DocumentBuilderFactory.class.getName(), null);
        if (domRefs != null) {
            for (ServiceReference sref : domRefs) {
                Object factory = context.getService(sref);
                LOGGER.debugf("DocumentBuilderFactory: %s", factory.getClass().getName());

                for (String key : sref.getPropertyKeys()) {
                    Object value = sref.getProperty(key);
                    if (key.equals(Constants.OBJECTCLASS)) {
                        value = Arrays.asList((String[]) value);
                    }
                    LOGGER.debugf("   %s=%s", key, value);
                }
            }
        } else {
            LOGGER.warnNoDocumentBuilderFactoryRegistered();
        }
    }
}