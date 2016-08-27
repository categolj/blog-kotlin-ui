#!/bin/sh
echo y | fly -t home sp -p blog-blog-kotlin-ui -c pipeline.yml -l ../../credentials.yml
