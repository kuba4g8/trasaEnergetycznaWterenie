package Logika;

import StrukturyDanych.Grid;
import StrukturyDanych.Node;
import java.util.*;

public class MetodaPotencjalow {
    
    public static List<Node> znajdzTrase(Grid terrainMap, Node startPoint, Node targetPoint) {
        terrainMap.resetGrid();
        obliczPolePotencjalow(terrainMap, targetPoint);
        
        List<Node> path = new ArrayList<>();
        Set<Node> visited = new HashSet<>(); // Zapobiega kręceniu się w kółko
        
        Node current = startPoint;
        current.gScore = 0; // Inicjalizacja kosztu startu
        path.add(current);
        visited.add(current);

        int maxSteps = 200; // Zwiększamy limit kroków dla większych map
        while (current != targetPoint && maxSteps > 0) {
            List<Node> neighbors = terrainMap.getNeighbors(current, terrainMap.czyNaUkos);
            Node bestNeighbor = null;
            double minPotential = Double.MAX_VALUE;

            for (Node neighbor : neighbors) {
                // Wybieramy sąsiada z najniższym potencjałem, którego jeszcze nie odwiedziliśmy
                if (!visited.contains(neighbor) && neighbor.potential < minPotential) {
                    minPotential = neighbor.potential;
                    bestNeighbor = neighbor;
                }
            }

            // Jeśli nie ma gdzie pójść (wszystko odwiedzone lub brak sąsiadów)
            if (bestNeighbor == null) {
                System.out.println("Metoda Potencjałów: Całkowita blokada (brak nieodwiedzonych sąsiadów)!");
                break;
            }

            // Obliczamy rzeczywisty koszt energetyczny przejścia
            double kosztPrzejscia = terrainMap.obliczEnergie(current, bestNeighbor);
            bestNeighbor.gScore = current.gScore + kosztPrzejscia;
            bestNeighbor.parent = current;
            
            current = bestNeighbor;
            path.add(current);
            visited.add(current);
            maxSteps--;
        }
        
        return path;
    }
    
    private static void obliczPolePotencjalow(Grid grid, Node target) {
        // ZMNIEJSZONA WAGA: wysokość nie może być ważniejsza niż dotarcie do celu
        double terrainWeight = 0.2;
        
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Node n = grid.getNode(x, y);
                double attraction = calculateDistance(n, target);
                double repulsion = n.elements.height * terrainWeight;
                n.potential = attraction + repulsion;
            }
        }
    }
    
    private static double calculateDistance(Node a, Node b) {
        return Math.sqrt(Math.pow(a.elements.x - b.elements.x, 2) +
                Math.pow(a.elements.y - b.elements.y, 2));
    }
}