package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Herbivore;
import org.model.Island;

@AnimalType(name = "Гусінь", hungerLevel = 1)
public class Caterpillar extends Herbivore {

    @Override
    public void eat() {
        if (location.hasPlant()) {
            location.consumePlant();
            grotheWeight();
            System.out.println(name + " съел растение");
        }
    }

    @Override
    public void move(Island island) {

    }
}
