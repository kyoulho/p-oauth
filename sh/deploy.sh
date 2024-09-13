#!/bin/bash

PRIV=/var/jenkins_home/.ssh/id_rsa
APP_NAME=oauth
SOURCE='build/libs/playce-'$APP_NAME'.war'
TARGET_DIR=/opt/playce/modules/playce-$APP_NAME

echo 'copy config'
scp -i $PRIV sh/setenv.sh sh/kill.sh osc@$SERVER:$TARGET_DIR/bin/
scp -i $PRIV sh/server.xml osc@$SERVER:$TARGET_DIR/conf/
echo 'copy db_patch'
ssh -i $PRIV osc@$SERVER 'mkdir -p '$TARGET_DIR/db_patch
scp -i $PRIV sh/db_patch/*.sql osc@$SERVER:$TARGET_DIR/db_patch/
echo 'copy app'
scp -i $PRIV $SOURCE 'osc@'$SERVER:$TARGET_DIR'/webapps/'$APP_NAME'.war'
echo 'unzip app'
ssh -i $PRIV osc@$SERVER 'cd '$TARGET_DIR'/webapps && rm -rf '$APP_NAME' && unzip -d '$APP_NAME' '$APP_NAME'.war && rm -f '$APP_NAME'.war'
