#!/usr/bin/env bash
mvn install -DperformRelease=true -DcreateChecksum=true -Dgpg.passphrase=password