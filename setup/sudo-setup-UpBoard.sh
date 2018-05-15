#!/bin/bash
####
# Copyright (c) 2018 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

echo "Cascadia system setup for UpBoard(4GB) ..."

echo "configuring Java repo ..."
add-apt-repository -y ppa:webupd8team/java
echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections

echo "configuring Neo4j repo ..."
wget -O - https://debian.neo4j.org/neotechnology.gpg.key | apt-key add -
echo 'deb http://debian.neo4j.org/repo stable/' > /etc/apt/sources.list.d/neo4j.list

echo "apt-get update, upgrade, install ..."
apt-get update
apt-get dist-upgrade -y
apt-get install -y oracle-java8-installer python-pip neo4j=3.1.4

echo "Neo4j post-install config ..."
apt-mark hold neo4j
rm -f /var/lib/neo4j/data/dbms/auth
sudo -u neo4j neo4j-admin set-initial-password cascade

echo "BlinkStick setup ..."
pip install blinkstick
blinkstick --add-udev-rule

echo "UpBoard kernel setup ..."
add-apt-repository -y ppa:ubilinux/up
apt update
apt-get autoremove -y --purge 'linux-.*generic'
apt-get install -y linux-image-generic-hwe-16.04-upboard

echo "Setup done. Reboot now!"
