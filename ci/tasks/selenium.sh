#!/bin/sh

export GEOMETRY="$SCREEN_WIDTH""x""$SCREEN_HEIGHT""x""$SCREEN_DEPTH"
xvfb-run --server-args="-screen 0 $GEOMETRY -ac +extension RANDR" java -jar /opt/selenium/selenium-server-standalone.jar &
sudo chmown -R seluser ../
repo/ci/tasks/unit.sh