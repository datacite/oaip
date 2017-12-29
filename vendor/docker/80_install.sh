#!/bin/sh
mvn clean install
cp /home/app/target/*.war /var/lib/tomcat7/webapps/
