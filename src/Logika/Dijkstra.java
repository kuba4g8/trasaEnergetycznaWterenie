package Logika;

import StrukturyDanych.Grid;
import StrukturyDanych.Node;
import java.util.*;

public class Dijkstra
{
    public static List<Node> znajdzTrase(Grid grid, Node start, Node cel)
    {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> openSetLookup = new HashSet<>();
        Set<Node> closedSet = new HashSet<>();
        
        grid.resetGrid();
        
        start.gScore = 0;
        start.fScore = 0;
        openSet.add(start);
        openSetLookup.add(start);
        
        while (!openSet.isEmpty())
        {
            Node current = openSet.poll();
            openSetLookup.remove(current);
            
            if (current == cel)
            {
                return reconstructPath(cel);
            }
            
            closedSet.add(current);
            
            List<Node> neigboursOfCurrent = grid.getNeighbors(current, grid.czyNaUkos);
            
            for (Node neighbor : neigboursOfCurrent)
            {
                if (closedSet.contains(neighbor))
                {
                    continue;
                }
                
                double kosztEnergii = grid.obliczEnergie(current, neighbor);
                double finalnyGScore = current.gScore + kosztEnergii;
                
                if (finalnyGScore < neighbor.gScore)
                {
                    neighbor.parent = current;
                    neighbor.gScore = finalnyGScore;
                    neighbor.fScore = finalnyGScore;
                    
                    if (!openSetLookup.contains(neighbor))
                    {
                        openSet.add(neighbor);
                        openSetLookup.add(neighbor);
                    }
                    else
                    {
                        openSet.remove(neighbor);
                        openSet.add(neighbor);
                    }
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    private static List<Node> reconstructPath(Node target)
    {
        List<Node> path = new ArrayList<>();
        Node curr = target;
        while (curr != null)
        {
            path.add(curr);
            curr = curr.parent;
        }
        Collections.reverse(path);
        return path;
    }
}