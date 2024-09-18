package org.start;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/**
 * Класс предоставляет интерфейс доступа к входящим настройкам животных
 */
public class StartParamethers {

    public static Set<String> getAnimalsNames() {
        if(animalsNames == null || animalsNames.isEmpty())
        {
            getEatingRelations();
            animalsNames = eatingProbabilities.keySet();
        }
        return animalsNames;
    }

    public static int getMaxPlantAmountInCell() {
        return getOwnValues()
                .get("Рослини")
                .get("Максимальна кількість на клітинці")
                .intValue();
    }

    public static Map<String, Map<String, Integer>> getEatingRelations() {
        if (eatingProbabilities == null) {
            // Используем ClassLoader для загрузки ресурса из папки resources
            try (InputStream inputStream = StartParamethers.class
                    .getClassLoader().getResourceAsStream("animal_probabilities.json")) {

                try (Reader reader = new java.io.InputStreamReader(inputStream)) {

                    // Определяем тип данных
                    Type mapType = new TypeToken<Map<String, Map<String, Integer>>>(){}.getType();
                    eatingProbabilities = gson.fromJson(reader, mapType);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return eatingProbabilities;
    }

    public static Map<String, Integer> getAnimalEatingRelations(String animalName) {
        // Загружаем данные, если они еще не были загружены
        getEatingRelations();

        // Ищем данные по названию животного
        if (eatingProbabilities != null && eatingProbabilities.containsKey(animalName)) {
            return eatingProbabilities.get(animalName); // Возвращаем данные, если ключ существует
        }

        // Возвращаем null, если данных для этого животного нет
        return null;
    }

    public static Map<String, Map<String, Float>> getOwnValues() {
        if (ownValues == null) {
            // Используем ClassLoader для загрузки ресурса из папки resources
            try (InputStream inputStream = StartParamethers.class
                    .getClassLoader().getResourceAsStream("animal_characteristics.json");
                 Reader reader = new java.io.InputStreamReader(inputStream)) {

                // Определяем тип данных

                Type mapType = new TypeToken<Map<String, Map<String, Float>>>(){}.getType();
                ownValues = gson.fromJson(reader, mapType);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ownValues;
    }

    public static int getMaxPossibleAnimalsAmountPerLocation(String name) {
        return getOwnValues()
                .get(name)
                .get("Максимальна кількість на клітинці")
                .intValue();
    }


    private static Map<String, Map<String, Integer>> eatingProbabilities;
    private static Map<String, Map<String, Float>> ownValues;
    private static Set<String> animalsNames;
    private static final Gson gson =
            new GsonBuilder()
                    .registerTypeAdapter(
                            Float.class,
                            new JsonDeserializer<Float>() {
                                @Override
                                public Float deserialize(
                                        JsonElement json,
                                        Type typeOfT,
                                        JsonDeserializationContext context)
                                        throws JsonParseException {

                                    if (json.isJsonPrimitive()) {
                                        JsonPrimitive jsonPrimitive =
                                                json.getAsJsonPrimitive();
                                        if (jsonPrimitive.isNumber()) {
                                            return jsonPrimitive.getAsFloat();
                                        }
                                    }
                                    return 0f; // Или какое-либо значение по умолчанию
                                }
                            }).create();
}
