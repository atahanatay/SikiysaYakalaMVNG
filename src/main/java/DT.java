import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class DT {
    static InputStream f = DT.class.getResourceAsStream("data.txt");

    static HashMap<String, String> texts;
    static HashMap<String, String> globals;
    static HashMap<String, Boolean> configs;
    static HashMap<String, Integer> values;

    static HashMap<String, String> overridedTexts;
    static HashMap<String, String> overridedGlobals;
    static HashMap<String, Boolean> overridedConfigs;
    static HashMap<String, Integer> overridedValues;

    static String langKeyO;

    static ArrayList<InputStream> overridePacks = new ArrayList<>();

    static void searchForOverrides() {
        File[] files = new File("./").listFiles();
        if (files != null)
            overridePacks.addAll(Arrays.stream(files).filter(file -> file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("skt")).map(file -> {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList()));

        overridePacks.add(DT.class.getResourceAsStream("defaultoverrides.skt"));
    }

    static void installOverrides(String[] overrides) throws FileNotFoundException {
        overridedTexts = new HashMap<>();
        overridedGlobals = new HashMap<>();
        overridedConfigs = new HashMap<>();
        overridedValues = new HashMap<>();

        for (InputStream f : overridePacks) {
            for (String overrideKey : overrides) {
                Scanner s = new Scanner(f);
                boolean inOverride = false;
                boolean inLang = false;

                while (s.hasNextLine()) {
                    String line = s.nextLine().trim();

                    if (line.isEmpty()) continue;

                    if (line.equals(".ENDLANG")) {
                        inLang = false;
                        continue;
                    }

                    if (line.equals(".ENDOVERRIDE")) {
                        inOverride = false;
                        continue;
                    }

                    String[] lineFinal = line.split(" ", 2);

                    if (inOverride) {
                        if (inLang) overridedTexts.put(lineFinal[0], lineFinal[1]);

                        if (lineFinal[0].equals(".GLOBAL")) {
                            String[] globalFinal = lineFinal[1].split(" ", 2);
                            overridedGlobals.put(globalFinal[0], globalFinal[1]);
                        }

                        if (lineFinal[0].equals(".CONFIG")) {
                            String[] globalFinal = lineFinal[1].split(" ", 2);
                            overridedConfigs.put(globalFinal[0], globalFinal[1].equals("true"));
                        }

                        if (lineFinal[0].equals(".VALUE")) {
                            String[] globalFinal = lineFinal[1].split(" ", 2);
                            overridedValues.put(globalFinal[0], Integer.parseInt(globalFinal[1]));
                        }

                        if (lineFinal[0].equals(".ALL")) {
                            String[] globalFinal = lineFinal[1].split(" ", 2);
                            overridedTexts.put(globalFinal[0], globalFinal[1]);
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
        configs = new HashMap<>();
        values = new HashMap<>();

        Scanner s = new Scanner(f);
        boolean inLang = false;

        while (s.hasNextLine()) {
            String line = s.nextLine().trim();

            if (line.isEmpty()) continue;

            if (line.equals(".ENDLANG")) {
                inLang = false;
                continue;
            }

            String[] lineFinal = line.split(" ", 2);

            if (inLang) texts.put(lineFinal[0], lineFinal[1]);

            if (lineFinal[0].equals(".GLOBAL")) {
                String[] globalFinal = lineFinal[1].split(" ", 2);
                globals.put(globalFinal[0], globalFinal[1]);
            }

            if (lineFinal[0].equals(".CONFIG")) {
                String[] globalFinal = lineFinal[1].split(" ", 2);
                configs.put(globalFinal[0], globalFinal[1].equals("true"));
            }

            if (lineFinal[0].equals(".VALUE")) {
                String[] globalFinal = lineFinal[1].split(" ", 2);
                values.put(globalFinal[0], Integer.parseInt(globalFinal[1]));
            }

            if (lineFinal[0].equals(".ALL")) {
                String[] globalFinal = lineFinal[1].split(" ", 2);
                texts.put(globalFinal[0], globalFinal[1]);
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

    static String getGlobal(String key) {
        if (overridedGlobals != null && overridedGlobals.containsKey(key)) return overridedGlobals.get(key);
        else return globals.getOrDefault(key, null);
    }

    static int getValue(String key) {
        if (overridedValues != null && overridedValues.containsKey(key)) return overridedValues.get(key);
        else return values.getOrDefault(key, null);
    }

    static boolean getConfig(String key) {
        if (overridedConfigs != null && overridedConfigs.containsKey(key)) return overridedConfigs.get(key);
        else return configs.getOrDefault(key, null);
    }
}
