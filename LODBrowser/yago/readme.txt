-Yago core Daten von http://resources.mpi-inf.mpg.de/yago-naga/yago3.1/yagoFacts.tsv.7z herunterladen

- YagoSingle.java mit dem Verzeichnis der CORE-Daten als Argument 
starten und so in die Datei 'allfacts.tsv_' transformieren

- Eine PostgreSQL-Shell oeffnen: 'psql -U postgres' und in die 
Yago-Datenbank wechseln '\c YAGO'

- Dort dann die folgenden SQL-Kommandos zum Importieren der Daten 
ausfuehren:

DROP TABLE IF EXISTS AllFacts;
CREATE TABLE AllFacts(SUBJ VARCHAR(256), PRED VARCHAR(256), OBJ 
VARCHAR(256), PRIMARY KEY(SUBJ, PRED, OBJ));
COPY AllFacts FROM '/tmp/allfacts.tsv_' ENCODING 'UTF8';

Das sollte etwa folgendermassen aussehen:

$ psql -U postgres
Password for user postgres:
psql (9.6.2)
Type "help" for help.

postgres=# \c yago
You are now connected to database "yago" as user "postgres".
yago=# DROP TABLE IF EXISTS AllFacts;
NOTICE:  table "facts" does not exist, skipping
DROP TABLE
yago=# CREATE TABLE AllFacts(SUBJ VARCHAR(256), PRED VARCHAR(256), OBJ 
VARCHAR(256), PRIMARY KEY(SUBJ, PRED, OBJ));
CREATE TABLE
yago=# COPY AllFacts FROM '/tmp/allfacts.tsv_' ENCODING 'UTF8';
COPY 12430700
yago=#

because duplicates stop the copy process, i removed the primary key
