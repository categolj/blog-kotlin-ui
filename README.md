Start [eureka-server](../eureka-server) and [blog-api](../blog-api) in advance, then

    $ mvn clean package -Dmaven.test.skip=true
    $ cf login -a api.<your CF target>
    $ cf create-user-provided-service blog-ui-log -l syslog://<your log manager>
    $ cf push --no-start
    $ cf set-env blog-ui CF_TARGET https://api.<your CF target>
    $ cf start blog-ui