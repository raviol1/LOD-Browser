-Geh sicher dass postgres installiert ist. Auf Linux server apt updaten und upgraden, dann postgres installieren.

-Yago core Daten von http://resources.mpi-inf.mpg.de/yago-naga/yago3.1/yagoFacts.tsv.7z herunterladen und im LODBrowser/yago folder auspacken.

- YagoSingle.java mit dem yago Verzeichnis als Argument 
starten und so in die Datei 'allfacts.tsv_' transformieren:
(> javac YagoSingle.java)
 > java YagoSingle ../yago/ (wenn sie sich im yago folder befinden)
 Dies konvertiert die Tabelle, entfernt eine Kolonne.

- Wenn weder benutzer noch db besteht in postgres können diese mithilfe dieser kommandos erstellt werden:

  > sudo -u postgres createuser >dein username<
  > sudo -u postgres createdb >dein username< (eine db mit dem username muss bestehen)
  > sudo -u postgres createdb yago
  
- Dann zum öffnen der db;
  > psql -U >dein username<
  und in die Yago-Datenbank wechseln:
  > \c YAGO

- Dort dann die folgenden SQL-Kommandos zum Importieren der Daten 
ausfuehren:

DROP TABLE IF EXISTS AllFacts;CREATE TABLE AllFacts(SUBJ VARCHAR(256), PRED VARCHAR(256), OBJ VARCHAR(256), PRIMARY KEY(SUBJ, PRED, OBJ));\copy AllFacts FROM '>weg zum ordner mit</allfacts.tsv_' ENCODING 'UTF8';

Das sollte etwa folgendermassen aussehen:

$ psql -U postgres
Password for user postgres:
psql (9.6.2)
Type "help" for help.

>your username<=# \c yago
You are now connected to database "yago" as user ">your username<".
yago=# DROP TABLE IF EXISTS AllFacts;
NOTICE:  table "facts" does not exist, skipping
DROP TABLE
yago=# CREATE TABLE AllFacts(SUBJ VARCHAR(256), PRED VARCHAR(256), OBJ 
VARCHAR(256), PRIMARY KEY(SUBJ, PRED, OBJ));
CREATE TABLE
yago=# COPY AllFacts FROM '../allfacts.tsv_' ENCODING 'UTF8';
COPY 12430700
yago=#

