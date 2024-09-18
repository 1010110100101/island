package org.start;

import org.model.GlobalWeather;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainWindow extends JFrame {

    private JPanel mainPanel;
    private IslandPanel islandPanel; // Панель для отображения острова

    private JButton button0;
    private JButton addSunAndRain_btn;
    private JButton reduceSunAndRain_btn;
    private JTextField textField1;

    private AtomicBoolean isIslandInLife;

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

        // Добавляем элементы на основную панель
        mainPanel.add(controlsPanel, BorderLayout.NORTH); // Кнопки сверху
        mainPanel.add(islandPanel, BorderLayout.CENTER);  // Остров в центре

        // Настройка и отображение окна
        setTitle("Остров");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();  // Устанавливаем размер окна в соответствии с предпочтительными размерами панелей
        setSize(width, height);
        setVisible(true);






        // Запуск перерисовки острова каждые 1000 мс (1 секунда)
        new Timer(50, e -> islandPanel.repaint()).start();




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
}
