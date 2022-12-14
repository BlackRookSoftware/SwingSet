# Black Rook Swing Set

Copyright (c) 2019-2022 Black Rook Software.  
[https://github.com/BlackRookSoftware/SwingSet](https://github.com/BlackRookSoftware/SwingSet)

### Required Libraries

NONE

### Required Java Modules

[java.desktop](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/module-summary.html)  
* [java.xml](https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/module-summary.html)  
* [java.datatransfer](https://docs.oracle.com/en/java/javase/11/docs/api/java.datatransfer/module-summary.html)  
* [java.base](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/module-summary.html)  


### Introduction

This library contains classes for creating common Swing objects quickly. These classes can be 
redistributed in whole or in part.


### Why?

Let's face facts - GUI programming is the worst. This library tries to reduce most of the tedium, 
but you'll still be doing GUI programming. Sorry.


### Library

Contained in this release is a series of classes that are used for quick Swing object creation. 

The javadocs contain basic outlines of each package's contents.


### Compiling with Ant

To compile this library with Apache Ant, type:

	ant compile

To make Maven-compatible JARs of this library (placed in the *build/jar* directory), type:

	ant jar

To make Javadocs (placed in the *build/docs* directory):

	ant javadoc

To compile main and test code and run tests (if any):

	ant test

To make Zip archives of everything (main src/resources, bin, javadocs, placed in the *build/zip* directory):

	ant zip

To compile, JAR, test, and Zip up everything:

	ant release

To clean up everything:

	ant clean
	
### Other

This program and the accompanying materials are made available under the 
terms of the MIT License which accompanies this distribution.

A copy of the MIT License should have been included in this release (LICENSE.txt).
If it was not, please contact us for a copy, or to notify us of a distribution
that has not included it. 
