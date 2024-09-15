package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Herbivore;

@AnimalType(name = "Коза", hungerLevel = 3)
public class Goat extends Herbivore {
    public Goat() {
        super();
    }
}
