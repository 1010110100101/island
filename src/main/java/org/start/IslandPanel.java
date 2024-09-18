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

        // Устанавливаем предпочтительные размеры панели
        // Измените ширину и высоту панели в зависимости от ваших требований
        setPreferredSize(new Dimension(1000, 800)); // 1000 пикселей ширина, 800 пикселей высота
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

                    // Отображаем количество растений правее от иконки
                    int plantCount = location.getPlants().size();
                    g.setColor(Color.BLACK);
                    g.drawString("" + plantCount, x + cellSize / 2 + 10, y + cellSize / 2);  // Смещение текста рядом с иконкой растения
                }


                try {
                    // Рисуем иконки животных
                    int nextAnimalX = 0; // смещение по X для следующей иконки
                    int nextAnimalY = 0; // смещение по Y для следующей иконки

                    for (Map.Entry<String, Integer> entry : location.getAmountPerAnimalType().entrySet()) {
                        String animalName = entry.getKey();       // Ключ — тип животного (например, "Вовк")
                        Integer animalCount = entry.getValue();   // Значение — количество животных этого типа

                        if (animalCount > 0) {
                            ImageIcon icon = getAnimalIcon(animalName);
                            icon.paintIcon(this, g, x + nextAnimalX, y + nextAnimalY);

                            // Отображаем количество правее от иконки
                            g.setColor(Color.BLACK);
                            g.drawString("" + animalCount, x + nextAnimalX + 10, y + nextAnimalY + 10); // Смещение текста рядом с иконкой

                            nextAnimalX += 36;
                            if(nextAnimalX > 110)
                            {
                                nextAnimalX = 0;
                                nextAnimalY += 36;
                            }
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
