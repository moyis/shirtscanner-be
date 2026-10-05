ARG BASE_IMAGE
FROM ${BASE_IMAGE}

RUN groupadd --system --gid 1001 app \
 && useradd --system --uid 1001 --gid app --create-home --home-dir /home/app --shell /usr/sbin/nologin app

COPY --chown=app:app --chmod=0755 curl-impersonate /usr/local/bin/curl-impersonate

USER 1001:1001
