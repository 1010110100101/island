package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Annotation.PredatorType;
import org.model.Predator;

@AnimalType(name = "Лисиця", hungerLevel = 3)
@PredatorType(aggressive = false)
public class Fox extends Predator {
    public Fox() {
    super();
}}
