package Logika;

import StrukturyDanych.Grid;
import StrukturyDanych.Node;
import java.util.*;

public class aStar {
    
    public static List<Node> znajdzTrase(Grid terrainMap, Node startPoint, Node targetPoint) {
        PriorityQueue<Node> nodesToExplore = new PriorityQueue<>();
        Set<Node> openSet = new HashSet<>();
        Set<Node> closedSet = new HashSet<>();
        
        terrainMap.resetGrid();
        
        startPoint.gScore = 0;
        startPoint.fScore = calculateHeuristic(startPoint, targetPoint);
        nodesToExplore.add(startPoint);
        openSet.add(startPoint);
        
        while (!nodesToExplore.isEmpty())
        {
            Node current = nodesToExplore.poll();
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
                    neighbor.fScore = neighbor.gScore + calculateHeuristic(neighbor, targetPoint);
                    
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
    
    private static double calculateHeuristic(Node current, Node target)
    {
        double dx = current.elements.x - target.elements.x;
        double dy = current.elements.y - target.elements.y;
        // Standardowa odległość w linii prostej
        return Math.sqrt(dx * dx + dy * dy);
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