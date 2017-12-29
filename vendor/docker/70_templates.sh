#!/bin/sh
dockerize -template /home/app/vendor/docker/database.properties.tmpl:/home/app/src/main/resources/database.properties
dockerize -template /home/app/vendor/docker/log4j.properties.tmpl:/home/app/src/main/resources/log4j.properties
