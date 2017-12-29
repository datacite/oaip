#!/bin/sh
cp /home/app/docker/webapp.conf:/etc/nginx/sites-enabled/webapp.conf
service nginx start
