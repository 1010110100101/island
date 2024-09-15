package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Annotation.PredatorType;
import org.model.Predator;

@AnimalType(name = "Удав", hungerLevel = 4)
@PredatorType(aggressive = true)
public class Python extends Predator {
    public Python() {
        super();
    }
}
