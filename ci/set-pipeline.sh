#!/bin/sh
echo y | fly -t do sp -p blog-blog-kotlin-ui -c pipeline.yml -l ../../credentials.yml
