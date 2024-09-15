package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Herbivore;

@AnimalType(name = "Кролик", hungerLevel = 2)
public class Rabbit extends Herbivore {
    public Rabbit() {
        super();
    }
}
