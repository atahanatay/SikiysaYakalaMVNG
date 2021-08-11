import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class DT {
    static InputStream f = DT.class.getResourceAsStream("data.txt");

    static HashMap<String, String> texts;
    static HashMap<String, String> globals;

    static HashMap<String, String> overridedTexts;
    static HashMap<String, String> overridedGlobals;

    static String langKeyO;

    static List<File> overridePacks;

    static boolean searchForOverrides() {
        File[] files = new File("./").listFiles();
        if (files != null)
            overridePacks = Arrays.stream(files).filter(file -> file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("skt")).collect(Collectors.toList());

        return overridePacks.size() != 0;
    }

    static void installOverrides(String[] overrides) throws FileNotFoundException {
        overridedTexts = new HashMap<>();
        overridedGlobals = new HashMap<>();

        for (File f : overridePacks) {
            for (String overrideKey : overrides) {
                Scanner s = new Scanner(f);
                boolean inOverride = false;
                boolean inLang = false;

                while (s.hasNextLine()) {
                    String line = s.nextLine().trim();

                    if (line.isEmpty()) continue;

                    if (line.equals("*")) {
                        if (inLang) inLang = false;
                        else if (inOverride) inOverride = false;
                        continue;
                    }

                    String[] lineFinal = line.split(" ", 2);

                    if (inOverride) {
                        if (inLang) overridedTexts.put(lineFinal[0], lineFinal[1]);

                        if (lineFinal[0].equals(".GLOBAL")) {
                            String[] globalFinal = lineFinal[1].split(" ", 1);
                            overridedGlobals.put(globalFinal[0], globalFinal[1]);
                        }

                        if (lineFinal[0].equals(".LANG") && lineFinal[1].equals(langKeyO)) inLang = true;
                    } else if (lineFinal[0].equals(".OVERRIDE") && lineFinal[1].equals(overrideKey)) inOverride = true;
                }

                s.close();
            }
        }
    }

    static void analyzeAll(String langKey) {
        langKeyO = langKey.toUpperCase(Locale.ENGLISH);

        texts = new HashMap<>();
        globals = new HashMap<>();

        Scanner s = new Scanner(f);
        boolean inLang = false;

        while (s.hasNextLine()) {
            String line = s.nextLine().trim();

            if (line.isEmpty()) continue;

            if (line.equals("*")) {
                inLang = false;
                continue;
            }

            String[] lineFinal = line.split(" ", 2);

            if (inLang) texts.put(lineFinal[0], lineFinal[1]);

            if (lineFinal[0].equals(".GLOBAL")) {
                String[] globalFinal = lineFinal[1].split(" ", 1);
                globals.put(globalFinal[0], globalFinal[1]);
            }

            if (lineFinal[0].equals(".LANG") && lineFinal[1].equals(langKeyO)) inLang = true;
        }

        System.out.printf("%d lines analyzed\n", texts.size());

        s.close();
    }

    static String getText(String key) {
        if (overridedTexts != null && overridedTexts.containsKey(key)) return overridedTexts.get(key);
        else return texts.getOrDefault(key, langKeyO + key);
    }
}
