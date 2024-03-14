FROM tomcat:9.0.86
EXPOSE 8080:8080
COPY ./build/libs/tomcat-war-ssl.war/ /usr/local/tomcat/webapps
WORKDIR /usr/local/tomcat
CMD ["catalina.sh", "run"]
