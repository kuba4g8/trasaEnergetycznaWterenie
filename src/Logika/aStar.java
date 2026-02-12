package Logika;

import StrukturyDanych.Grid;
import StrukturyDanych.Node;
import java.util.*;

public class aStar extends ZlozonoscObliczeniowa
{
    
    public static List<Node> znajdzTrase(Grid terrainMap, Node startPoint, Node targetPoint)
    {
        resetLicznika();
        PriorityQueue<Node> nodesToExplore = new PriorityQueue<>();
        Set<Node> openSet = new HashSet<>();
        Set<Node> closedSet = new HashSet<>();
        
        terrainMap.resetGrid();
        
        startPoint.gScore = 0;
        startPoint.fScore = calculateHeuristic(startPoint, targetPoint, terrainMap.wspolczynnikWysokosci);
        nodesToExplore.add(startPoint);
        openSet.add(startPoint);
        
        while (!nodesToExplore.isEmpty())
        {
            Node current = nodesToExplore.poll();
            liczbaOdwiedzonych++;
            openSet.remove(current);
            
            if (current == targetPoint)
            {
                return buildFinalPath(targetPoint);
            }
            
            closedSet.add(current);
            
            for (Node neighbor : terrainMap.getNeighbors(current, terrainMap.czyNaUkos))
            {
                if (closedSet.contains(neighbor))
                {
                    continue;
                }
                
                double costToNeighbor = terrainMap.obliczEnergie(current, neighbor);
                double tentativeGScore = current.gScore + costToNeighbor;
                
                if (tentativeGScore < neighbor.gScore)
                {
                    neighbor.parent = current;
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = neighbor.gScore + calculateHeuristic(neighbor, targetPoint, terrainMap.wspolczynnikWysokosci);
                    
                    if (!openSet.contains(neighbor))
                    {
                        nodesToExplore.add(neighbor);
                        openSet.add(neighbor);
                    }
                    else
                    {
                        nodesToExplore.remove(neighbor);
                        nodesToExplore.add(neighbor);
                    }
                }
            }
        }
        return Collections.emptyList();
    }
    
    private static double calculateHeuristic(Node current, Node target, double wspolczynnikWysokosci)
    {
        // 1. Odległość w linii prostej (najkrótsza możliwa droga)
        double dx = current.elements.x - target.elements.x;
        double dy = current.elements.y - target.elements.y;
        double distanceEuclidean = Math.sqrt(dx * dx + dy * dy);
        
        // 2. Różnica wysokości
        double heightDiff = target.elements.height - current.elements.height;
        
        // 3. Obliczenie minimalnego kosztu fizycznego (HEURYSTYKA)
        
        double minFrictionCost = Grid.MASS * Grid.GRAVITY * Grid.FRICTION_COEFF * distanceEuclidean;
        
        double minElevationCost = 0.0;
        if (heightDiff > 0)
        {
            minElevationCost = (Grid.MASS * Grid.GRAVITY * heightDiff) * wspolczynnikWysokosci;
        }
        
        // Zwracamy sumę. To jest nasze h(n).
        return minFrictionCost + minElevationCost;
    }
    
    private static List<Node> buildFinalPath(Node targetNode)
    {
        List<Node> path = new ArrayList<>();
        Node step = targetNode;
        while (step != null)
        {
            path.add(step);
            step = step.parent;
        }
        Collections.reverse(path);
        return path;
    }
}