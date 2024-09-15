package org.start;

import org.model.Island;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    static Island island;
    // Используем AtomicBoolean
    //AtomicBoolean: Это класс, предоставляющий возможность безопасного многопоточного доступа к переменной boolean.
    // Его можно использовать для передачи и изменения состояния между разными классами и потоками.
    static AtomicBoolean isIslandInLife;
    static AtomicBoolean allAnimalPutedOnIsland = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
        // Запускаем GUI в потоке AWT
        // вызов конструктора без параметров
        //SwingUtilities.invokeLater(MainWindow::new);

        isIslandInLife = new AtomicBoolean(false);
        // Вызов конструктора MainWindow с параметрами
        SwingUtilities.invokeLater(() -> new MainWindow(800, 800, isIslandInLife));


        //SwingUtilities.invokeLater(() -> new MainFrame(1400, 800));

        isIslandInLife.set(true);

        // Загружаем данные и инициализируем остров после загрузки данных
        createIsland();

        // Запускаем симуляцию жизненного цикла
        // движуха на острове не стартанет доколе не будет создан остров и пока не будут расставлены все животные
        while(island == null || allAnimalPutedOnIsland.get() == false)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        island.simulateLifeCycle();
    }

    // Метод инициализации острова и размещения животных
    public static void createIsland() throws InterruptedException {

        loadAnimalOwnData(() -> {
            // Проверяем, что данные загружены
            if (StartParamethers.getOwnValues() == null || StartParamethers.getOwnValues().isEmpty()) {
                System.out.println("Ошибка: данные животных не загружены.");
                return; // Останавливаем выполнение, если данные не загружены
            }

            System.out.println("Данные животных загружены.");

            // Рождение острова
            island = new Island(5, 5, isIslandInLife);

            // Заполняем остров растениями
            island.putPlantsOnIsland();
            System.out.println("Растения посажены.");

            // Заполняем остров животными
            island.putAnimalsOnIsland();
            System.out.println("Животные расставлены.");

            allAnimalPutedOnIsland.set(true);
        });



        loadAnimalEatingRelations(() -> {
            //проверяем, что данные загружены
            if (StartParamethers.getEatingRelations() == null || StartParamethers.getEatingRelations().isEmpty()) {
                System.out.println("Ошибка: настройки пищевой цепи не загружены.");
                return; // Останавливаем выполнение, если данные не загружены
            }

            System.out.println("Данные настройки пищевой цепи загружены.");
        });
    }

    // Метод асинхронной загрузки данных с коллбэком
    public static void loadAnimalOwnData(Runnable callback) {
        new Thread(() -> {
            try {
                // Загрузка данных из JSON файла
                StartParamethers.getOwnValues();

                // После завершения загрузки данных вызываем коллбэк
                callback.run();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ошибка загрузке данных о животных, растительности из JSON.");
            }
        }).start();
    }

    public static void loadAnimalEatingRelations(Runnable callback) {
        new Thread(() -> {
            try {
                // Загрузка данных из JSON файла
                StartParamethers.getEatingRelations();

                // После завершения загрузки данных вызываем коллбэк
                callback.run();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ошибка загрузке данных о животных, растительности из JSON.");
            }
        }).start();
    }
}