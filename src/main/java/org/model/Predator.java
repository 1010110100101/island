package org.model;

import org.model.Annotation.PredatorType;
import org.start.StartParamethers;

import java.util.List;
import java.util.Random;



public class Predator extends Animal {
    public Predator() {
        super();
    }

    @Override
    public void eat() {
        PredatorType predatorType = this.getClass().getAnnotation(PredatorType.class);
        List<Animal> prey = location.getPrey();
        if (!prey.isEmpty()) {
            Animal animal = prey.get(0);
            location.removeAnimal(animal);
            grotheWeight();
            if (predatorType != null && predatorType.aggressive()) {
                System.out.println(name + " (агрессивный хищник) съел " + animal.name);
            } else {
                System.out.println(name + " (хищник) съел " + animal.name);
            }
        }
    }

    @Override
    public void reproduce(Island island) {
        List<Predator> predators = location.getAnimalsByType(Predator.class);

        // Проверяем наличие партнера и факт максимально возможной популяции вида
        if (predators.size() >= 2
                &&
                predators.size() <
                        StartParamethers
                                .getMaxPossibleAnimalsAmountPerLocation(name)) {

            Random random = new Random();
            if (random.nextBoolean()) {  // Условие для успешного размножения
                try {
                    Animal newAnimal = AnimalFactory.createAnimalByName(this.name);
                    newAnimal.name = this.name;
                    newAnimal.transferSpeed = transferSpeed;
                    newAnimal.setEtalonWeight(getEtalonWeight());

                    location.addAnimal(newAnimal, true);

                    // После размножения, завершение цикла
                    this.finishedCycle = true;

                    System.out.println(name + " (хищник) размножился.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
