#!/bin/sh
for file in *.sql; do
  mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < < $file
done
