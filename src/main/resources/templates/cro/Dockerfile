FROM croservices/$$DOCKER_IMAGE$$
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN zef install --deps-only . && raku -c -Ilib service.p6
ENV $$HOST_VARIABLE$$="0.0.0.0" $$PORT_VARIABLE$$="10000"
EXPOSE 10000
CMD raku -Ilib service.p6
