FROM gradle:7.5.1-jdk17-alpine

LABEL maintainer="yurinogueira@id.uff.br"
LABEL vendor="Nogueira Tecnologia"

WORKDIR /usr/src/

COPY ./src /usr/src/src

RUN mkdir build

COPY ./settings.gradle /usr/src/settings.gradle
COPY ./build.gradle /usr/src/build.gradle

ENTRYPOINT ["gradle"]
