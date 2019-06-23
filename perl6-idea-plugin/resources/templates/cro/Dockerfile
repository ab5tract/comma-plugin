FROM croservices/$$DOCKER_IMAGE$$
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN zef install --deps-only . && perl6 -c -Ilib service.p6
ENV $$HOST_VARIABLE$$="0.0.0.0" $$PORT_VARIABLE$$="10000"
EXPOSE 10000
CMD perl6 -Ilib service.p6
