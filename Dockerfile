FROM openliberty/open-liberty:kernel-java8-openj9-ubi

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

LABEL \
  org.opencontainers.image.authors="Dillon Carns & Mary Crivelli & Dennis Mila" \
  org.opencontainers.image.vendor="Open Liberty" \
  org.opencontainers.image.url="local" \
  org.opencontainers.image.source="https://github.com/carnsds-ibm/backend-openliberty-demo" \
  org.opencontainers.image.version="$VERSION" \
  org.opencontainers.image.revision="$REVISION" \
  vendor="Open Liberty" \
  name="article-backend" \
  version="$VERSION-$REVISION" \
  summary="The calculator microservice" \
  description="This image contains the article microservice running with the Open Liberty runtime."

COPY --chown=1001:0 \
    src/main/liberty/config \
    /config/

COPY --chown=1001:0 \
    target/voidbackend.war \
    /config/apps

RUN configure.sh