package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Annotation.HerbivoreType;
import org.model.Herbivore;

@AnimalType(name = "Качка", hungerLevel = 2)
@HerbivoreType(eatsInsects = true)
public class Duck extends Herbivore {
    public Duck() {
        super();
    }
}
