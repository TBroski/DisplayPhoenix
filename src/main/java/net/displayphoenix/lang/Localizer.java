package net.displayphoenix.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.Application;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TBroski
 */
public class Localizer {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Map<Local, Map<String, String>> TRANSLATED_VALUES_LOCAL = new HashMap<>();

    public static String translate(String key) {
        return TRANSLATED_VALUES_LOCAL.get(Application.getSelectedLocal()).getOrDefault(key, key);
    }

    public static void create() {
        loadValues();
        System.out.println("Loaded lang values");
    }

    private static void loadValues() {
        try {
            new File("lang").mkdir();
            for (Local local : Local.values()) {
                File file = new File("lang/" + local.getTag() + ".json");

                if (!file.createNewFile()) {
                    FileReader reader = new FileReader(file);

                    TRANSLATED_VALUES_LOCAL.put(local, gson.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType()));
                    if (TRANSLATED_VALUES_LOCAL.get(local) == null)
                        TRANSLATED_VALUES_LOCAL.put(local, new HashMap<>());

                    reader.close();
                }
                else {
                    FileWriter writer = new FileWriter(file);
                    Map<String, String> example = new HashMap<>();
                    example.put("key.example.test", "Example");
                    TRANSLATED_VALUES_LOCAL.put(local, example);

                    String json = gson.toJson(TRANSLATED_VALUES_LOCAL.get(local));

                    writer.write(json);

                    writer.flush();
                    writer.close();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}