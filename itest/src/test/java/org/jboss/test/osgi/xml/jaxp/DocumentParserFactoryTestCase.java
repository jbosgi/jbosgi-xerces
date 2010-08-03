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
package org.jboss.test.osgi.xml.jaxp;

//$Id$

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URL;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.osgi.testing.OSGiManifestBuilder;
import org.jboss.osgi.xml.DocumentBuilderFactoryImpl;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.test.osgi.xml.XMLParserTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A test that shows how a third party library, that you cannot change, may use 
 * the JAXP DocumentBuilderFactory API to load the DOM parser from jboss-osgi-xerces.
 * 
 * The test bundle embeds
 * META-INF/services/javax.xml.parsers.DocumentBuilderFactory
 * and sets the thread context class loader before it calls
 * DocumentBuilderFactory.newInstance() 
 * 
 * @author thomas.diesler@jboss.com
 * @since 21-Jul-2009
 */
@RunWith(Arquillian.class)
public class DocumentParserFactoryTestCase extends XMLParserTestCase
{
   static String serviceId = "META-INF/services/" + DocumentBuilderFactory.class.getName();
   
   @Inject
   public BundleContext context;
   @Inject
   public Bundle bundle;
   // [ARQ-234] Define calls to @BeforeClass, @Before, @AfterClass, @After
   static Bundle xercesBundle;
   
   @Deployment
   public static JavaArchive createdeployment()
   {
      final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "dom-factory.jar");
      archive.addClasses(DocumentParserFactoryTestCase.class, XMLParserTestCase.class);
      archive.addResource("simple/simple.xml");
      archive.setManifest(new Asset()
      {
         public InputStream openStream()
         {
            OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
            builder.addBundleSymbolicName(archive.getName());
            builder.addBundleManifestVersion(2);
            builder.addExportPackages(DocumentParserFactoryTestCase.class);
            builder.addImportPackages("org.jboss.arquillian.junit");
            builder.addImportPackages("org.jboss.shrinkwrap.api", "org.jboss.shrinkwrap.api.asset", "org.jboss.shrinkwrap.api.spec");
            builder.addImportPackages("javax.inject", "org.junit", "org.junit.runner");
            builder.addImportPackages("org.osgi.framework", "org.w3c.dom");
            builder.addImportPackages("org.jboss.osgi.xml", "javax.xml.parsers");
            return builder.openStream();
         }
      });
      
      // This adds the META-INF/services/javax.xml.parsers.DocumentBuilderFactory 
      // resource to the test bundle
      archive.addResource("jaxp/" + serviceId, serviceId);
      return archive;
   }
   
   @Test
   public void testDOMParser() throws Exception
   {
      if (xercesBundle == null)
      {
         xercesBundle = installXercesBundle(context);
         xercesBundle.start();
      }
      
      URL resourceURL = bundle.getResource(serviceId);
      assertNotNull("Resource URL not null", resourceURL);
      
      DocumentBuilderFactory factory;
      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         // Set the TCL to the CL of the test bundle 
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
         
         // The Document builder factory should see
         // META-INF/services/javax.xml.parsers.DocumentBuilderFactory
         // that is embedded in this test bundle
         factory = DocumentBuilderFactory.newInstance();
         assertNotNull("DocumentBuilderFactory not null", factory);
         assertEquals(DocumentBuilderFactoryImpl.class.getName(), factory.getClass().getName());
         
         factory.setNamespaceAware(true);
         factory.setValidating(false);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(ctxLoader);
      }
      
      DocumentBuilder domBuilder = factory.newDocumentBuilder();
      URL resURL = context.getBundle().getResource("simple/simple.xml");
      Document dom = domBuilder.parse(resURL.openStream());
      assertNotNull("Document not null", dom);
      
      Element root = dom.getDocumentElement();
      assertEquals("root", root.getLocalName());
      
      Node child = root.getFirstChild();
      assertEquals("child", child.getLocalName());
      assertEquals("content", child.getTextContent());
   }
}