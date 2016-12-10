import java.util.*;

import Jcg.geometry.*;
import Jcg.polyhedron.*;
import Jama.Matrix;

public class BackTrack {
	
	ArrayList<TreeSet<Window>> T;
	public Polyhedron_3<Point_3> polyhedron3D;
	Vertex<Point_3> source;
	ArrayList<Point_3> result;
	
	public BackTrack(Polyhedron_3<Point_3> polyhedron3D, Vertex<Point_3> source, ArrayList<TreeSet<Window>> T) {
		this.polyhedron3D=polyhedron3D;
		this.source = source;
		this.T = T;
		result = new ArrayList<Point_3>();
	}
	
	//Précision à 1E-7
	static boolean equal7(double a, double b){
		if(Math.abs(a-b)<0.0000001) return true;
		return false;
	}
	
	static double round7(double a){
		return Math.round(a*10000000.)/10000000. ;
	}
	
	static double Norme(Halfedge<Point_3> h){
		Point_3 p1 = h.vertex.getPoint();
		Point_3 p2 = h.opposite.vertex.getPoint();
		
		return (Double) p1.distanceFrom(p2);
	}
	
	//il faut supposer que la pseudosource est à gauche de la halfedge
	//on aplatit donc le triangle h, h.next, h.prev
	public void traceRay(double x, Halfedge<Point_3> h){
		ExactAlgorithm EA = new ExactAlgorithm();
		int index = h.index;
		
		double l = Norme(h);
		Point_3 p1 = h.opposite.vertex.getPoint();
		Point_3 p2 = h.vertex.getPoint();
		Point_3 p3 = h.next.vertex.getPoint();
		TreeSet<Window> Ti = T.get(index);
		if ((x == 0 && h.opposite.vertex == source) || (x == Norme(h) && h.vertex == source)){
			return;
		}
		else {
			Halfedge<Point_3> h1 = h.next.opposite;
			Halfedge<Point_3> h2 = h.prev.opposite;
			double l1 = Norme(h1);
			double l2 = Norme(h2);
			Rotation R = new Rotation(h.opposite);
			R.TransformTriangle(h.opposite);
			Window xw = new Window (x,x,0.,0.,0.,null,null);
			Ti.tailSet(xw, true);
			
			System.out.println(h.toString());
			/*for (Window wti : Ti){
				System.out.println("Ti has " + wti.to_string());
			}*/
			xw = Ti.pollFirst();
			System.out.println("passing ray " + xw.to_string());
			Point_3 wl = (Point_3)Point_3.linearCombination(new Point_3[]{p1,p2}, new Double[]{(l-xw.Left())/l, xw.Left()/l});
			Point_3 wr = (Point_3)Point_3.linearCombination(new Point_3[]{p1,p2}, new Double[]{(l-xw.Right())/l, xw.Right()/l});
			Point_3 X = (Point_3)Point_3.linearCombination(new Point_3[]{p1,p2}, new Double[]{(l-x)/l, x/l});
			Point_2 s_2 = EA.goodPseudosource(new Point_2(wl.x, wl.y), new Point_2(wr.x, wr.y), new Point_2(p3.x, p3.y), EA.Pseudosource(xw));
			Point_2 X_2 = new Point_2(X.x, X.y);
			Point_2 p1_2 = new Point_2(p1.x, p1.y);
			Point_2 p2_2 = new Point_2(p2.x, p2.y);
			Point_2 p3_2 = new Point_2(p3.x, p3.y);
			double[] xh2 = EA.FindIntersection(s_2, X_2, p1_2, p3_2);
			double[] xh1 = EA.FindIntersection(s_2, X_2, p2_2, p3_2);
			R.TransformTriangleBack(h);
			
			if ((equal7(0, xh2[0])||0<xh2[0]) && (equal7(l2, xh2[0])||xh2[0]<l2)){
				System.out.println("on h2");
				Point_3 intersection = (Point_3)Point_3.linearCombination(new Point_3[]{p1,p3}, new Double[]{(l2-xh2[0])/l2, xh2[0]/l2});
				System.out.println("intersection = "+intersection);
				result.add(intersection);
				traceRay(xh2[0], h2);
			}
			
			else if ((equal7(0, xh1[0])||0<xh1[0]) && (equal7(l1, xh1[0])||xh1[0]<l1)){
				System.out.println("on h1");
				Point_3 intersection = (Point_3)Point_3.linearCombination(new Point_3[]{p2,p3}, new Double[]{(l1-xh1[0])/l1, xh1[0]/l1});
				System.out.println("intersection = "+intersection);
				result.add(intersection);
				traceRay(xh2[0], h2);
			}
			
		}
	}
	
	

}
