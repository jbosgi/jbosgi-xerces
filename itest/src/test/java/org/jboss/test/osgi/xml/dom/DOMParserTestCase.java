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
package org.jboss.test.osgi.xml.dom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.osgi.spi.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A test that uses a DOM parser to read an XML document.
 *
 * @author thomas.diesler@jboss.com
 * @see http://www.osgi.org/javadoc/r4v41/org/osgi/util/xml/XMLParserActivator.html
 * @since 21-Jul-2009
 */
@RunWith(Arquillian.class)
public class DOMParserTestCase {
    @Inject
    public static BundleContext context;
    @Inject
    public Bundle bundle;

    @Deployment
    public static JavaArchive createdeployment() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "dom-parser.jar");
        archive.addClasses(DOMParserTestCase.class);
        archive.addAsResource("simple/simple.xml");
        archive.setManifest(new Asset() {
            public InputStream openStream() {
                OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
                builder.addBundleSymbolicName(archive.getName());
                builder.addBundleManifestVersion(2);
                builder.addImportPackages(DocumentBuilder.class, Document.class);
                builder.addImportPackages(ServiceTracker.class);
                return builder.openStream();
            }
        });
        return archive;
    }

    @Test
    public void testDOMParser() throws Exception {
        ServiceReference[] srefs = context.getServiceReferences(DocumentBuilderFactory.class.getName(), "(provider=jboss)");
        if (srefs == null)
            throw new IllegalStateException("DocumentBuilderFactory not available");

        DocumentBuilderFactory factory = (DocumentBuilderFactory) context.getService(srefs[0]);
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        DocumentBuilder domBuilder = factory.newDocumentBuilder();
        URL resURL = bundle.getResource("simple/simple.xml");
        Document dom = domBuilder.parse(resURL.openStream());
        assertNotNull("Document not null", dom);

        Element root = dom.getDocumentElement();
        assertEquals("root", root.getLocalName());

        Node child = root.getFirstChild();
        assertEquals("child", child.getLocalName());
        assertEquals("content", child.getTextContent());
    }

    @Test
    public void testDOMParserTracker() throws Exception {
        final StringBuffer messages = new StringBuffer();
        ServiceTracker tracker = new ServiceTracker(context, DocumentBuilderFactory.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                DocumentBuilderFactory factory = (DocumentBuilderFactory) super.addingService(reference);
                try {
                    factory.setValidating(false);

                    DocumentBuilder domBuilder = factory.newDocumentBuilder();
                    URL resURL = bundle.getResource("simple/simple.xml");
                    Document dom = domBuilder.parse(resURL.openStream());
                    assertNotNull("Document not null", dom);

                    Element root = dom.getDocumentElement();
                    assertEquals("root", root.getLocalName());

                    Node child = root.getFirstChild();
                    assertEquals("child", child.getLocalName());
                    assertEquals("content", child.getTextContent());

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
}