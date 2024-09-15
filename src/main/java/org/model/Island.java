package org.model;

import org.model.animalType.Caterpillar;
import org.model.animalType.Plant;
import org.start.StartParamethers;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Island {
    private final Location[][] grid;

    public int getIslandHeight() { return height; }
    public int getIslandWidth() { return width; }

    private final int width;
    private final int height;
    private Random ran = new Random();

    static AtomicBoolean isIslandInLife;


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

    /**
     * Инициация растительности на острове
     */
    public void putPlantsOnIsland() {

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
            {
                for (int amount = 0; amount <= StartParamethers.getInitialPlantAmountInCell(); amount++) {
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

                    int maxAnimalAmountPerCell = animalFeatures.get("Максимальна кількість на клітинці").intValue();
                    int animalSpeedTransfer = animalFeatures.get("Швидкість переміщення, клітинок/хід").intValue();
                    float weight = animalFeatures.get("Вага, кг").floatValue();

                    int initialAnimalAmountInCell = ran.nextInt(0, maxAnimalAmountPerCell);

                    for (int amount = 0; amount <= initialAnimalAmountInCell; amount++) {
                        try {
                            Animal newAnimal = AnimalFactory.createAnimalByName(animalName);
                            newAnimal.name = animalName;
                            newAnimal.transferSpeed = animalSpeedTransfer;
                            newAnimal.setEtalonWeight(weight);

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
        x = (x > 0) ? (Math.min(x, (this.width - 1))) : (x = 0);
        y = (y > 0) ? (Math.min(y, (this.height - 1))) : (y = 0);

        return grid[x][y];
    }

    public void simulateLifeCycle() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
        scheduler.scheduleAtFixedRate(() -> growPlants(), 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> animalsLifeCycle(), 0, 5, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> rottingСorpses(), 0, 5, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> showStatistics(), 0, 1, TimeUnit.SECONDS);
    }

    private void growPlants() {
        if(!isIslandInLife.get())
            return;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j].growPlant();
            }
        }
    }

    private void animalsLifeCycle() {
        if (!isIslandInLife.get())
            return;

        ExecutorService animalExecutor = Executors.newCachedThreadPool();
        //while(isIslandInLife.get()) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Location location = grid[i][j];

                //жизненный цикл живых животных
                for (Animal animal : location.getNoDeadAnimals()) {
                    //animalExecutor.submit(() ->
                    //{
                        //голодание
                        animal.starve();

                        //если животное живое (не умерло еще)
                        if (!animal.isDead) {
                            if (animal.getWeightPercentage() <= 85) {
                                //питание
                                animal.eat();
                            } else {
                                //размножение
                                animal.reproduce(this);
                            }
                            //перемещение
                            animal.move(this);
                        }
                    //});

                }
            }
        }
        //animalExecutor.shutdown();
    }


    private void rottingСorpses() {

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

    public void showStatistics()
    {
        int totalPlants = 0;
        int totalCaterpilars = 0;
        int totalAnimals = 0;
        int totalDeadAnimals = 0;

        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //System.out.println("Локация[" + String.valueOf(i) + "][" + String.valueOf(j) + "]:");


                int locationTotalPlants = grid[i][j].getPlants().size();
                totalPlants += locationTotalPlants;
                //System.out.println("Растений: " + String.valueOf(locationTotalPlants));


                int locationTotalCaterpilars = grid[i][j].getCaterpillars().size();
                totalCaterpilars += locationTotalCaterpilars;
                //System.out.println("Гусениц: " + String.valueOf(locationTotalCaterpilars));


                int localTotalAnimals = grid[i][j].getNoDeadAnimals().size();
                totalAnimals += localTotalAnimals;
                //System.out.println("Всего животных: " + String.valueOf(localTotalAnimals));

                int localTotalDeadAnimals = grid[i][j].getDeadAnimals().size();
                totalDeadAnimals += localTotalDeadAnimals;
                //System.out.println("Всего разлагающихся трупов: " + String.valueOf(localTotalDeadAnimals));
            }
        }

        System.out.println("Всего гусениц: " + String.valueOf(totalCaterpilars));
        System.out.println("Всего животных: " + String.valueOf(totalAnimals));
        System.out.println("Всего разлагающихся трупов: " + String.valueOf(totalDeadAnimals));
        System.out.println("Всего Растений: " + String.valueOf(totalPlants));
    }
}
