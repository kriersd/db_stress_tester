FROM websphere-liberty:webProfile7

ADD target/db-stress-tester-1.0.0.war /config/dropins/db-stress-tester.war

EXPOSE 9080

ENV LICENSE accept