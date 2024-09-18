package org.model;

import org.model.animalType.Plant;
import org.start.StartParamethers;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Island {
    private final Location[][] grid;

    public int getIslandHeight() { return height; }
    public int getIslandWidth() { return width; }

    private final int width;
    private final int height;

    private static AtomicBoolean isIslandInLife;


    /**
     * Создает массив локаций
     * @param width - ширина острова
     * @param height - высота острова
     */
    public Island(int width, int height, AtomicBoolean isIslandInLife_) {

        isIslandInLife = isIslandInLife_;

        this.width = Math.abs(width); //только положительное значение
        this.height = Math.abs(height); //только положительное значение

        this.grid = new Location[width][height]; // инициализация двумерного массива локаций размерностью width/height

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                grid[i][j] = new Location(i, j);
    }

    public void simulateLifeCycle() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(16);
        scheduler.scheduleAtFixedRate(() -> growPlants(), 0, 250, TimeUnit.MILLISECONDS); //рост травы
        scheduler.scheduleAtFixedRate(() -> animalsLifeCycle(), 0, 500, TimeUnit.MILLISECONDS); //день проживания животных
        scheduler.scheduleAtFixedRate(() -> rottingСorpses(), 0, 500, TimeUnit.MILLISECONDS); //разложение трупов умерших от голода животных
        scheduler.scheduleAtFixedRate(() -> showStatistics(), 0, 500, TimeUnit.MILLISECONDS); //вывод статистики в консоль
    }

    public synchronized void showStatistics() {
        int totalPlants = 0;
        int totalCaterpilars = 0;
        int totalAnimals = 0;
        int totalDeadAnimals = 0;
        int totalPredators = 0;
        int totalHerbivores = 0;

        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int locationTotalPlants = grid[i][j].getPlants().size();
                totalPlants += locationTotalPlants;

                int locationTotalCaterpilars = grid[i][j].getCaterpillars().size();
                totalCaterpilars += locationTotalCaterpilars;

                int localTotalAnimals = grid[i][j].getNoDeadAnimals().size();
                totalAnimals += localTotalAnimals;

                int localTotalDeadAnimals = grid[i][j].getDeadAnimals().size();
                totalDeadAnimals += localTotalDeadAnimals;

                int localTotalHerbivores = grid[i][j].getAnimalsByType(Herbivore.class).size();
                totalHerbivores += localTotalHerbivores;

                int localTotalPredators = grid[i][j].getAnimalsByType(Predator.class).size();
                totalPredators += localTotalPredators;
            }
        }

        System.out.println("Всего Растений: " + String.valueOf(totalPlants));
        System.out.println("Всего гусениц: " + String.valueOf(totalCaterpilars));
        System.out.println("Всего животных: " + String.valueOf(totalAnimals));
        System.out.println("  из них травоядных: " + String.valueOf(totalHerbivores));
        System.out.println("  из них хищников: " + String.valueOf(totalPredators));
        System.out.println("Всего разлагающихся трупов: " + String.valueOf(totalDeadAnimals));

    }

    /**
     * Инициация растительности на острове
     */
    public void putPlantsOnIsland() {
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
            {
                for (int amount = 0; amount <= StartParamethers.getMaxPlantAmountInCell(); amount++) {
                    try {
                        Plant newPlant = new Plant();
                        grid[i][j].addPlant(newPlant);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
    }

    /**
     * Инициация животных на острове
     */
    public void putAnimalsOnIsland() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //проходим по списку животных в коллекции
                for (Map.Entry<String, Map<String, Float>> animal : StartParamethers.getOwnValues().entrySet()) {
                    String animalName = animal.getKey();

                    if (animalName.equalsIgnoreCase("Рослини"))
                        continue;

                    Map<String, Float> animalFeatures = animal.getValue();

                    int maxAnimalAmountPerCell = animalFeatures
                            .get("Максимальна кількість на клітинці")
                            .intValue();
                    int animalSpeedTransfer = animalFeatures
                            .get("Швидкість переміщення, клітинок/хід")
                            .intValue();
                    float weight = animalFeatures
                            .get("Вага, кг")
                            .floatValue();

                    int initialAnimalAmountInCell = ThreadLocalRandom
                            .current()
                            .nextInt(0, maxAnimalAmountPerCell);

                    for (int amount = 0; amount <= initialAnimalAmountInCell; amount++) {
                        try {
                            Animal newAnimal = AnimalFactory
                                    .createAnimalByName(animalName);

                            newAnimal.name = animalName;
                            newAnimal.transferSpeed = animalSpeedTransfer;
                            newAnimal.etalonWeight = weight;
                            newAnimal.weight = newAnimal.getEtalonWeight() * 0.80f;

                            grid[i][j].addAnimal(newAnimal, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    /**
     * Предоставляет доступ к требуемой локации по координатам x и y
     * @param x
     * @param y
     * @return
     */
    public Location getLocation(int x, int y) {
        x = (x > 0) ? (Math.min(x, (this.width - 1))) : 0;
        y = (y > 0) ? (Math.min(y, (this.height - 1))) : 0;

        return grid[x][y];
    }

    private synchronized void growPlants() {
        if(!isIslandInLife.get())
            return;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j].growPlant();
            }
        }
    }

    private synchronized void animalsLifeCycle() {
        if (!isIslandInLife.get())
            return;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Location location = grid[i][j];

                //жизненный цикл живых животных
                for (Animal animal : location.getNoDeadAnimals()) {
                    //голодание
                    animal.starve();

                    //если животное живое (не умерло еще)
                    if (!animal.isDead) {
                        if(animal.getWeightPercentage() < 85) {//если вес животного близок к идеальному, пища ему не нужна
                            //питание
                            animal.eat();
                        } else {
                            //размножение
                            animal.reproduce(this);
                        }

                        //перемещение
                        animal.move(this);
                    }
                }
            }
        }
    }

    private synchronized void rottingСorpses() {
        ExecutorService animalExecutor = Executors.newFixedThreadPool(2);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Location location = grid[i][j];

                for (Animal deadAnimal : location.getDeadAnimals()) {
                    animalExecutor.submit(() -> {
                        //полное сгниение трупов мертвых животных
                        location.removeAnimal(deadAnimal);
                    });
                }
            }
        }
        animalExecutor.shutdown();
    }
}
