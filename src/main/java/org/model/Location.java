package org.model;

import org.model.animalType.Animal;
import org.model.animalType.Caterpillar;
import org.model.animalType.Plant;
import org.start.StartParamethers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Location {
    private List<Animal> animals = new ArrayList<>();
    private List<Plant> plants = new ArrayList<>();

    private int x;
    private int y;

    public int getX() { return this.x; }
    public int getY() { return this.y; }

    private String activeMessage;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
        this.activeMessage = "Локация ["+getX()+"]["+getY()+"]: ";
    }

    public synchronized void addAnimal(Animal animal, boolean firstStart) {
        animals.add(animal);
        animal.location = this;

        if(firstStart)
            System.out.println(animal.name + "_" + animal.id + " помещен на локацию (" + this.x + "," + this.y + ")");
        else
            System.out.println(animal.name + "_" + animal.id + " прибыл на локацию (" + this.x + "," + this.y + ")");
    }

    public synchronized void addPlant(Plant plant) {
        plants.add(plant);
    }

    public synchronized void removeAnimal(Animal animal) {

        if(animal.isDead)
            System.out.println(animal.name + "_" + animal.id + " сдох от голода на локации (" + this.x + "," + this.y + ")");
        else
            System.out.println(animal.name + "_" + animal.id + " был съеден на локации (" + this.x + "," + this.y + ")");

        animals.remove(animal);
    }

    public synchronized void animalLeaveLocation(Animal animal) {

        if(animal.isDead)
            System.out.println("Труп животного " + animal.name + "_" + animal.id + " утаскивается с локации (" + this.x + "," + this.y + ")");
        else
            System.out.println(animal.name + "_" + animal.id + " покидает локацию (" + this.x + "," + this.y + ")");

        animals.remove(animal);
    }

    public synchronized boolean hasPlant() {
        return (plants != null && plants.size() > 0);
    }

    public synchronized void consumePlant() {
        if(plants != null && !plants.isEmpty())
            plants.removeLast(); // Одно растение съедено
    }

    /**
     * Получить добычу из травоядных
     */
    public synchronized List<Animal> getPrey() {
        List<Animal> prey = new ArrayList<>();
        for (Animal animal : animals) {
            //получаем всех травоядных, кроме гусениц
            if (animal instanceof Herbivore &&
                    !(animal instanceof Caterpillar)) {
                prey.add(animal);
            }
        }
        return prey;
    }

    /**
     * Получить насекомых (гусениц)
     */
    public synchronized List<Animal> getCaterpillars() {
        List<Animal> caterpillars = new ArrayList<>();
        List<Animal> animalsCopy = new ArrayList<>(animals);  // Создаем копию списка животных
        for (Animal animal : animalsCopy) {
            if (animal instanceof Herbivore && animal.name.equalsIgnoreCase("Гусінь")) {
                caterpillars.add(animal);
            }
        }
        return caterpillars;

        //Ранее было получено исключение
        //Exception in thread "main" java.util.ConcurrentModificationException
        //at java.base/java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1096)
        //at java.base/java.util.ArrayList$Itr.next(ArrayList.java:1050)
        //at org.model.Location.getInsects(Location.java:60)
        //
        //поэтому создаем копию коллекции и снимаем статистику с копии, также добавлен модификатор синхронизации
        //Копирование списка: Создание копии списка animals для итерации позволяет избежать исключения ConcurrentModificationException, если исходный список будет модифицирован во время выполнения цикла.
        //Синхронизация: Для многопоточного доступа к спискам добавлена синхронизация метода getInsects().
    }

    public synchronized List<Animal> getDeadAnimals() {
        List<Animal> animals = new ArrayList<>();
        for (Animal animal : this.animals) {
            if (animal.isDead) {
                animals.add(animal);
            }
        }
        return animals;
    }

    public synchronized List<Animal> getNoDeadAnimals() {
        List<Animal> animals = new ArrayList<>();
        for (Animal animal : this.animals) {
            if (
                    //отбираем всех живых животных: хищников, травоядных, гусениц
                    ((animal instanceof Predator) ||
                    (animal instanceof Herbivore)) &&
                    (!animal.isDead)
            ) {
                animals.add(animal);
            }
        }
        return animals;
    }
    public synchronized List<Plant> getPlants() {
        return plants;
    }

    public synchronized void growPlant() {
        if (plants == null || plants.size() < 0) {
            plants = new ArrayList<>(); // Создание нового растения
        }

        for(int i = 0; i < GlobalWeather.getSunAndRain(); i++)
        {
            if(plants.size() >= StartParamethers.getMaxPlantAmountInCell())
                break;

            for (int j = 0; j < 20; j++)
                this.plants.add(new Plant());


            System.out.println(this.activeMessage + "выросло растение");
        }
    }

    public synchronized List<Animal> getAnimals() {
        return animals;
    }

    public synchronized HashMap<String, Integer> getAmountPerAnimalType() {
        // Инициализируем карту для хранения количества животных каждого типа
        HashMap<String, Integer> animalCounts = new HashMap<String, Integer>();

        Set<String> animalNames = StartParamethers.getAnimalsNames();

        // Инициализируем все типы животных в карте animalCounts с нуля
        for (String animalName : animalNames) {
            animalCounts.put(animalName, 0);
        }

        // Проходим по каждому животному в локации
        for (Animal animal : animals) {
            String animalType = animal.getName(); // Получаем имя животного

            // Если животное есть в списке типов, увеличиваем счётчик
            if (animalCounts.containsKey(animalType)) {
                int currentCount = animalCounts.get(animalType);
                animalCounts.put(animalType, currentCount + 1);
            }
        }

        return animalCounts;
    }

    public synchronized List<Animal> getAnimals(String name) {
        return animals.stream()
                .filter(animal -> name.equals(animal.name))
                .collect(Collectors.toList());
    }

    // Метод для фильтрации животных по их типу
    public synchronized <T extends Animal> List<T> getAnimalsByType(Class<T> type) {
        return animals.stream()
                .filter(type::isInstance) // Фильтруем животных по типу
                .map(type::cast) // Приводим к нужному типу
                .toList(); // Собираем в список
    }
}
