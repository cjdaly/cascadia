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

echo "Cascadia system setup for Raspberry Pi 3B, 3B+ ..."

echo "configuring Neo4j repo ..."
wget -O - https://debian.neo4j.org/neotechnology.gpg.key | apt-key add -
echo 'deb http://debian.neo4j.org/repo stable/' > /etc/apt/sources.list.d/neo4j.list

echo "apt-get update, upgrade, install ..."
apt-get update
apt-get upgrade -y
apt-get install -y python-pip python3-pip libatlas-base-dev neo4j=3.1.4

echo "Neo4j post-install config ..."
apt-mark hold neo4j
rm -f /var/lib/neo4j/data/dbms/auth
sudo -u neo4j neo4j-admin set-initial-password cascade

echo "BlinkStick setup ..."
pip install setuptools
pip install blinkstick
blinkstick --add-udev-rule

echo "Pip3 installations..."
pip3 install praw tensorflow

echo "Setup done. Reboot now!"
