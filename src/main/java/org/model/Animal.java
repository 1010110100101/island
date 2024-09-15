package org.model;
import org.model.Annotation.AnimalType;
import org.start.StartParamethers;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Animal {

    int id;

    protected boolean finishedCycle;
    public boolean isFinishedCycle() {
        return finishedCycle;
    }
    public void resetCycle() {
        this.finishedCycle = false;
    }
    protected boolean isDead = false;

    protected String name;

    public String getName() { return name; }

    protected float weight;
    private float etalonWeight;

    //процентное соотношение текущей массы тела к эталонному значению
    public float getWeightPercentage() {
        if (etalonWeight == 0) {
            return 0;
        }
        return (weight / etalonWeight) * 100;
    }

    protected int hungerLevel;
    protected int transferSpeed;
    protected Location location;

    public Animal() {
        AnimalType animalType = this.getClass().getAnnotation(AnimalType.class);
        if (animalType != null) {
            this.name = animalType.name();
            this.hungerLevel = animalType.hungerLevel();
        }

        this.finishedCycle = false;
    }

    public abstract void eat();

    public void move(Island island) {

        List<Location> potentialToMoveLocations = new ArrayList<>();


        //1: обрабатываем края карты
        int start_i = (location.getX() - transferSpeed) < 0 ? 0 : location.getX() - transferSpeed;
        int finisf_i = (location.getX() + transferSpeed) > island.getIslandWidth() - 1 ? island.getIslandWidth() - 1 : location.getX() + transferSpeed;

        int start_j = (location.getY() - transferSpeed) < 0 ? 0 : location.getY() - transferSpeed;
        int finisf_j = (location.getY() + transferSpeed) > island.getIslandHeight() - 1 ? island.getIslandHeight() - 1 : location.getY() + transferSpeed;
        //:1

        for(int i = start_i; i < finisf_i; i++) { //по шагам по х
            for (int j = start_j - i; j < finisf_j - i; j++) { // по шагам по у с учетом пройденных уже шагов по х
                Location potentialLocation = island.getLocation(i, j);

                if(potentialLocation != null) { //если локация физически существует по указанным координатам
                    if (potentialLocation.getAnimals(name).size() <
                            StartParamethers.getMaxPossibleAnimalsAmountPerLocation(name)) { //если численность животных этого вида не достигла предела
                        potentialToMoveLocations.add(potentialLocation);
                    }
                }

                //если вдруг имеются дубли, то уникализируем список возможных локаций для перемещения
                potentialToMoveLocations = potentialToMoveLocations.stream().distinct().collect(Collectors.toList());

                //если есть доступные к перемещению потенциальные локации
                if (potentialToMoveLocations != null && potentialToMoveLocations.size() > 0) {
                    System.out.println(name + "_" + id+ " стартовал на миграцию");
                    //выбираем одну из доступных к перемещению локаций наиболее выгодную для проживания и размножения
                    //это та, где популяция данного вида животных наименьшая, а пищи больше
                    //пока что чисто случайным образом
                    Random r = new Random();
                    int index = r.nextInt(0, potentialToMoveLocations.size());

                    //выбранная целевая локация для перемещения
                    Location destinationLocation = potentialToMoveLocations.get(index);

                    //уходим с текущей локации
                    this.location.animalLeaveLocation(this);

                    //приходим в новую локацию
                    destinationLocation.addAnimal(this, false);
                }
            }
        }
    }

    public abstract void reproduce(Island island);

    protected void grotheWeight() {
        this.weight *=1.1f;
    }



    public void starve() {
        this.weight *= 0.95f;
        System.out.println(name + " похудел на 5%");
        if(this.weight < 0.5f*this.etalonWeight) {
            this.isDead = true;
            System.out.println(name + " сдох");
        }
    }

    public void setEtalonWeight(float weight) {
        if(weight <= 0)
            weight = 0;

        etalonWeight = weight;
        this.weight = etalonWeight;
    }

    public float getEtalonWeight() {
        return this.etalonWeight;
    }
}
