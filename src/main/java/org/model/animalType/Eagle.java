package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Annotation.PredatorType;
import org.model.Predator;

@AnimalType(name = "Орел", hungerLevel = 4)
@PredatorType(aggressive = false)
public class Eagle extends Predator {
    public Eagle() {
        super();
    }
}
