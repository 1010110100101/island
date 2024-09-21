package org.start;

import org.model.GlobalWeather;
import org.model.Island;
import org.model.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainWindow extends JFrame {

    private JPanel mainPanel;
    private IslandPanel islandPanel; // Панель для отображения острова

    private JButton button0;
    private JButton addSunAndRain_btn;
    private JButton reduceSunAndRain_btn;
    private JTextField textField1;

    private JList<String> animalList; // Список для отображения животных и их количества
    private DefaultListModel<String> listModel; // Модель списка для динамического обновления

    private AtomicBoolean isIslandInLife;
    private Island island;

    public MainWindow(int width, int height, AtomicBoolean isIslandInLife) {

        this.isIslandInLife = isIslandInLife;

        // Инициализация главной панели
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout()); // Используем BorderLayout для размещения панели острова и кнопок

        textField1 = new JTextField(10); // Задаем ширину текстового поля

        // Инициализация кнопок
        button0 = new JButton("Остановить остров");
        addSunAndRain_btn = new JButton("Добавить солнце и дождь");
        reduceSunAndRain_btn = new JButton("Уменьшить солнце и дождь");

        // Создаем панель для кнопок и текстового поля
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new FlowLayout());
        controlsPanel.add(button0);
        controlsPanel.add(textField1);
        controlsPanel.add(addSunAndRain_btn);
        controlsPanel.add(reduceSunAndRain_btn);

        // Создаем панель острова
        islandPanel = new IslandPanel(Main.island);
        mainPanel.add(islandPanel, BorderLayout.CENTER);  // Остров в центре

        // Инициализация панели для списка животных
        listModel = new DefaultListModel<>();
        animalList = new JList<>(listModel); // Модель для динамического обновления списка
        JScrollPane scrollPane = new JScrollPane(animalList); // Добавляем скроллбар
        scrollPane.setPreferredSize(new Dimension(200, height)); // Размер панели списка

        // Панель для списка животных справа от острова
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(listPanel, BorderLayout.EAST); // Панель списка справа

        // Добавляем элементы на основную панель
        mainPanel.add(controlsPanel, BorderLayout.NORTH); // Кнопки сверху

        // Настройка и отображение окна
        setTitle("Остров");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();  // Устанавливаем размер окна в соответствии с предпочтительными размерами панелей
        setSize(width, height);
        setVisible(true);

        // Запуск перерисовки острова каждые 50 мс (0.05 сек)
        new Timer(50, e -> islandPanel.repaint()).start();

        // Запуск обновления списка животных каждые 2 секунды
        new Timer(100, e -> updateAnimalList()).start();

        // Обработчик кнопки запуска/остановки острова
        button0.addActionListener(e -> {
            this.isIslandInLife.set(!this.isIslandInLife.get()); // Изменяем значение AtomicBoolean

            if (button0.getText().equalsIgnoreCase("Остановить остров")) {
                button0.setText("Запустить остров");
                JOptionPane.showMessageDialog(this, "Остров остановлен");
            } else {
                button0.setText("Остановить остров");
                JOptionPane.showMessageDialog(this, "Остров запущен");
            }
        });

        // Обработчик добавления солнца и дождя
        addSunAndRain_btn.addActionListener(e -> {
            GlobalWeather.addSunAndRain();
            JOptionPane.showMessageDialog(this, "Погода на: " + GlobalWeather.getSunAndRain());
        });

        // Обработчик уменьшения солнца и дождя
        reduceSunAndRain_btn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GlobalWeather.reduceSunAndRain();
                JOptionPane.showMessageDialog(MainWindow.this, "Погода на: " + GlobalWeather.getSunAndRain());
            }
        });
    }

    // Метод для обновления списка животных
    private void updateAnimalList() {
        if (Main.island == null) return;

        Map<String, Integer> animalCounts = getAnimalCounts(); // Получаем количество животных по типам
        listModel.clear(); // Очищаем список перед обновлением

        // Разделяем на хищников и травоядных
        listModel.addElement("Хищники:");
        for (Map.Entry<String, Integer> entry : animalCounts.entrySet()) {
            String animalName = entry.getKey();
            Integer count = entry.getValue();

            // Проверяем, если животное хищник
            if (isPredator(animalName)) {
                listModel.addElement(animalName + ": " + count); // Добавляем хищников в список
            }
        }

        listModel.addElement(" ");
        listModel.addElement("Травоядные:");
        for (Map.Entry<String, Integer> entry : animalCounts.entrySet()) {
            String animalName = entry.getKey();
            Integer count = entry.getValue();

            // Проверяем, если животное травоядное
            if (!isPredator(animalName)) {
                listModel.addElement(animalName + ": " + count); // Добавляем травоядных в список
            }
        }

        // Добавляем информацию о количестве травы на острове
        int totalPlants = getTotalPlants();
        listModel.addElement(" ");
        listModel.addElement("Растения: " + totalPlants);
    }

    // Метод для подсчета всех животных на острове
    private Map<String, Integer> getAnimalCounts() {
        Map<String, Integer> totalCounts = new HashMap<>();

        for (int i = 0; i < Main.island.getIslandWidth(); i++) {
            for (int j = 0; j < Main.island.getIslandHeight(); j++) {
                Location location = Main.island.getLocation(i, j);
                Map<String, Integer> locationAnimalCounts = location.getAmountPerAnimalType();
                for (Map.Entry<String, Integer> entry : locationAnimalCounts.entrySet()) {
                    String animalName = entry.getKey();
                    Integer count = entry.getValue();
                    totalCounts.put(animalName, totalCounts.getOrDefault(animalName, 0) + count); // Суммируем количество животных
                }
            }
        }
        return totalCounts;
    }

    // Метод для подсчета общего количества растений на острове
    private int getTotalPlants() {
        int totalPlants = 0;
        for (int i = 0; i < Main.island.getIslandWidth(); i++) {
            for (int j = 0; j < Main.island.getIslandHeight(); j++) {
                totalPlants += Main.island.getLocation(i, j).getPlants().size();
            }
        }
        return totalPlants;
    }

    // Вспомогательный метод для проверки, является ли животное хищником
    private boolean isPredator(String animalName) {
        return animalName.equals("Вовк") || animalName.equals("Удав") ||
                animalName.equals("Лисиця") || animalName.equals("Ведмідь") ||
                animalName.equals("Орел");
    }
}
