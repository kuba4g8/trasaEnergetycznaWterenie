package GUI;

import Logika.Dijkstra;
import Logika.aStar;
import Logika.MetodaPotencjalow;
import StrukturyDanych.Grid;
import StrukturyDanych.Node;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PathfindingGUI extends JFrame {
    private Grid grid;
    private Node start;
    private Node end;
    private TerrainVisualizer visualizer;
    private JLabel statusLabel;
    private JLabel costLabel;

    public PathfindingGUI(Grid grid, Node start, Node end) {
        this.grid = grid;
        this.start = start;
        this.end = end;

        setupUI();
    }

    private void setupUI() {
        setTitle("Wizualizacja Algorytmów Wyznaczania Tras");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel wizualizacji
        visualizer = new TerrainVisualizer(grid);
        visualizer.setStartAndEnd(start, end);

        JScrollPane scrollPane = new JScrollPane(visualizer);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Mapa Wysokości"));
        add(scrollPane, BorderLayout.CENTER);

        // Panel kontrolny
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        // Panel informacyjny
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Wybierz Algorytm"));

        JButton dijkstraButton = new JButton("Dijkstra");
        dijkstraButton.setBackground(new Color(255, 99, 71));
        dijkstraButton.setForeground(Color.WHITE);
        dijkstraButton.setFocusPainted(false);
        dijkstraButton.setFont(new Font("Arial", Font.BOLD, 14));
        dijkstraButton.addActionListener(e -> runDijkstra());

        JButton aStarButton = new JButton("A*");
        aStarButton.setBackground(new Color(30, 144, 255));
        aStarButton.setForeground(Color.WHITE);
        aStarButton.setFocusPainted(false);
        aStarButton.setFont(new Font("Arial", Font.BOLD, 14));
        aStarButton.addActionListener(e -> runAStar());

        JButton potentialButton = new JButton("Metoda Potencjałów");
        potentialButton.setBackground(new Color(255, 165, 0));
        potentialButton.setForeground(Color.WHITE);
        potentialButton.setFocusPainted(false);
        potentialButton.setFont(new Font("Arial", Font.BOLD, 14));
        potentialButton.addActionListener(e -> runPotentialMethod());

        JButton clearButton = new JButton("Wyczyść");
        clearButton.setBackground(new Color(128, 128, 128));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.addActionListener(e -> clearPath());

        panel.add(dijkstraButton);
        panel.add(aStarButton);
        panel.add(potentialButton);
        panel.add(clearButton);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        JLabel titleLabel = new JLabel("Wizualizacja Tras na Mapie Wysokości");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statusLabel = new JLabel("Wybierz algorytm do wizualizacji trasy");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        costLabel = new JLabel("");
        costLabel.setFont(new Font("Arial", Font.BOLD, 14));
        costLabel.setHorizontalAlignment(SwingConstants.CENTER);
        costLabel.setForeground(new Color(0, 128, 0));

        textPanel.add(titleLabel);
        textPanel.add(statusLabel);
        textPanel.add(costLabel);

        panel.add(textPanel, BorderLayout.CENTER);

        // Legenda
        JPanel legendPanel = createLegendPanel();
        panel.add(legendPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Legenda"));

        panel.add(createLegendItem(Color.GREEN, "Start (S)"));
        panel.add(createLegendItem(Color.BLUE, "Cel (C)"));
        panel.add(createLegendItem(new Color(34, 139, 34), "Niziny (0-20m)"));
        panel.add(createLegendItem(new Color(144, 238, 144), "Wzniesienia (20-50m)"));
        panel.add(createLegendItem(new Color(210, 180, 140), "Pagórki (50-100m)"));
        panel.add(createLegendItem(new Color(205, 133, 63), "Góry (>100m)"));
        panel.add(createLegendItem(new Color(178, 34, 34), "Wysokie góry (>1000m)"));
        panel.add(createLegendItem(new Color(139, 0, 0), "Mury (>5000m)"));

        return panel;
    }

    private JPanel createLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 11));

        item.add(colorBox);
        item.add(label);

        return item;
    }

    private void runDijkstra() {
        statusLabel.setText("Uruchamianie algorytmu Dijkstry...");
        costLabel.setText("");

        SwingWorker<List<Node>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Node> doInBackground() {
                return Dijkstra.znajdzTrase(grid, start, end);
            }

            @Override
            protected void done() {
                try {
                    List<Node> path = get();
                    if (path.isEmpty()) {
                        statusLabel.setText("Dijkstra: Nie znaleziono trasy!");
                        costLabel.setText("");
                    } else {
                        visualizer.setPath(path, new Color(255, 99, 71));
                        statusLabel.setText("Dijkstra: Długość: " + path.size() + " punktów");
                        costLabel.setText(String.format("Koszt energetyczny: %.2f", end.gScore));
                    }
                } catch (Exception e) {
                    statusLabel.setText("Błąd: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void runAStar() {
        statusLabel.setText("Uruchamianie algorytmu A*...");
        costLabel.setText("");

        SwingWorker<List<Node>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Node> doInBackground() {
                return aStar.znajdzTrase(grid, start, end);
            }

            @Override
            protected void done() {
                try {
                    List<Node> path = get();
                    if (path.isEmpty()) {
                        statusLabel.setText("A*: Nie znaleziono trasy!");
                        costLabel.setText("");
                    } else {
                        visualizer.setPath(path, new Color(30, 144, 255));
                        statusLabel.setText("A*: Długość: " + path.size() + " punktów");
                        costLabel.setText(String.format("Koszt energetyczny: %.2f", end.gScore));
                    }
                } catch (Exception e) {
                    statusLabel.setText("Błąd: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void runPotentialMethod() {
        statusLabel.setText("Uruchamianie metody potencjałów...");
        costLabel.setText("");

        SwingWorker<List<Node>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Node> doInBackground() {
                return MetodaPotencjalow.znajdzTrase(grid, start, end);
            }

            @Override
            protected void done() {
                try {
                    List<Node> path = get();
                    if (path.isEmpty() || path.get(path.size() - 1) != end) {
                        statusLabel.setText("Metoda Potencjałów: Nie dotarła do celu!");
                        costLabel.setText("Algorytm utknął w lokalnym minimum");
                        if (!path.isEmpty()) {
                            visualizer.setPath(path, new Color(255, 165, 0));
                        }
                    } else {
                        visualizer.setPath(path, new Color(255, 165, 0));
                        statusLabel.setText("Metoda Potencjałów: Długość: " + path.size() + " punktów");
                        costLabel.setText(String.format("Koszt energetyczny: %.2f", end.gScore));
                    }
                } catch (Exception e) {
                    statusLabel.setText("Błąd: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void clearPath() {
        visualizer.setPath(null, Color.RED);
        statusLabel.setText("Wybierz algorytm do wizualizacji trasy");
        costLabel.setText("");
    }
}
