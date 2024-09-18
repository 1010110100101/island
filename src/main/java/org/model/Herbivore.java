package org.model;

import org.model.Annotation.HerbivoreType;
import org.start.StartParamethers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class Herbivore extends Animal {
    public Herbivore() {
        super();
    }

    @Override
    public synchronized void eat() {
        // Получаем пищевые предпочтения для травоядного
        Map<String, Integer> eatingRelations = StartParamethers.getAnimalEatingRelations(this.name);
        if (eatingRelations == null || eatingRelations.isEmpty()) {
            return; // Если нет данных о предпочтениях питания, ничего не делаем
        }

        // Сначала проверяем, есть ли растения для поедания
        if (location.hasPlant()) {
            location.consumePlant(); // Съедаем растение
            this.grotheWeight(); // Травоядное набирает вес
            System.out.println(this.name + " съел растение");
            return; // Если съел растение, выходим из метода
        }

        // Проверяем наличие насекомых, если травоядное может их есть
        HerbivoreType herbivoreType = this.getClass().getAnnotation(HerbivoreType.class);
        if (herbivoreType != null && herbivoreType.eatsInsects()) {
            List<Animal> caterpillars = location.getCaterpillars();
            if (!caterpillars.isEmpty()) {
                Animal caterpillar = caterpillars.get(0);
                int eatProbability = eatingRelations.getOrDefault("Гусінь", 0);
                if (ThreadLocalRandom.current().nextInt(100) < eatProbability) {
                    location.removeAnimal(caterpillar); // Съедаем гусеницу
                    this.grotheWeight(); // Травоядное набирает вес
                    System.out.println(this.name + " съел " + caterpillar.name);
                } else {
                    System.out.println(this.name + " не смог съесть гусеницу из-за низкой вероятности.");
                }
            }
        }
    }


    @Override
    public synchronized void reproduce(Island island) {
        List<Herbivore> herbivores = location.getAnimalsByType(Herbivore.class).stream().filter(anim -> !anim.isDead).toList();

        // Проверяем наличие партнера и факт максимально возможной популяции вида
        if (herbivores.size() >= 2
                &&
                herbivores.size() <
                        StartParamethers
                                .getMaxPossibleAnimalsAmountPerLocation(name)) {

            if (
                    ThreadLocalRandom.current().nextBoolean()
                            && ThreadLocalRandom.current().nextBoolean()
                            && ThreadLocalRandom.current().nextBoolean()
            ) {  // Условие для успешного размножения
                try {
                    Animal newAnimal = AnimalFactory.createAnimalByName(this.name);
                    newAnimal.name = this.name;
                    newAnimal.transferSpeed = this.transferSpeed;
                    newAnimal.setEtalonWeight(this.etalonWeight);
                    newAnimal.weight = newAnimal.getEtalonWeight() * 0.80f;

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