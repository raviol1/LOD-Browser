import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class YagoSingle {

	public static void main(String[] args) throws Exception {

		BufferedWriter writer = new BufferedWriter(new FileWriter(args[0] + "/allfacts.tsv_"));

		String[] files = new File(args[0]).list();

		for (int i = 0; i < files.length; i++) {
			if (!files[i].endsWith(".tsv"))
				continue;

			BufferedReader reader = new BufferedReader(new FileReader(args[0] + "/" + files[i]));

			System.out.println(files[i]);
			String line;
			String[] fields;
			int j = 0;

			while ((line = reader.readLine()) != null) {
				if (++j == 1)
					continue;

				// System.out.println(line);
				fields = line.split("\t");
				for(int u =1; u<4; u++) {
				
					fields[u] = fields[u].replaceAll("<|>", "");
					//fields[u] = fields[u].replaceAll(">", "");
					//fields[u] = fields[u].replaceAll("|", "");
					fields[u] = fields[u].replaceAll("'", "`");
					fields[u] = fields[u].replaceAll("_", " ");
					//fields[u] = fields[u].replaceAll("\\", "/");
				}
				

				writer.write(fields[1] + "\t" + fields[2] + "\t"+ fields[3]/*.substring(0, Math.min(fields[3].length() - 1, 255))*/ + "\n");

				// System.out.println(fields[2] + ":\t" + fields[1] + "\t" +
				// fields[3] + "\n");
			}
			reader.close();
		}

		System.out.println(
				"DROP TABLE IF EXISTS AllFacts; CREATE TABLE AllFacts(SUBJ VARCHAR(256), PRED VARCHAR(256), OBJ VARCHAR(256), PRIMARY KEY(SUBJ, PRED, OBJ));");
		System.out.println("COPY AllFacts FROM '" + args[0] + "/allfacts.tsv_' ENCODING 'UTF8';");
		writer.flush();
		writer.close();
	}
}
