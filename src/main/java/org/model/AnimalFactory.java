package org.model;

import org.model.Annotation.AnimalType;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.model.animalType.Animal;
import org.reflections.Reflections;

public class AnimalFactory {

    static int id = 0;
    static Reflections reflections = new Reflections("org.model.animalType");
    static Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AnimalType.class);

    public static Animal createAnimalByName(String animalName) throws Exception {

        for (Class<?> clazz : annotatedClasses) {
            // Получаем аннотацию
            AnimalType annotation = clazz.getAnnotation(AnimalType.class);
            if (annotation != null && annotation.name().equals(animalName)) {
                // Создаём объект класса
                Constructor<?> constructor = clazz.getDeclaredConstructor();

                Animal newAnimal = (Animal) constructor.newInstance();
                newAnimal.id = id++;

                return  newAnimal;
            }
        }

        throw new Exception("Сущность вида " + animalName + " не создано.");
    }

    /**
     * Возвращает уникальные пары "название животного" - "название класса"
     * на основе аннотации @AnimalType
     *
     * @return Map, где ключ - название животного, значение - название класса
     */
    public static Map<String, String> getAnimalClassNameByAnimalName() {
        if(animalClassesByNames == null || animalClassesByNames.isEmpty()) {
            animalClassesByNames = new HashMap<>();
            for (Class<?> clazz : annotatedClasses) {
                AnimalType annotation = clazz.getAnnotation(AnimalType.class);
                if (annotation != null) {
                    animalClassesByNames.put(annotation.name(), clazz.getSimpleName());
                }
            }
        }
        return animalClassesByNames;
    }

    private static Map<String, String> animalClassesByNames;

    /**
     * Получает название класса по имени животного.
     *
     * @param animalName название животного
     * @return название класса или null, если имя животного не найдено
     */
    public static String getClassNameByAnimalName(String animalName) {
        Map<String, String> animalClassMap = getAnimalClassNameByAnimalName();
        return animalClassMap.get(animalName);
    }
}
