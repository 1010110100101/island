package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Herbivore;

@AnimalType(name = "Миша", hungerLevel = 1)
public class Mouse extends Herbivore {
    public Mouse() {
        super();
    }
}
