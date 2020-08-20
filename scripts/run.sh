#!/usr/bin/env bash

gradle clean execute \
  -DmainClass=at.hannesmoser.gleam.App \
  -Dexec.args="" \
  -Pdirect-runner
