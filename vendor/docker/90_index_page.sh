#!/bin/sh
cd vendor/middleman
bundle exec middleman build -e ${RAILS_ENV}

service nginx start
