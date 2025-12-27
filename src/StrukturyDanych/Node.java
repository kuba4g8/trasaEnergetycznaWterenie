package StrukturyDanych;

public class Node implements Comparable<Node>
{
    public NodeElems elements;
    
    public Node parent;
    
    // wartosci dla algorytmow, obliczenia kosztow itd.
    public double gScore = Double.POSITIVE_INFINITY;
    
    public double fScore = Double.POSITIVE_INFINITY;
    
    public double potential = 0.0;
    public Node(NodeElems elements)
    {
        this.elements = elements;
    }
    
    @Override
    public int compareTo(Node other)
    {
        return Double.compare(this.fScore, other.fScore);
    }
    
    public static class NodeElems
    {
        public int x;
        public int y;
        public double height;
        
        public NodeElems(int x, int y, double height)
        {
            this.x = x;
            this.y = y;
            this.height = height;
        }
    }
}


