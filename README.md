XML Parser Strategies
---------------------

There are several ways of using an XML parser in an OSGi environment. Here are some strategies in descending order of preference

#1 DocumentBuilderFactory service
---------------------------------

The prefered way is to obtain a DocumentBuilderFactory service from the OSGi registry.
In your bundle you setup a ServiceTracker and use the DocumentBuilderFactory that is 
given to you by the OSGi runtime. The jboss-osgi-xerces bundle registeres such a service
when it is started.

#2 DocumentBuilderFactory.newInstance()
---------------------------------------

In case you have a third party library that uses the DocumentBuilderFactory API and you 
cannot change that library to use strategy #1 you can wrap that library in a bundle and 
embed the 'META-INF/services/javax.xml.parsers.DocumentBuilderFactory' resource with
the following content in it.

	org.jboss.osgi.xml.DocumentBuilderFactoryImpl

Your wrapper bundle must import the 'org.jboss.osgi.xml' package and you must set the 
thread context classloader before the library calls DocumentBuilderFactory.newInstance().

      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
         factory = DocumentBuilderFactory.newInstance();
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(ctxLoader);
      }

The document builder factory you get should be the one from jboss-osgi-xerces.  
