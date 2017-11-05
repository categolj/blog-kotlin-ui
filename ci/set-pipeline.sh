#!/bin/sh
fly -t home sp -p blog-blog-kotlin-ui \
    -c `dirname $0`/pipeline.yml \
    -l `dirname $0`/../../credentials.yml