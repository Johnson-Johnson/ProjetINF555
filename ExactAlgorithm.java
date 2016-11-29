import java.util.*;

import Jcg.geometry.*;
import Jcg.polyhedron.*;

import java.math.*;

public class ExactAlgorithm {
	
	PriorityQueue<Window> Q;
	
	ArrayList<TreeSet<Window>> T;
	
	public Polyhedron_3<Point_3> polyhedron3D;
	
	public ExactAlgorithm(Polyhedron_3<Point_3> polyhedron3D) {
		this.polyhedron3D=polyhedron3D;
		this.Q = new PriorityQueue<Window>(new WindowComparator());
		this.T = new ArrayList<TreeSet<Window>>();
		int index = 0;
		for(Halfedge<Point_3> h : this.polyhedron3D.halfedges){
			h.index=index;
			T.add(new TreeSet<Window>(new WindowBinarySearch()));
		}
	}
	
	//Précision à 1E-7
	static boolean equal(double a, double b){
		if(Math.abs(a-b)<0.0000001) return true;
		return false;
	}
	
	//Retourne la norme 2 d'un point (x,y)
	static double Norme2(Pair<Double,Double> p){
		return (p.first()*p.first()+p.second()*p.second());
	}
	
	//Résolution équation degré 2
	static Pair<Double,Double> Solve(double a, double b, double c){
		double Delta = b*b-4*a*c;
		if(Delta<0) return null;
		return new Pair((-b+Math.sqrt(Delta))/(2*a),(-b-Math.sqrt(Delta))/(2*a));
	}
	
	//Calcule les coordonnées de la pseudosource avec pour origine l'extrémité gauche de la fenêtre
	static Pair<Double,Double> Pseudosource(Window w){
		double b0 = w.Left();
		double b1 = w.Right();
		double d0 = w.LeftD();
		double d1 = w.RightD();
		
		d0 = d0*d0;
		d1 = d1*d1;
		
		double x = (b1-b0)+(d0-d1)/(b1-b0);
		double y = Math.sqrt(d0-x*x);
		
		return new Pair(x,y);
	}
	
	//Calcule le point de superposition de 2 window
	//Check pour dire si w1 l'emporte sur un intervalle non nul ou pas
	static double Fight(Window w1, Window w2, boolean check){
		
		Pair<Double,Double> p1 = Pseudosource(w1);
		Pair<Double,Double> p2 = Pseudosource(w2);
		
		p2.UpdateA(p2.first()+w2.Left()-w1.Left());
		
		double x1 = p1.first();
		double y1 = p1.second();
		double x2 = p2.first();
		double y2 = p2.second();
		
		double sigma1 = w1.Sigma();
		double sigma2 = w2.Sigma();
		
		double alpha = x2-x1;
		double beta = sigma2-sigma1;
		double gamma = Norme2(p1)-Norme2(p2)-beta*beta;
		
		double A = alpha*alpha-beta*beta;
		double C = (1/4.)*gamma*gamma-Norme2(p2)*beta*beta;
		double B = gamma*alpha+2*x2*beta*beta;
		
		double px = Solve(A,B,C).first();
		
		//TODO Changer ce test de merde
		if(equal(px,w1.Left())) check = false;
		else check = true;
		
		return px;
	}
	
	//Suppose le triangle dans un plan z=cste !!!
	private Pair<Pair<Double,Double>,Pair<Double,Double>> FindOppositeWindows(Window w){
		Halfedge<Point_3> h = w.Halfedge();
		Halfedge<Point_3> h1 = h.next.opposite;
		Halfedge<Point_3> h2 = h.next.next.opposite;
		
		Point_3 p1 = h.vertex.getPoint();
		Point_3 p2 = h.next.vertex.getPoint();
		Point_3 p3 = h.next.next.vertex.getPoint();
		
		Point_3 Intersect1Gauche;
		Point_3 Intersect2Gauche;
		Point_3 Intersect1Droite;
		Point_3 Intersect2Droite;
		
		
		
		return null;
	}
	
	private void Propagation(Window w){
		
	}
	
	
}
