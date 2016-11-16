#!/bin/bash

if [ ! -d /var/lib/mysql/mysql ]; then
    chown mysql:mysql /var/lib/mysql
    mysql_install_db
fi

trap "mysqladmin shutdown" TERM
mysqld_safe --bind-address=0.0.0.0 &
wait
