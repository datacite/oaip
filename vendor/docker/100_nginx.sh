#!/bin/sh
cp /home/app/vendor/docker/webapp.conf:/etc/nginx/sites-enabled/webapp.conf
service nginx start
