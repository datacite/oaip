# Overview

This is the OAI-PMH Data Provider (OAIP) application for the DataCite central infrastructure. This
app is a servlet with the main functionality of disseminating the contents of the 
DataCite Metadata Store using OAI-PMH.

To learn more about DataCite please visit [our website](http://www.datacite.org)

To use this service please go to [http://oai.datacite.org](http://oai.datacite.org)

# Installation (for development only)

## Tools

You will need Maven 2.2.1 and JDK 6 in your system (OpenJDK from Ubuntu
works fine).

### Solr setup

This application is configured to connect to a Solr instance for all searching and record retrieval. 

## Java dependencies

All dependencies are managed by Maven public repositories.

## Configure the source code 

I assume you had created a fork from the master DataCite
OAIP project. Now you need to configure the code before compiling. 

The git repository has a bunch of *.template files. You can find them
with:

    find . -name *.template

Those files are templates for the various configuration files which
are machine specific i.e. logging, database credentials etc.

To customise them you need to make a copy omitting (.template from
file name) e.g.:

    cp src/main/resources/log4j.xml.template \
     src/main/resources/log4j.xml

Now in the created file you need to adjust values according to your
local environment.

### src/main/resources/database.properties

your database(Solr) configuration.

### src/main/resources/log4j.properties

your usual log4j stuff.

## Running locally 

### First run

At this stage you should be able to run the application.

    mvn compile tomcat:run

Point your browser at http://localhost:8080/oaip/

You should see the welcome page of the DataCite OAI-PMH Data Provider.

### That's all!

Now you can run test OAI-PMH requests against the application.
