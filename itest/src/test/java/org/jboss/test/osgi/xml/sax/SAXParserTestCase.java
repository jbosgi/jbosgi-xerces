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
package org.jboss.test.osgi.xml.sax;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.osgi.testing.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.inject.Inject;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * A test that uses a SAX parser to read an XML document.
 *
 * @author thomas.diesler@jboss.com
 * @see http://www.osgi.org/javadoc/r4v41/org/osgi/util/xml/XMLParserActivator.html
 * @since 21-Jul-2009
 */
@RunWith(Arquillian.class)
public class SAXParserTestCase {
    @Inject
    public BundleContext context;
    @Inject
    public Bundle bundle;

    @Deployment
    public static JavaArchive createdeployment() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "sax-parser.jar");
        archive.addClasses(SAXParserTestCase.class);
        archive.addAsResource("simple/simple.xml");
        archive.setManifest(new Asset() {
            public InputStream openStream() {
                OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
                builder.addBundleSymbolicName(archive.getName());
                builder.addBundleManifestVersion(2);
                builder.addImportPackages(SAXParser.class, SAXException.class, DefaultHandler.class);
                builder.addImportPackages(ServiceTracker.class);
                return builder.openStream();
            }
        });
        return archive;
    }

    @Test
    public void testSAXParser() throws Exception {
        ServiceReference[] srefs = context.getServiceReferences(SAXParserFactory.class.getName(), "(provider=jboss)");
        if (srefs == null)
            throw new IllegalStateException("SAXParserFactory not available");

        SAXParserFactory factory = (SAXParserFactory) context.getService(srefs[0]);
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        SAXParser saxParser = factory.newSAXParser();
        URL resURL = bundle.getResource("simple/simple.xml");

        SAXHandler saxHandler = new SAXHandler();
        saxParser.parse(resURL.openStream(), saxHandler);
        assertEquals("content", saxHandler.getContent());
    }

    @Test
    public void testDOMParserTracker() throws Exception {
        final StringBuffer messages = new StringBuffer();
        ServiceTracker tracker = new ServiceTracker(context, SAXParserFactory.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                SAXParserFactory factory = (SAXParserFactory) super.addingService(reference);
                try {
                    factory.setValidating(false);

                    SAXParser saxParser = factory.newSAXParser();
                    URL resURL = bundle.getResource("simple/simple.xml");

                    SAXHandler saxHandler = new SAXHandler();
                    saxParser.parse(resURL.openStream(), saxHandler);
                    assertEquals("content", saxHandler.getContent());

                    messages.append("pass");
                } catch (Exception ex) {
                    messages.append(ex.toString());
                }
                return factory;
            }
        };
        tracker.open();

        assertEquals("pass", messages.toString());
    }

    static class SAXHandler extends DefaultHandler {
        private String content;

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            content = new String(ch, start, length);
        }

        public String getContent() {
            return content;
        }
    }
}