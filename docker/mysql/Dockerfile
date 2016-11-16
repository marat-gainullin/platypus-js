FROM ubuntu:14.04
MAINTAINER jskonst
RUN DEBIAN_FRONTEND=noninteractive apt-get update && apt-get install mysql-client mysql-server -y
COPY start.sh /
CMD [ "/start.sh" ]