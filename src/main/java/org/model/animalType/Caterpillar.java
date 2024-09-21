package org.model.animalType;

import org.model.*;
import org.model.Annotation.AnimalType;
import org.start.StartParamethers;


import java.util.List;

@AnimalType(name = "Гусінь", hungerLevel = 1)
public class Caterpillar extends Herbivore {

    @Override
    public void eat() {
        if (location.hasPlant()) {
            location.consumePlant();
            grotheWeight();
            grotheWeight();
            System.out.println(name + " съел растение");
        }
    }

    @Override
    public void move(Island island) {

    }

    public synchronized void starve() {
        this.weight *= 0.95f;
        System.out.println(name + " похудел на 5%");
        if(this.weight < 0.5f * this.etalonWeight) {
            this.isDead = true;
            System.out.println(this.name + " сдох");
        }
    }

    @Override
    public synchronized void reproduce(Island island) {

        List<Caterpillar> caterpilars = location.getAnimalsByType(Caterpillar.class).stream().filter(anim -> !anim.isDead).toList();

        if (caterpilars.size() < StartParamethers.getMaxPossibleAnimalsAmountPerLocation(name)) {
            if (getWeightPercentage() > 50) {
                for (int i = 0; i < 300; i++) {
                    try {
                        Animal newAnimal = AnimalFactory.createAnimalByName(this.name);
                        newAnimal.name = this.name;
                        newAnimal.transferSpeed = this.transferSpeed;
                        newAnimal.setEtalonWeight(this.etalonWeight);
                        newAnimal.weight = newAnimal.getEtalonWeight() * 0.95f;

                        location.addAnimal(newAnimal, true);

                        this.weight *= 0.95f;

                        this.eat();
                        // После размножения, завершение цикла
                        this.finishedCycle = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(name + " размножился.");
                }
            }
        }
    }
}
