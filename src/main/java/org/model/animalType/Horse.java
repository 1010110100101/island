package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Herbivore;

@AnimalType(name = "Кінь", hungerLevel = 4)
public class Horse extends Herbivore {
    public Horse() {
        super();
    }
}
