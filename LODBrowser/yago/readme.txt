-Geh sicher dass postgres installiert ist. Auf Linux server apt updaten und upgraden, dann postgres installieren.

-Yago core Daten von http://resources.mpi-inf.mpg.de/yago-naga/yago3.1/yagoFacts.tsv.7z herunterladen und nicht auspacken.

- YagoSingle.java mit dem Verzeichnis der CORE-Daten als Argument 
starten und so in die Datei 'allfacts.tsv_' transformieren

- Wenn noch weder benutzer noch db besteht koennen diese mithilfe dieser kommandos erstellt werden:

  > sudo -u postgres createuser >dein username<
  > sudo -u postgres createdb >dein username< (eine db mit dem username muss bestehen)
  > sudo -u postgres createdb yago
  
- Dann zum öffnen der db;
  > psql -U >dein username<
  und in die Yago-Datenbank wechseln:
  > \c YAGO

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
