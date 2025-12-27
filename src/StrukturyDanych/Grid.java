package StrukturyDanych;

import java.util.*;

public class Grid
{
    private Node[][] punkty;
    private int width;
    private int height;
    
    public Grid(int width, int height, double[][] gridWysokosci)
    {
        this.width = width;
        this.height = height;
        
        punkty = new Node[width][height];
        supplyPunkty(gridWysokosci);
    }
    
    public List<Node> getNeighbors(Node node)
    {
        List<Node> neighbors = new ArrayList<>();
        
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0)
                    continue;
                
                int newX = node.elements.x + dx;
                int newY = node.elements.y + dy;
                
                if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                    neighbors.add(punkty[newX][newY]);
                }
            }
        }
        return neighbors;
    }
    
    public Node getNode(int x, int y)
    {
        return punkty[x][y];
    }
    
    public void resetGrid() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Node n = punkty[i][j];
                n.parent = null;
                n.gScore = Double.POSITIVE_INFINITY;
                n.fScore = Double.POSITIVE_INFINITY;
                n.potential = 0.0;
            }
        }
    }
    
    public double obliczEnergie(Node from, Node to) {
        double dx = Math.abs(from.elements.x - to.elements.x);
        double dy = Math.abs(from.elements.y - to.elements.y);
        
        // Jeśli suma różnic współrzędnych wynosi 2, to idziemy po skosie (np. 1,1)
        // Dystans po skosie to pierwiastek z 2 (ok. 1.41), w linii prostej to 1.0
        double distance = (dx + dy == 2) ? Math.sqrt(2) : 1.0;

        // roznica wysokosci
        double deltaH = to.elements.height - from.elements.height;
        
        double energyCost = 0;
        
        // podejscie pod gore
        if (deltaH > 0)
        {
            energyCost = distance + (deltaH * 10.0);
        }
        // zejscie w dol
        else if (deltaH < 0)
        {
            energyCost = distance + (Math.abs(deltaH) * 1.0);
        }
        // plaski teren
        else
        {
            energyCost = distance;
        }
        
        return energyCost;
    }
    
    private void supplyPunkty(double[][] gridWysokosci)
    {
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                punkty[i][j] = new Node(new Node.NodeElems(i, j, gridWysokosci[j][i]));
            }
        }
    }
    
    public int getWidht()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
}
