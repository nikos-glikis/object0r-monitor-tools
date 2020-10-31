#!/usr/bin/env bash
mvn install -DperformRelease=true -DcreateChecksum=true -Dgpg.passphrase=password
#mvn deploy -DperformRelease=true -DcreateChecksum=true -Dgpg.passphrase=password