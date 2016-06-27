FROM aipocom/tomcat:latest

RUN mkdir -p /opt/tomcat/datasource

COPY server.xml /opt/tomcat/conf/server.xml
COPY dbcp-org001.properties /opt/tomcat/datasource
