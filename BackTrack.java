import java.util.*;

import Jcg.geometry.*;
import Jcg.polyhedron.*;
import Jama.Matrix;

public class BackTrack {
	
	ArrayList<TreeSet<Window>> T;
	public Polyhedron_3<Point_3> polyhedron3D;
	Vertex<Point_3> source;
	
	static double Norme(Halfedge<Point_3> h){
		Point_3 p1 = h.vertex.getPoint();
		Point_3 p2 = h.opposite.vertex.getPoint();
		
		return (Double) p1.distanceFrom(p2);
	}
	
	private double[] FindIntersection(Point_2 s, Point_2 p0, Point_2 p1, Point_2 p2){
		double sp0 = Math.sqrt(s.minus(p0).squaredLength().doubleValue());
		double p1p2 = Math.sqrt(p2.minus(p1).squaredLength().doubleValue());
		double infinity = 1000000000;
		double[][] arraymat = {{(p1.x-p2.x)/p1p2,(p0.x-s.x)/sp0},{(p1.y-p2.y)/p1p2,(p0.y-s.y)/sp0}};
		double[][] arrayb = {{p1.x - s.x},{p1.y - s.y}};
		
		Matrix A = new Matrix(arraymat);
		Matrix b = new Matrix(arrayb);
		if (A.det() == 0) {
			System.out.println("droites parallèles : on met le point d'intersection à +l'infini = 1000000000 à une distance infinie");
			return new double[]{infinity,infinity};
		}
		A = A.inverse();
		Matrix sol = A.times(b);
		
		return new double[]{sol.get(0, 0), sol.get(1, 0)};
	}
		
	public void traceRay(double x, Halfedge<Point_3> h){
		if ((x == 0 && h.opposite.vertex == source) || (x == Norme(h) && h.vertex == source)){
			return;
		}
		else return;
	}
	
	

}
