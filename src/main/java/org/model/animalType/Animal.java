package org.model.animalType;
import org.model.Annotation.AnimalType;
import org.model.Island;
import org.model.Location;
import org.start.StartParamethers;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class Animal {

    public int id;

    protected boolean finishedCycle;
    public boolean isFinishedCycle() {
        return finishedCycle;
    }
    public void resetCycle() {
        this.finishedCycle = false;
    }
    public boolean isDead = false;

    public String name;

    public String getName() { return name; }

    public float weight;
    public float etalonWeight;

    //процентное соотношение текущей массы тела к эталонному значению
    public float getWeightPercentage() {
        if (etalonWeight == 0) {
            return 0;
        }
        return (weight / etalonWeight) * 100;
    }

    protected int hungerLevel;
    public int transferSpeed;
    public Location location;

    public Animal() {
        AnimalType animalType = this.getClass().getAnnotation(AnimalType.class);
        if (animalType != null) {
            this.name = animalType.name();
            this.hungerLevel = animalType.hungerLevel();
        }

        this.finishedCycle = false;
    }

    public abstract void eat();

    public synchronized void move(Island island) {
        List<Location> potentialToMoveLocations = new ArrayList<>();

        // Текущие координаты животного
        int currentX = location.getX();
        int currentY = location.getY();

        //подготовка допустимых локаций для миграции
        // Проходим по всем возможным смещениям в пределах скорости перемещения
        for (int dx = -transferSpeed; dx <= transferSpeed; dx++) {
            for (int dy = -transferSpeed + Math.abs(dx); dy <= transferSpeed - Math.abs(dx); dy++) {

                //на всякий случай дополнительно проверяем, что сумма модулей
                // смещений по x и y не превышает скорость перемещения
                if (Math.abs(dx) + Math.abs(dy) <= transferSpeed) {

                    int newX = currentX + dx;
                    int newY = currentY + dy;

                    // Проверяем, что новые координаты находятся в пределах острова
                    if (newX >= 0 && newX < island.getIslandWidth() &&
                            newY >= 0 && newY < island.getIslandHeight()) {

                        Location potentialLocation = island.getLocation(newX, newY);

                        if (potentialLocation != null) {
                            // Проверяем, что численность животных этого вида на новой локации не достигла предела
                            if (potentialLocation.getAnimals(name).size() <
                                    StartParamethers.getMaxPossibleAnimalsAmountPerLocation(name)) {
                                potentialToMoveLocations.add(potentialLocation);
                            }
                        }
                    }
                }
            }
        }

        //непосредственно акт миграции
        // Если есть доступные для перемещения локации
        if (!potentialToMoveLocations.isEmpty()) {
            System.out.println(name + "_" + id + " стартовал на миграцию");

            // Выбираем случайную локацию из доступных
            int index = ThreadLocalRandom.current().nextInt(potentialToMoveLocations.size());
            Location destinationLocation = potentialToMoveLocations.get(index);

            // Уходим с текущей локации
            this.location.animalLeaveLocation(this);

            // Приходим в новую локацию
            destinationLocation.addAnimal(this, false);
        }
    }

    public abstract void reproduce(Island island);

    protected synchronized void grotheWeight() {
        this.weight *=1.1f;
    }



    public synchronized void starve() {
        this.weight *= 0.95f;
        System.out.println(name + " похудел на 5%");
        if(this.weight < 0.5f * this.etalonWeight) {
            this.isDead = true;
            System.out.println(this.name + " сдох");
        }
    }

    public void setEtalonWeight(float weight) {
        if(weight <= 0)
            weight = 0;

        this.etalonWeight = weight;
        //this.weight = etalonWeight;
    }

    public float getEtalonWeight() {
        return this.etalonWeight;
    }
}
