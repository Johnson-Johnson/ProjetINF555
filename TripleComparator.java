import java.util.Comparator;

import Jcg.geometry.Point_3;
import Jcg.polyhedron.Vertex;

public class TripleComparator implements Comparator<Triple<Vertex<Point_3>, Double, Vertex<Point_3>> >{
	public int compare(Triple<Vertex<Point_3>, Double, Vertex<Point_3>> t1, Triple<Vertex<Point_3>, Double, Vertex<Point_3>> t2){
		if(t1.second()<t2.second()){
			return -1;
		}
		return 1;
	}
}