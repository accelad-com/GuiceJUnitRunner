Guice-JUnit
===========

Guice-JUnit is based on the demonstration project of https://github.com/sfragis/GuiceJUnitRunner.  It enables injection of Guice modules in unit tests. 

As an added extra this library also includes support for Mybatis+Guice transactions, inspired by http://mybatis-user.963551.n3.nabble.com/How-to-rollback-all-changes-in-a-JUnit-test-td3181475.html

To use this library, include it in your project: 
```
<dependency>
	<groupId>nl.pvanassen</groupId>
	<artifactId>guice-junit-runner</artifactId>
	<version>1.0.0</version>
	<scope>test</scope>
</dependency>
```

To set up a test with the junit runner, use the RunWith annotation to point to GuiceJUnitRunner. Also supply the required modules to load
```
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ ComponentsTestModule.class, ServicesTestModule.class })
```
For an example see https://github.com/pvanassen/GuiceJUnitRunner/blob/master/src/test/java/nl/pvanassen/guicejunitrunner/JavaxInjectPropertyTest.java

To enable MyBatis transactions, instead use: 
```
@RunWith(GuiceMyBatisRollbackJUnitRunner.class)
@GuiceModules({ ComponentsTestModule.class, ServicesTestModule.class })
```

Make sure Mybatis is initialized in one of your modules. 