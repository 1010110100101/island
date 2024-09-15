package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Herbivore;

@AnimalType(name = "Вівця", hungerLevel = 3)
public class Sheep extends Herbivore {
    public Sheep() {
        super();
    }
}
