FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/install/docker/ /app/
WORKDIR /app/bin
CMD ["./docker"]
