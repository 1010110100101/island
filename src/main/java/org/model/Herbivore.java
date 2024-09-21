package org.model;

import org.model.Annotation.HerbivoreType;
import org.model.animalType.Animal;
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

        if(getWeightPercentage() > 90)
            return;

        // Получаем пищевые предпочтения для травоядного
        Map<String, Integer> eatingRelations = StartParamethers.getAnimalEatingRelations(this.name);
        if (eatingRelations == null || eatingRelations.isEmpty()) {
            return; // Если нет данных о предпочтениях питания, ничего не делаем
        }

        // Сначала проверяем, есть ли растения для поедания
        if (location.hasPlant()) {
            // Получаем вероятность съедания растения
            int plantEatProbability = eatingRelations.getOrDefault("Рослини", 0);

            // Проверяем вероятность съедания растения
            if (ThreadLocalRandom.current().nextInt(100) < plantEatProbability) {
                location.consumePlant(); // Съедаем растение
                this.grotheWeight(); // Травоядное набирает вес
                System.out.println(this.name + " съел растение");

            } else {
                System.out.println(this.name + " не смог съесть растение из-за низкой вероятности.");
            }

            return; // Если съел растение, выходим из метода
        }

        if(getWeightPercentage() > 85)
            return;

        // Проверяем наличие насекомых (гусениц), если травоядное может их есть
        HerbivoreType herbivoreType = this.getClass().getAnnotation(HerbivoreType.class);
        if (herbivoreType != null && herbivoreType.eatsInsects()) {
            List<Animal> caterpillars = location.getCaterpillars();
            if (!caterpillars.isEmpty()) {
                // Получаем вероятность съедания гусеницы
                int caterpillarEatProbability = eatingRelations.getOrDefault("Гусінь", 0);

                // Проверяем вероятность съедания гусеницы
                if (ThreadLocalRandom.current().nextInt(100) < caterpillarEatProbability) {
                    Animal caterpillar = caterpillars.get(ThreadLocalRandom.current().nextInt(caterpillars.size())); // Берём случайную гусеницу
                    location.removeAnimal(caterpillar); // Съедаем гусеницу
                    this.grotheWeight(); // Травоядное набирает вес
                    System.out.println(this.name + " съел гусеницу " + caterpillar.name);
                } else {
                    System.out.println(this.name + " не смог съесть гусеницу из-за низкой вероятности.");
                }
            }
        }
    }


    @Override
    public synchronized void reproduce(Island island) {

        if(getWeightPercentage() < 80)
            // Размножается только если все хорошо с его весом, едой, здоровьем
            return;

        List<Herbivore> herbivores = location.getAnimalsByType(Herbivore.class).stream().filter(anim -> !anim.isDead).toList();

        // Проверяем наличие партнера и факт максимально возможной популяции вида
        if (herbivores.size() >= 2
                &&
                herbivores.size() <
                        StartParamethers
                                .getMaxPossibleAnimalsAmountPerLocation(name)) {

            if (  // Условие для успешного размножения
                    ThreadLocalRandom.current().nextBoolean()
            ) {
                try {
                    Animal newAnimal = AnimalFactory.createAnimalByName(this.name);
                    newAnimal.name = this.name;
                    newAnimal.transferSpeed = this.transferSpeed;
                    newAnimal.setEtalonWeight(this.etalonWeight);
                    newAnimal.weight = newAnimal.getEtalonWeight() * 0.70f;

                    location.addAnimal(newAnimal, true);

                    this.weight *= 0.8f;

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