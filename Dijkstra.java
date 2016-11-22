import java.util.*;

import Jcg.geometry.*;
import Jcg.polyhedron.*;

public class Dijkstra {
	
	public Polyhedron_3<Point_3> polyhedron3D;
	
	public Dijkstra(Polyhedron_3<Point_3> polyhedron3D) {
		this.polyhedron3D=polyhedron3D;
	}

	public ArrayList<Vertex<Point_3>> Voisins(Vertex<Point_3> v){
		Halfedge<Point_3> h = v.getHalfedge();
		
		ArrayList<Vertex<Point_3>> result = new ArrayList<Vertex<Point_3>>();
		
		Halfedge<Point_3> e = h;
		e = e.opposite;
		
		do{
			result.add(e.getVertex());
			e=e.opposite.next;
		} while(e.opposite!=h);
		
		return result;
	}
	
	public ArrayList<Pair<Double,Vertex<Point_3>>> ShortestPaths(Vertex<Point_3> s){
		ArrayList<Pair<Double,Vertex<Point_3>>> A = new ArrayList<>();
		for(Vertex<Point_3> v : this.polyhedron3D.vertices){
			A.add(new Pair<>(-1.,v));
		}
		PriorityQueue<Triple<Vertex<Point_3>,Double,Vertex<Point_3>>> Q= new PriorityQueue<Triple<Vertex<Point_3>,Double,Vertex<Point_3>>>(new TripleComparator());
		
		Triple<Vertex<Point_3>,Double,Vertex<Point_3>> p = new Triple<>(s,0.,s);
		Q.add(p);
		
		while(!Q.isEmpty()){
			Triple<Vertex<Point_3>,Double,Vertex<Point_3>> t1 = Q.poll();
			Vertex<Point_3> v = t1.first();
			
			int i = v.index;

			if(A.get(i).first()<0) {
				A.get(i).UpdateA(t1.second());
				A.get(i).UpdateB(t1.third());
			}
			else continue;
			
			for(Vertex<Point_3> u : Voisins(v)){
				double d = (double) (v.getPoint()).distanceFrom(u.getPoint());
				Triple<Vertex<Point_3>,Double,Vertex<Point_3>> t2 = new Triple<>(u,d+t1.second(),v);
				Q.add(t2);
			}
		}
		
		return A;
	}
	
	
}
