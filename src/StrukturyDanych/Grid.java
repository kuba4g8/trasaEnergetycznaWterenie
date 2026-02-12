package StrukturyDanych;

import java.util.*;

public class Grid
{
    private Node[][] punkty;
    public boolean czyNaUkos;
    private int width;
    private int height;
    
    public static final double MASS = 80.0;       // Masa obiektu w kg
    public static final double GRAVITY = 9.81;    // Przyspieszenie ziemskie m/s^2
    public static final double FRICTION_COEFF = 0.1; // Współczynnik oporów ruchu
    
    public double wspolczynnikWysokosci = 1.0;
    
    public Grid(int width, int height, double[][] gridWysokosci, boolean czyNaUkos)
    {
        this.width = width;
        this.height = height;
        this.czyNaUkos = czyNaUkos;
        
        punkty = new Node[width][height];
        supplyPunkty(gridWysokosci);
    }
    
    public List<Node> getNeighbors(Node node, boolean czyNaUkos)
    {
        List<Node> neighbors = new ArrayList<>();
        
        for (int dx = -1; dx <= 1; dx++)
        {
            for (int dy = -1; dy <= 1; dy++)
            {
                
                if (dx == 0 && dy == 0)
                    continue;
                
                if (!czyNaUkos && dx != 0 && dy != 0)
                    continue;
                
                int newX = node.elements.x + dx;
                int newY = node.elements.y + dy;
                
                if (newX >= 0 && newX < width && newY >= 0 && newY < height)
                {
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
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                Node n = punkty[i][j];
                n.parent = null;
                n.gScore = Double.POSITIVE_INFINITY;
                n.fScore = Double.POSITIVE_INFINITY;
                n.potential = 0.0;
            }
        }
    }
    
    public double obliczEnergie(Node from, Node to) {
        // 1. Obliczenie odległości w poziomie (dystans euklidesowy 2D)
        double dx = Math.abs(from.elements.x - to.elements.x);
        double dy = Math.abs(from.elements.y - to.elements.y);
        double distance2D = (dx + dy == 2) ? Math.sqrt(2) : 1.0;
        
        // 2. Różnica wysokości (h_koncowe - h_poczatkowe)
        double heightDiff = to.elements.height - from.elements.height;
        
        // 3. Model Fizyczny
        
        // A. Praca wykonana przeciwko siłom tarcia/oporu na danym dystansie
        double workFriction = MASS * GRAVITY * FRICTION_COEFF * distance2D;
        
        // B. Zmiana Energii Potencjalnej (Praca przeciwko grawitacji)
        double deltaPotentialEnergy = (MASS * GRAVITY * heightDiff) * wspolczynnikWysokosci;
        
        double totalEnergyCost = 0.0;
        
        // PODEJŚCIE POD GÓRĘ:
        if (deltaPotentialEnergy > 0)
        {
            // Musimy pokonać opory ruchu ORAZ zwiększyć energię potencjalną.
            totalEnergyCost = workFriction + deltaPotentialEnergy;
        }
        else
        {
            // ZEJŚCIE W DÓŁ / PŁASKO:
            // Grawitacja "pomaga", ale nadal musimy wykonać pracę, żeby nie spaść (hamowanie)
            // lub po prostu pokonać opory ruchu.
            // Przyjmijmy, że koszt to opory ruchu + niewielki koszt hamowania (np. 10% odzyskanej energii tracimy na hamowanie)
            
            
            // jak sie go ustawi np na 0.5 to algorytm nie bedzie schodzil po duzych skarpach
            float descentCost = 0.1f;
            
            totalEnergyCost = workFriction + (Math.abs(deltaPotentialEnergy) * descentCost);
            
        }
        
        return totalEnergyCost;
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
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
}
