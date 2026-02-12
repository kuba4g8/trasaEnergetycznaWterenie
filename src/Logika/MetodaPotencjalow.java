package Logika;

import StrukturyDanych.Grid;
import StrukturyDanych.Node;
import java.util.*;

public class MetodaPotencjalow extends ZlozonoscObliczeniowa
{
    
    
    public static List<Node> znajdzTrase(Grid terrainMap, Node startPoint, Node targetPoint)
    {
        resetLicznika();
        terrainMap.resetGrid();
        obliczPolePotencjalow(terrainMap, targetPoint);
        
        Stack<Node> pathStack = new Stack<>();
        Set<Node> visited = new HashSet<>();
        
        Node current = startPoint;
        current.gScore = 0;
        
        pathStack.push(current);
        visited.add(current);
        
        int maxSteps = terrainMap.getWidth() * terrainMap.getHeight() * 10;
        
        while (!pathStack.isEmpty() && maxSteps > 0) {
            current = pathStack.peek();
            liczbaOdwiedzonych++;
            
            if (current.equals(targetPoint)) {
                return new ArrayList<>(pathStack);
            }
            
            List<Node> neighbors = terrainMap.getNeighbors(current, terrainMap.czyNaUkos);
            Node bestNeighbor = null;
            double minPotential = Double.MAX_VALUE;
            
            for (Node neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    if (neighbor.potential < minPotential) {
                        minPotential = neighbor.potential;
                        bestNeighbor = neighbor;
                    }
                }
            }
            
            if (bestNeighbor != null) {
                // Idziemy naprzód
                double kosztPrzejscia = terrainMap.obliczEnergie(current, bestNeighbor);
                bestNeighbor.gScore = current.gScore + kosztPrzejscia;
                bestNeighbor.parent = current; // Rodzic dla spójności
                
                visited.add(bestNeighbor);
                pathStack.push(bestNeighbor);
            } else {
                pathStack.pop();
            }
            
            maxSteps--;
        }
        
        System.out.println("Metoda Potencjałów: Nie znaleziono trasy (stos pusty lub limit kroków).");
        return new ArrayList<>();
    }
    
    private static void obliczPolePotencjalow(Grid grid, Node target)
    {
        double terrainWeight = grid.wspolczynnikWysokosci;
        
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Node n = grid.getNode(x, y);
                
                double attraction = calculateDistance(n, target);
                
                double height = n.elements.height;
                double effectiveHeight = Math.max(0.0, height);
                
                double repulsion = effectiveHeight * terrainWeight;

                n.potential = attraction + repulsion;
            }
        }
    }
    
    private static double calculateDistance(Node a, Node b) {
        return Math.sqrt(Math.pow(a.elements.x - b.elements.x, 2) +
                Math.pow(a.elements.y - b.elements.y, 2));
    }
}