FROM yaronr/openjdk-7-jre

ADD target/boop-0.1.0-standalone.jar standalone.jar
VOLUME /opt/boop/config
CMD ["java", "-jar", "standalone.jar", "/opt/boop/config/production.edn"]

EXPOSE 3000

