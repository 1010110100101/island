package org.model;

import org.model.Annotation.HerbivoreType;
import org.start.StartParamethers;

import java.util.List;
import java.util.Random;



public class Herbivore extends Animal {
    public Herbivore() {
        super();
    }

    @Override
    public void eat() {
        HerbivoreType herbivoreType = this.getClass().getAnnotation(HerbivoreType.class);
        if (location.hasPlant()) {
            location.consumePlant();//съедание растения
            grotheWeight();
            System.out.println(name + " (травоядный) съел растение");
        }

        else if (herbivoreType != null && herbivoreType.eatsInsects()) {
            List<Animal> caterpillars = location.getCaterpillars();
            if (!caterpillars.isEmpty()) {
                Animal caterpillar = caterpillars.get(0);
                location.removeAnimal(caterpillar);
                grotheWeight();
                System.out.println(name + " съел " + caterpillar.name);
            }
        }
    }

    @Override
    public void reproduce(Island island) {

        List<Herbivore> herbivores = location.getAnimalsByType(Herbivore.class);

        // Проверяем наличие партнера и факт максимально возможной популяции вида
        if (herbivores.size() >= 2
                &&
                herbivores.size() <
                        StartParamethers
                                .getMaxPossibleAnimalsAmountPerLocation(name)) {
            Random random = new Random();
            if (random.nextBoolean()) {  // Условие для успешного размножения
                try {
                    Animal newAnimal = AnimalFactory.createAnimalByName(this.name);
                    newAnimal.name = this.name;
                    newAnimal.transferSpeed = transferSpeed;
                    newAnimal.setEtalonWeight(getEtalonWeight());

                    location.addAnimal(newAnimal, true);

                    // После размножения, завершение цикла
                    this.finishedCycle = true;

                    System.out.println(name + " (травоядный) размножился.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}