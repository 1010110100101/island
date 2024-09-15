package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Annotation.PredatorType;
import org.model.Predator;

// Класс Волк
@AnimalType(name = "Вовк", hungerLevel = 5)
@PredatorType(aggressive = true)
public class Wolf extends Predator {
    public Wolf() {
        super();
    }
}
