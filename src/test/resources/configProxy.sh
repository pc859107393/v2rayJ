#!/usr/bin/bash

pac_url=$1
httpPort=$2
sockPort=$3

cd /Users/cheng/.v2rayJ/bin && chmod 777 * && ./V2rayUTool -mode pac -pac "$pac_url" -http-port "$httpPort" -sock-port "$sockPort"
