@echo off


@REM Lancer le rmiregistry dans le dossier src du projet en parralèle du serveur sur le port 1099
start rmiregistry -J-Djava.rmi.server.codebase=file:src/ -J-Djava.rmi.server.hostname=localhost 1099 &

@REM Wait for the registry to start
ping -n 5 localhost > nul

@REM Lancer le serveur dans le dossier src du projet, src/MainServeur.java en précisant le projet comme classpath
start java -classpath src/ MainServeur &