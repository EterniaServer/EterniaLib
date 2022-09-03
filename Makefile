#!/bin/bash
.PHONY: default
.SILENT:


default:


build-compose:
	docker-compose build --force-rm --no-cache --pull

stop-compose:
	docker-compose stop

test:
	docker-compose down -v
	docker-compose run --rm el-java test -i --no-daemon

build-jar:
	./gradlew clean
	./gradlew build -x test
	./gradlew cleanJar
