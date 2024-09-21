package org.model;

import org.model.animalType.Animal;
import org.start.StartParamethers;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class Predator extends Animal {
    public Predator() {
        super();
    }

    //Будем получать доступ к настройкам вероятностей для каждого животного через StartParamethers.getAnimalEatingRelations().
    //Сначала определим жертву на локации (одно животное).
    //После этого, используя вероятность, попробуем съесть выбранное животное.
    @Override
    public synchronized void eat() {
        if (this.getWeightPercentage() > 80) {
            // Если вес животного близок к идеальному, пища ему не нужна
            return;
        }

        // Получаем пищевые предпочтения для хищника
        Map<String, Integer> eatingRelations = StartParamethers.getAnimalEatingRelations(this.name);
        if (eatingRelations == null || eatingRelations.isEmpty()) {
            return; // Если нет данных о предпочтениях питания, ничего не делаем
        }

        List<Animal> prey = location.getPrey(); // Получаем список добычи на текущей локации
        if (prey.isEmpty()) {
            return; // Если нет доступной добычи, выходим
        }

        // Выбираем случайное животное из списка добычи
        Animal potentialPrey = prey.get(ThreadLocalRandom.current().nextInt(prey.size()));

        // Проверяем вероятность съедания этого животного
        int eatProbability = eatingRelations.getOrDefault(potentialPrey.getName(), 0);
        if (ThreadLocalRandom.current().nextInt(100) < eatProbability) {
            // Если случайное число меньше вероятности, хищник съедает жертву
            location.removeAnimal(potentialPrey);
            grotheWeight(); // Хищник набирает вес
            System.out.println(this.getName() + " съел " + potentialPrey.getName());
        } else {
            // Если не удалось съесть
            System.out.println(this.getName() + " не смог съесть " + potentialPrey.getName() + " из-за низкой вероятности.");
        }
    }


    @Override
    public synchronized void reproduce(Island island) {
        if(getWeightPercentage() < 90)
            // Размножается только если все хорошо с его весом, едой, здоровьем
            return;

        List<Predator> predators = location.getAnimalsByType(Predator.class).stream().filter(anim -> !anim.isDead).toList();

        // Проверяем наличие партнера и факт максимально возможной популяции вида
        if (predators.size() >= 2 &&
                predators.size() <
                        StartParamethers
                                .getMaxPossibleAnimalsAmountPerLocation(name)) {

            if (ThreadLocalRandom.current().nextBoolean()
            ) {  // Условие для успешного размножения - трижды чтоб вероятность была крайне мала чтоб не писать сложный алгоритм определения двух в паре и надолго не могут размножаться после размножения
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

                    System.out.println(name + " (хищник) размножился.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
