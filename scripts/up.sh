#!/usr/bin/env bash

docker-compose \
  --env-file ./config/.env.dev \
  -p gleam \
  -f ./gleam.dev.yml \
  up -d
