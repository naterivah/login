#!/usr/bin/env bash

cd ../
sudo docker build -t login/login .
sudo docker run -p 80:8070 -d login/login