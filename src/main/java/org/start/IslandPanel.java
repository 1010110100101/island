package org.start;

import org.model.AnimalFactory;
import org.model.Island;
import org.model.Location;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class IslandPanel extends JPanel {

    private Island island;

    public IslandPanel(Island island) {
        this.island = island;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (island == null) {
            return;
        }

        int cellSize = Math.min(getWidth() / island.getIslandWidth(), getHeight() / island.getIslandHeight());

        for (int i = 0; i < island.getIslandWidth(); i++) {
            for (int j = 0; j < island.getIslandHeight(); j++) {
                int x = i * cellSize;
                int y = j * cellSize;

                // Рисуем границу клетки
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);

                // Получаем локацию
                Location location = island.getLocation(i, j);
                if (location == null)
                    continue;

                // Если есть растение, рисуем его
                if (location.hasPlant()) {
                    ImageIcon plantIcon = getPlantIcon();
                    plantIcon.paintIcon(this, g, x + cellSize / 2 - 8, y + cellSize / 2 - 8);
                }


                try {
                    // Рисуем иконки животных
                    int nextAnimalX = 0; // смещение по X для следующей иконки
                    int nextAnimalY = 0; // смещение по Y для следующей иконки

                    int animalNumberInLocation = 0;

                    for (Map.Entry<String, Integer> entry : location.getAmountPerAnimalType().entrySet()) {
                        String animalName = entry.getKey();       // Ключ — тип животного (например, "Вовк")
                        Integer animalCount = entry.getValue();   // Значение — количество животных этого типа

                        if (animalCount > 0) {
                            ImageIcon icon = getAnimalIcon(animalName);
                            icon.paintIcon(this, g, x + nextAnimalX, y + nextAnimalY);




                            if(animalNumberInLocation > 0 && animalNumberInLocation % 2 == 0)
                            {
                                nextAnimalX = 0; // смещение для следующей иконки
                                nextAnimalY += 26; // смещение для следующей иконки
                            }
                            else {
                                nextAnimalX += 46; // смещение для следующей иконки
                            }
                            animalNumberInLocation++;
                        }
                    }
                }
                catch (Exception e) {}

            }
        }
    }

    private ImageIcon getAnimalIcon(String animalName) {

        String animalResourceName = AnimalFactory.getClassNameByAnimalName(animalName).toLowerCase();
        return new ImageIcon(getClass().getResource("/icons/"+animalResourceName+".png"));
    }

    private ImageIcon getPlantIcon() {
        return new ImageIcon(getClass().getResource("/icons/plant.png"));
    }
}
