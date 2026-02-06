package GUI;

import StrukturyDanych.Grid;
import StrukturyDanych.Node;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TerrainVisualizer extends JPanel {
    private Grid grid;
    private List<Node> path;
    private Node start;
    private Node end;
    private Color pathColor;
    private static final int CELL_SIZE = 50;

    public TerrainVisualizer(Grid grid) {
        this.grid = grid;
        this.pathColor = Color.RED;
        int width = grid.getWidth() * CELL_SIZE;
        int height = grid.getHeight() * CELL_SIZE;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);
    }

    public void updateGrid(Grid grid) {
        this.grid = grid;
        int width = grid.getWidth() * CELL_SIZE;
        int height = grid.getHeight() * CELL_SIZE;
        setPreferredSize(new Dimension(width, height));
        revalidate();
        repaint();
    }

    public void setPath(List<Node> path, Color color) {
        this.path = path;
        this.pathColor = color;
        repaint();
    }

    public void setStartAndEnd(Node start, Node end) {
        this.start = start;
        this.end = end;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Rysowanie mapy wysokości
        drawTerrain(g2d);

        // Rysowanie ścieżki
        if (path != null && !path.isEmpty()) {
            drawPath(g2d);
        }

        // Rysowanie punktów start i koniec
        if (start != null) {
            drawPoint(g2d, start, Color.GREEN, "S");
        }
        if (end != null) {
            drawPoint(g2d, end, Color.BLUE, "C");
        }

        // Rysowanie siatki
        drawGrid(g2d);
    }

    private void drawTerrain(Graphics2D g2d) {
        double maxHeight = findMaxHeight();

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Node node = grid.getNode(x, y);
                double height = node.elements.height;

                // Obliczanie koloru na podstawie wysokości
                Color color = getHeightColor(height, maxHeight);
                g2d.setColor(color);
                g2d.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                // Rysowanie wysokości jako tekst
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String heightText = String.format("%.0f", height);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(heightText);
                int textHeight = fm.getAscent();
                g2d.drawString(heightText, 
                    x * CELL_SIZE + (CELL_SIZE - textWidth) / 2,
                    y * CELL_SIZE + (CELL_SIZE + textHeight) / 2 - 2);
            }
        }
    }

    private void drawPath(Graphics2D g2d) {
        g2d.setColor(pathColor);
        g2d.setStroke(new BasicStroke(3));

        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node next = path.get(i + 1);

            int x1 = current.elements.x * CELL_SIZE + CELL_SIZE / 2;
            int y1 = current.elements.y * CELL_SIZE + CELL_SIZE / 2;
            int x2 = next.elements.x * CELL_SIZE + CELL_SIZE / 2;
            int y2 = next.elements.y * CELL_SIZE + CELL_SIZE / 2;

            g2d.drawLine(x1, y1, x2, y2);
        }

        // Rysowanie punktów na ścieżce
        g2d.setColor(pathColor.darker());
        for (Node node : path) {
            int x = node.elements.x * CELL_SIZE + CELL_SIZE / 2;
            int y = node.elements.y * CELL_SIZE + CELL_SIZE / 2;
            g2d.fillOval(x - 4, y - 4, 8, 8);
        }
    }

    private void drawPoint(Graphics2D g2d, Node node, Color color, String label) {
        int x = node.elements.x * CELL_SIZE + CELL_SIZE / 2;
        int y = node.elements.y * CELL_SIZE + CELL_SIZE / 2;

        g2d.setColor(color);
        g2d.fillOval(x - 10, y - 10, 20, 20);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(label);
        int textHeight = fm.getAscent();
        g2d.drawString(label, x - textWidth / 2, y + textHeight / 2 - 2);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.setStroke(new BasicStroke(1));

        for (int x = 0; x <= grid.getWidth(); x++) {
            g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, grid.getHeight() * CELL_SIZE);
        }

        for (int y = 0; y <= grid.getHeight(); y++) {
            g2d.drawLine(0, y * CELL_SIZE, grid.getWidth() * CELL_SIZE, y * CELL_SIZE);
        }
    }

    private Color getHeightColor(double height, double maxHeight) {
        if (height > 5000) {
            return new Color(139, 0, 0); // Ciemny czerwony - ekstremalna wysokość
        } else if (height > 1000) {
            return new Color(178, 34, 34); // Czerwony - bardzo wysokie
        } else if (height > 100) {
            return new Color(205, 133, 63); // Brązowy - wysokie
        } else if (height > 50) {
            return new Color(210, 180, 140); // Jasny brązowy - pagórki
        } else if (height > 20) {
            return new Color(144, 238, 144); // Jasny zielony - lekkie wzniesienia
        } else if (height > 0) {
            return new Color(34, 139, 34); // Zielony - niziny
        } else {
            return new Color(173, 216, 230); // Jasny niebieski - depresje
        }
    }

    private double findMaxHeight() {
        double max = 0;
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                double height = grid.getNode(x, y).elements.height;
                if (height < 10000 && height > max) { // Ignorujemy mury
                    max = height;
                }
            }
        }
        return max;
    }
}
