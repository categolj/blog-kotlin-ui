#!/bin/sh
echo y | fly -t apj sp -p blog-blog-kotlin-ui -c pipeline.yml -l ../../credentials.yml
