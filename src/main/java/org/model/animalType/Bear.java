package org.model.animalType;

import org.model.Annotation.AnimalType;
import org.model.Annotation.PredatorType;
import org.model.Predator;

@AnimalType(name = "Ведмідь", hungerLevel = 6)
@PredatorType(aggressive = true)
public class Bear extends Predator {}
