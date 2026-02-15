package GUI;

import Testy.AnalizaWrazliwosci;
import Testy.AnalizaWrazliwosci.WynikAnalizy;
import StrukturyDanych.Grid;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AnalizaWrazliwosciGUI extends JFrame {
    private Grid grid;
    private double[][] terrainData;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> algorithmCombo;
    private JTextField iterationsField;
    private JButton startButton;
    private JLabel statusLabel;

    public AnalizaWrazliwosciGUI(Grid grid, double[][] terrainData) {
        this.grid = grid;
        this.terrainData = terrainData;
        
        setTitle("Analiza Wrażliwości");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setupUI();
    }

    private void setupUI() {
        // Panel sterowania
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        controlPanel.add(new JLabel("Algorytm:"));
        algorithmCombo = new JComboBox<>(new String[]{"Dijkstra", "A*", "Metoda Potencjałów"});
        controlPanel.add(algorithmCombo);

        controlPanel.add(new JLabel("Powtórzenia:"));
        iterationsField = new JTextField("10", 5);
        controlPanel.add(iterationsField);

        startButton = new JButton("Uruchom Testy");
        startButton.addActionListener(e -> runAnalysis());
        controlPanel.add(startButton);

        add(controlPanel, BorderLayout.NORTH);

        // Tabela wyników
        String[] columnNames = {"Alpha", "Długość", "Koszt Energii", "Śr. Czas (ms)", "Odchylenie (ms)", "Odwiedzone (N)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // Status
        statusLabel = new JLabel("Gotowy");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void runAnalysis() {
        int algorithmIndex = algorithmCombo.getSelectedIndex();
        int iterations;
        try {
            iterations = Integer.parseInt(iterationsField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nieprawidłowa liczba powtórzeń", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        startButton.setEnabled(false);
        statusLabel.setText("Trwa analiza... Proszę czekać.");
        tableModel.setRowCount(0);

        SwingWorker<List<WynikAnalizy>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<WynikAnalizy> doInBackground() {
                return AnalizaWrazliwosci.wykonajAnalize(terrainData, algorithmIndex, iterations);
            }

            @Override
            protected void done() {
                try {
                    List<WynikAnalizy> wyniki = get();
                    for (WynikAnalizy w : wyniki) {
                        tableModel.addRow(new Object[]{
                            w.alpha, w.dlugosc, String.format("%.2f", w.kosztEnergii),
                            String.format("%.3f", w.sredniCzasMs), String.format("%.3f", w.odchylenieMs),
                            w.odwiedzoneWezly
                        });
                    }
                    statusLabel.setText("Analiza zakończona dla algorytmu: " + AnalizaWrazliwosci.pobierzNazweAlgorytmu(algorithmIndex));
                } catch (Exception e) {
                    statusLabel.setText("Błąd podczas analizy: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    startButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
