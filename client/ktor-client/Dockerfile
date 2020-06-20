# We select the base image from. Locally available or from https://hub.docker.com/
FROM openjdk:8-jre-alpine

# We define the user we will use in this instance to prevent using root that even in a container, can be a security risk.
ENV APPLICATION_USER CodeView

#names should be synced with AppContext enums
ENV PORT 8000
#ssl disabled by default
ENV SSL_PORT unknown
ENV SSL_KEY_STORE_FILE key.jks
ENV KEY_ALIAS mykey
ENV KEY_STORE_PASSWORD password
ENV PRIVATE_KEY_PASSWORD password
ENV logsDir logs
ENV cacheDir cache

RUN apk add --update --no-cache ttf-dejavu ttf-droid ttf-freefont ttf-liberation ttf-ubuntu-font-family graphviz 

# Then we add the user, create the /app folder and give permissions to our user.
RUN adduser -D  $APPLICATION_USER
RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

# Marks this container to use the specified $APPLICATION_USER
USER $APPLICATION_USER

# We copy the FAT Jar we built into the /app folder and sets that folder as the working directory.
COPY ./build/libs/code-view-application.jar /app/code-view-application.jar
WORKDIR /app

# We launch java to execute the jar, with good defauls intended for containers.
CMD ["sh", "-c", "java -server \
    -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap \
    -XX:InitialRAMFraction=2 -XX:MinRAMFraction=2 -XX:MaxRAMFraction=2 \
    -XX:+UseG1GC -XX:MaxGCPauseMillis=100 \
    -XX:+UseStringDeduplication \
    -jar code-view-application.jar"]
