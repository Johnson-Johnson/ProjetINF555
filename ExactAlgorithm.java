import java.util.*;

import Jcg.geometry.*;
import Jcg.polyhedron.*;

public class ExactAlgorithm {
	
	PriorityQueue<Window> Q;
	
	public Polyhedron_3<Point_3> polyhedron3D;
	
	public ExactAlgorithm(Polyhedron_3<Point_3> polyhedron3D) {
		this.polyhedron3D=polyhedron3D;
		this.Q = new PriorityQueue<Window>(new WindowComparator());
	}
	
	private void Propagation(Window w){
		
	}
}
