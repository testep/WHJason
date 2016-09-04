@echo off
start rmi.bat
timeout 2
java -classpath .\bin\classes;.\lib\jason.jar;.\lib\sqlite-jdbc-3.8.11.2.jar jason.infra.centralised.RunCentralisedMAS warehouse.mas2j