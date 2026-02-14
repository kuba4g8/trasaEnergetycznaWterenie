package GUI;

import StrukturyDanych.Grid;
import StrukturyDanych.Node;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;

public class TerrainVisualizer extends JPanel {
    private Grid grid;
    private List<Node> path;
    private Node start;
    private Node end;
    private Color pathColor;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private static final int INITIAL_VIEW_SIZE = 800;
    private BiConsumer<Node, Node> onPointsChanged;

    public TerrainVisualizer(Grid grid) {
        this.grid = grid;
        this.pathColor = Color.RED;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(INITIAL_VIEW_SIZE, INITIAL_VIEW_SIZE));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    private void handleMouseClick(MouseEvent e) {
        if (grid == null) return;

        updateScales();
        int x = (int) (e.getX() / scaleX);
        int y = (int) (e.getY() / scaleY);

        // Zabezpieczenie przed wyjściem poza granice mapy
        if (x >= 0 && x < grid.getWidth() && y >= 0 && y < grid.getHeight()) {
            Node clickedNode = grid.getNode(x, y);
            if (SwingUtilities.isLeftMouseButton(e)) {
                start = clickedNode;
            } else if (SwingUtilities.isRightMouseButton(e)) {
                end = clickedNode;
            }

            if (onPointsChanged != null) {
                onPointsChanged.accept(start, end);
            }
            repaint();
        }
    }

    public void setOnPointsChanged(BiConsumer<Node, Node> callback) {
        this.onPointsChanged = callback;
    }

    private void updateScales() {
        if (grid == null) return;
        scaleX = (double) getWidth() / grid.getWidth();
        scaleY = (double) getHeight() / grid.getHeight();
    }

    public void updateGrid(Grid grid) {
        this.grid = grid;
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
        if (grid == null) return;
        
        updateScales();
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

        // Rysowanie siatki (tylko jeśli komórki są wystarczająco duże)
        if (scaleX > 5 && scaleY > 5) {
            drawGrid(g2d);
        }
    }

    private void drawTerrain(Graphics2D g2d) {
        double maxHeight = findMaxHeight();
        int w = getWidth();
        int h = getHeight();

        // Jeśli mapa jest bardzo duża, rysujemy ją piksel po pikselu na BufferedImage dla wydajności
        if (grid.getWidth() > w || grid.getHeight() > h) {
            drawOptimizedTerrain(g2d, maxHeight);
            return;
        }

        for (int x = 0; x < grid.getWidth(); x++) {
            int xPos = (int) (x * scaleX);
            int nextXPos = (int) ((x + 1) * scaleX);
            int cellW = Math.max(1, nextXPos - xPos);

            for (int y = 0; y < grid.getHeight(); y++) {
                Node node = grid.getNode(x, y);
                double height = node.elements.height;

                int yPos = (int) (y * scaleY);
                int nextYPos = (int) ((y + 1) * scaleY);
                int cellH = Math.max(1, nextYPos - yPos);

                Color color = getHeightColor(height, maxHeight);
                g2d.setColor(color);
                g2d.fillRect(xPos, yPos, cellW, cellH);

                if (cellW >= 20 && cellH >= 20) {
                    drawHeightText(g2d, xPos, yPos, cellW, cellH, height);
                }
            }
        }
    }

    private void drawOptimizedTerrain(Graphics2D g2d, double maxHeight) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
        
        for (int screenX = 0; screenX < w; screenX++) {
            int gridX = (int) (screenX / scaleX);
            gridX = Math.min(gridX, grid.getWidth() - 1);
            for (int screenY = 0; screenY < h; screenY++) {
                int gridY = (int) (screenY / scaleY);
                gridY = Math.min(gridY, grid.getHeight() - 1);
                
                double height = grid.getNode(gridX, gridY).elements.height;
                img.setRGB(screenX, screenY, getHeightColor(height, maxHeight).getRGB());
            }
        }
        g2d.drawImage(img, 0, 0, null);
    }

    private void drawHeightText(Graphics2D g2d, int x, int y, int cellW, int cellH, double height) {
        g2d.setColor(Color.BLACK);
        int fontSize = Math.min(cellW, cellH) / 5 + 2;
        g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));
        String heightText = String.format("%.0f", height);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(heightText);
        int textHeight = fm.getAscent();
        g2d.drawString(heightText, 
            x + (cellW - textWidth) / 2,
            y + (cellH + textHeight) / 2 - 2);
    }

    private void drawPath(Graphics2D g2d) {
        g2d.setColor(pathColor);
        g2d.setStroke(new BasicStroke(Math.max(1, (float)(Math.min(scaleX, scaleY) / 3))));

        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node next = path.get(i + 1);

            int x1 = (int) (current.elements.x * scaleX + scaleX / 2);
            int y1 = (int) (current.elements.y * scaleY + scaleY / 2);
            int x2 = (int) (next.elements.x * scaleX + scaleX / 2);
            int y2 = (int) (next.elements.y * scaleY + scaleY / 2);

            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawPoint(Graphics2D g2d, Node node, Color color, String label) {
        int x = (int) (node.elements.x * scaleX + scaleX / 2);
        int y = (int) (node.elements.y * scaleY + scaleY / 2);

        int pointSize = (int) Math.max(4, Math.min(scaleX, scaleY) / 2);
        if (pointSize < 8 && (label.equals("S") || label.equals("C"))) pointSize = 8;
        
        g2d.setColor(color);
        g2d.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);

        if (scaleX >= 15 && scaleY >= 15) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, (int) Math.max(10, Math.min(scaleX, scaleY) / 3)));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getAscent();
            g2d.drawString(label, x - textWidth / 2, y + textHeight / 2 - 2);
        }
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.setStroke(new BasicStroke(1));

        for (int x = 0; x <= grid.getWidth(); x++) {
            int xPos = (int) (x * scaleX);
            g2d.drawLine(xPos, 0, xPos, getHeight());
        }

        for (int y = 0; y <= grid.getHeight(); y++) {
            int yPos = (int) (y * scaleY);
            g2d.drawLine(0, yPos, getWidth(), yPos);
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
