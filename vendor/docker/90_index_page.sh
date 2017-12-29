#!/bin/sh
cd vendor/middleman
bundle exec middleman build -e ${RAILS_ENV}

cp /home/app/vendor/docker/webapp.conf /etc/nginx/sites-enabled/webapp.conf
service nginx start
