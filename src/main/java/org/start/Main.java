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
    static AtomicBoolean isIslandInLife = new AtomicBoolean(false);
    static AtomicBoolean allAnimalPutedOnIsland = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
        // Запускаем GUI в потоке AWT
        // вызов конструктора без параметров
        //SwingUtilities.invokeLater(MainWindow::new);
        // Вызов конструктора MainWindow с параметрами
        SwingUtilities.invokeLater(() -> new MainWindow(1400, 800, isIslandInLife));

        isIslandInLife.set(true);

        // Загружаем данные и инициализируем остров после загрузки данных
        createIsland();

        // Запускаем симуляцию жизненного цикла
        // движуха на острове не стартанет доколе не будет создан остров и пока не будут расставлены все животные
        while(island == null || !allAnimalPutedOnIsland.get())
        {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //запускаем движение жизней животных
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

            // РОЖДЕНИЕ ОСТРОВА
            island = new Island(5, 5, isIslandInLife);

            // Заполнение остров растениями
            island.putPlantsOnIsland();
            System.out.println("Растения посажены.");

            // Заполнение острова животными
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
    private static void loadAnimalOwnData(Runnable callback) {
        new Thread(() -> {
            try {
                // Загрузка данных из JSON файла
                StartParamethers.getOwnValues();

                // После завершения загрузки данных вызываем коллбэк
                callback.run();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ошибка загрузки JSON-данных о животных, растительности.");
            }
        }).start();
    }

    // Метод асинхронной загрузки данных с коллбэком
    private static void loadAnimalEatingRelations(Runnable callback) {
        new Thread(() -> {
            try {
                // Загрузка данных из JSON файла
                StartParamethers.getEatingRelations();

                // После завершения загрузки данных вызываем коллбэк
                callback.run();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ошибка загрузке JSON-данных о питании животных, растительности.");
            }
        }).start();
    }
}