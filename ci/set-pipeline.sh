#!/bin/sh
echo y | fly -t azr sp -p blog-blog-kotlin-ui -c pipeline.yml -l ../../credentials.yml
