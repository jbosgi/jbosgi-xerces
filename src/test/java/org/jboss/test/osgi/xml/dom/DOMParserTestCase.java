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

//$Id$

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URL;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.osgi.testing.OSGiManifestBuilder;
import org.jboss.osgi.xml.XMLParserCapability;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A test that uses a DOM parser to read an XML document.
 * 
 * @see http://www.osgi.org/javadoc/r4v41/org/osgi/util/xml/XMLParserActivator.html
 * 
 * @author thomas.diesler@jboss.com
 * @since 21-Jul-2009
 */
@RunWith(Arquillian.class)
public class DOMParserTestCase
{
   @Inject
   public BundleContext context;
   
   @Deployment
   public static JavaArchive createdeployment()
   {
      final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "dom-parser.jar");
      archive.addClasses(DOMParserTestCase.class);
      archive.addResource("simple/simple.xml");
      archive.setManifest(new Asset()
      {
         public InputStream openStream()
         {
            OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
            builder.addBundleSymbolicName(archive.getName());
            builder.addBundleManifestVersion(2);
            // [TODO] generate a separate bundle the contains the test case
            builder.addExportPackages(DOMParserTestCase.class);
            builder.addImportPackages("org.jboss.arquillian.junit");
            builder.addImportPackages("org.jboss.shrinkwrap.api", "org.jboss.shrinkwrap.api.asset", "org.jboss.shrinkwrap.api.spec");
            builder.addImportPackages("javax.inject", "org.junit", "org.junit.runner");
            builder.addImportPackages("org.osgi.framework", "javax.xml.parsers", "org.w3c.dom");
            return builder.openStream();
         }
      });
      return archive;
   }
   
   @Test
   public void testDOMParser() throws Exception
   {
      DocumentBuilder domBuilder = getDocumentBuilder();
      URL resURL = context.getBundle().getResource("simple/simple.xml");
      Document dom = domBuilder.parse(resURL.openStream());
      assertNotNull("Document not null", dom);
      
      Element root = dom.getDocumentElement();
      assertEquals("root", root.getLocalName());
      
      Node child = root.getFirstChild();
      assertEquals("child", child.getLocalName());
      assertEquals("content", child.getTextContent());
   }

   private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException, InvalidSyntaxException
   {
      // This service gets registerd by the jboss-osgi-apache-xerces service
      String filter = "(" + XMLParserCapability.PARSER_PROVIDER + "=" + XMLParserCapability.PROVIDER_JBOSS_OSGI + ")";
      ServiceReference[] srefs = context.getServiceReferences(DocumentBuilderFactory.class.getName(), filter);
      if (srefs == null)
         throw new IllegalStateException("DocumentBuilderFactory not available");
      
      DocumentBuilderFactory factory = (DocumentBuilderFactory)context.getService(srefs[0]);
      factory.setValidating(false);
      
      DocumentBuilder domBuilder = factory.newDocumentBuilder();
      return domBuilder;
   }
}