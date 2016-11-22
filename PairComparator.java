import java.util.Comparator;

import Jcg.geometry.Point_3;
import Jcg.polyhedron.Vertex;

public class PairComparator implements Comparator<Pair<Vertex<Point_3>, Double> >{
	public int compare(Pair<Vertex<Point_3>, Double> p1, Pair<Vertex<Point_3>, Double> p2){
		if(p1.second()<p2.second()){
			return -1;
		}
		return 1;
	}
}