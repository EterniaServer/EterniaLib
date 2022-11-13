#!/bin/bash
.PHONY: default
.SILENT:


default:


build-compose:
	docker-compose build --force-rm --no-cache --pull

start:
	docker-compose up -d

stop:
	docker-compose stop

test:
	docker-compose exec el-java gradle test -i --no-daemon

build-jar:
	docker-compose exec el-java gradle clean
	docker-compose exec el-java gradle build -x test
	docker-compose exec el-java gradle cleanJar

copy-jar:
	docker-compose cp el-java:/usr/src/build/libs ./output
