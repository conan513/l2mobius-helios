@echo off
SET NAME=SingleCore Database
TITLE %NAME%

cd "%CD%\Database"
bin\mysqld --defaults-file=my-large.ini --console --standalone
exit