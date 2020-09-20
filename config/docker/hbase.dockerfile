FROM openjdk:14-alpine

ARG HBASE_VERSION="2.2.5"

RUN apk add --no-cache bash wget

RUN mkdir -p /opt
RUN wget -q -O /opt/hbase.tar.gz https://apache.mirror.colo-serv.net/hbase/stable/hbase-${HBASE_VERSION}-bin.tar.gz
RUN cd /opt && tar xzvf hbase.tar.gz

WORKDIR /opt/hbase-${HBASE_VERSION}

EXPOSE 16010

CMD ["./bin/hbase-daemon.sh", "--config", "./conf", "foreground_start", "master"]
