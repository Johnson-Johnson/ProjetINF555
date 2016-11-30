import java.util.*;

import Jcg.geometry.*;
import Jcg.polyhedron.*;
import Jama.Matrix;

import java.math.*;

import processing.core.PApplet;

public class ExactAlgorithm {
	
	PriorityQueue<Window> Q;
	
	ArrayList<TreeSet<Window>> T;
	
	public Polyhedron_3<Point_3> polyhedron3D;
	
	//just for test purpose
	public ExactAlgorithm() {
		this.polyhedron3D=null;
		this.Q=null;
		this.T=null;
	}
	
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
	
	//		s
	//
	//
	//
	//          h
	//	   p1_______p2
	//		\		/
	//		 \h2 h1/
	//		  \	  /
	//         p3
	//
	
	//Suppose le triangle dans un plan z=cste !!!
	private ArrayList<Window> FindOppositeWindows(Window w){
		ArrayList<Window> result = new ArrayList<Window>();
		
		Halfedge<Point_3> h = w.Halfedge();
		Halfedge<Point_3> h1 = h.opposite.prev;
		Halfedge<Point_3> h2 = h.opposite.next;
		
		Point_3 source = w.Pseudosource().getPoint();
		
		Point_3 p1 = h.opposite.vertex.getPoint();
		Point_3 p2 = h.vertex.getPoint();
		Point_3 p3 = h2.vertex.getPoint();
		
		//je pense qu'il faut mettre la longueur de l'arrête en mémoire de la window pour s'éviter ces calculs de merde
		double l = Math.sqrt(p2.minus(p1).squaredLength().doubleValue());
		double l1 = Math.sqrt(p2.minus(p3).squaredLength().doubleValue());
		double l2 = Math.sqrt(p3.minus(p1).squaredLength().doubleValue());
		
		Point_3 wl = (Point_3)Point_3.linearCombination(new Point_3[]{p1,p2}, new Double[]{(l-w.Left())/l, w.Left()/l});
		Point_3 wr = (Point_3)Point_3.linearCombination(new Point_3[]{p1,p2}, new Double[]{(l-w.Right())/l, w.Right()/l});
		
		Point_2 s_2 = new Point_2(source.x, source.y);
		Point_2 p_2_0 = new Point_2(wl.x, wl.y);
		Point_2 p_2_1 = new Point_2(p1.x, p1.y);
		Point_2 p_2_2 = new Point_2(p3.x, p3.y);
		double[] cu2 = this.FindIntersection(s_2, p_2_0, p_2_1, p_2_2);
		double u2 = cu2[0];
		p_2_0 = new Point_2(wr.x, wr.y);
		double[] cv2 = this.FindIntersection(s_2, p_2_0, p_2_1, p_2_2);
		double v2 = cv2[0];
		p_2_1 = new Point_2(p3.x, p3.y);
		p_2_2 = new Point_2(p2.x, p2.y);
		double[] cv1 = this.FindIntersection(s_2, p_2_0, p_2_1, p_2_2);
		double v1 = cv1[0];
		p_2_0 = new Point_2(wl.x, wl.y);
		double[] cu1 = this.FindIntersection(s_2, p_2_0, p_2_1, p_2_2);
		double u1 = cu1[0];
		
		//first : left edge in case it's the only one hit by the ray
		if (u2>=0 && u2<v2 && v2<=l2){
			System.out.println("case 1");
			double[] coeff = this.exchangeCoeff(new double[]{u2,v2,cu2[1]*cu2[1],cv2[1]*cv2[1]});
			Window w2 = new Window(u2,v2,coeff[0],coeff[1], w.Sigma(),h2,w.Pseudosource());
			result.add(w2);
		}
		
		//second : left Window is not hit. Only second edge is.
		if (u1>=0 && u1<v1 && v1<=l1){
			System.out.println("case 2");
			double[] coeff = this.exchangeCoeff(new double[]{u1,v1,cu1[1]*cu1[1],cv1[1]*cv1[1]});
			Window w1 = new Window(u1,v1,coeff[0],coeff[1], w.Sigma(),h1,w.Pseudosource());
			result.add(w1);
		}
		
		//third: both edges are hit by the ray
		if (u2>=0 && v2>l2){
			System.out.println("case 3");
			double lsp3 = p3.minus(source).squaredLength().doubleValue();
			double[] coeff = this.exchangeCoeff(new double[]{u2,l2,cu2[1]*cu2[1],lsp3});
			double[] coeffp = this.exchangeCoeff(new double[]{0.,v1,lsp3,cv1[1]*cv1[1]});
			
			Window w2 = new Window(u2,l2,coeff[0],coeff[1], w.Sigma(),h2,w.Pseudosource());
			Window w1 = new Window(0.,v1,coeffp[0],coeffp[1], w.Sigma(),h1,w.Pseudosource());
			result.add(w2); result.add(w1);
		}
		
		//fourth: case b0 = 0 or b1 = l --> creation of a new pseudosource and we add two more windows (dans le cas d'angle mort)
		if (w.Left()==0 && u1>0 && u1<l1){
			System.out.println("case 4-1");
			double d0 = Math.sqrt(w.Left()*w.Left() + w.LeftD()*w.Left() + w.RightD());
			double[] coeff = this.exchangeCoeff(new double[]{0.,l2,0.,l2*l2});
			double[] coeffp = this.exchangeCoeff(new double[]{0.,u1,l2*l2,(cu1[1]-d0)*(cu1[1]-d0)});
			
			Window w2 = new Window(0.,l2,coeff[0],coeff[1], w.Sigma()+d0,h2,h.opposite.vertex);
			Window w1 = new Window(0.,u1,coeffp[0],coeffp[1], w.Sigma()+d0 ,h1,h.opposite.vertex);
			result.add(w1); result.add(w2);
		}
		
		if (w.Right()==l && v2>0 && v2<l2){
			System.out.println("case 4-2");
			double d1 = Math.sqrt(w.Right()*w.Right() + w.LeftD()*w.Right() + w.RightD());
			double[] coeff = this.exchangeCoeff(new double[]{v2,l2,(cv2[1]-d1)*(cv2[1]-d1),l1*l1});
			double[] coeffp = this.exchangeCoeff(new double[]{0.,l1,l1*l1,0.});
			
			Window w2 = new Window(v2,l2,coeff[0],coeff[1], w.Sigma()+d1,h2,h.vertex);
			Window w1 = new Window(0.,l1,coeffp[0],coeffp[1], w.Sigma()+d1 ,h1,h.vertex);
			result.add(w1); result.add(w2);
		}
		return result;
	}
	//				 p2
	//				/
	//  s-----p0---/------->
	//			  /
	//			p1
	//
	//returns position of intersection point
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
	
	//the two functions that follow allow us to switch from para coeff to dist coeff
	//from distances to parabolic coeff
	private double[] exchangeCoeff(double[] para){
		double c1 = ((para[3] - para[2])-(para[1]*para[1] - para[0]*para[0]))/(para[1]- para[0]);
		double c2 = para[2] - para[0]*para[0] - c1*para[0];
		double[] res = {c1, c2};
		return res;
	}
	
	//from parabolic coeff to distances
	private double[] exchangeBackCoeff(double[] para){
		double d0 = para[0]*para[0] + para[2]*para[0] + para[3];
		double d1 = para[1]*para[1] + para[2]*para[1] + para[3];
		double[] res = {d0, d1};
		return res;
	}
	
	private void Propagation(Window w){
		
	}
	
	public static void main(String args[]) {
	ExactAlgorithm inst = new ExactAlgorithm();	
	
	//BEGIN TEST
	System.out.println("test");
	//param window test (here percentage)
	double coeffl = 0.;
	double coeffr = 1.;
	Point_3 s = new Point_3(5.,0.5,0);
	Point_3 P1 = new Point_3(1,2,0);
	Point_3 P2 = new Point_3(3,1,0);
	Point_3 P3 = new Point_3(1,1,0);
	Vertex<Point_3> vs = new Vertex<Point_3>(s);
	Vertex<Point_3> vp1 = new Vertex<Point_3>(P1);
	Vertex<Point_3> vp2 = new Vertex<Point_3>(P2);
	Vertex<Point_3> vp3 = new Vertex<Point_3>(P3);
	Halfedge<Point_3> h = new Halfedge<Point_3>();
	Halfedge<Point_3> ho = new Halfedge<Point_3>();
	Halfedge<Point_3> h1 = new Halfedge<Point_3>();
	Halfedge<Point_3> h1o = new Halfedge<Point_3>();
	Halfedge<Point_3> h2 = new Halfedge<Point_3>();
	Halfedge<Point_3> h2o = new Halfedge<Point_3>();
	h.setVertex(vp2);
	h.setOpposite(ho);
	ho.setVertex(vp1);
	ho.setOpposite(h);
	ho.setNext(h2);
	ho.setPrev(h1);
	h1.setVertex(vp2);
	h1.setOpposite(h1o);
	h1o.setVertex(vp3);
	h1o.setOpposite(h1);
	h2.setVertex(vp3);
	h2.setOpposite(h2o);
	h2o.setVertex(vp1);
	h2o.setOpposite(h2);
	double l = Math.sqrt(P1.minus(P2).squaredLength().doubleValue());
	coeffl*=l;coeffr*=l;
	Point_3 wl = (Point_3)Point_3.linearCombination(new Point_3[]{P1,P2}, new Double[]{(l-coeffl)/l, coeffl/l});
	Point_3 wr = (Point_3)Point_3.linearCombination(new Point_3[]{P1,P2}, new Double[]{(l-coeffr)/l, coeffr/l});
	double d0 = s.minus(wl).squaredLength().doubleValue();
	double d1 = s.minus(wr).squaredLength().doubleValue();
	double[] coeff = inst.exchangeCoeff(new double[]{coeffl, coeffr,d0,d1});
	Window w = new Window(coeffl, coeffr, coeff[0], coeff[1], 0., h, vs);
	ArrayList<Window> lw = inst.FindOppositeWindows(w);
	for (Window wi : lw){
		System.out.println(wi.to_string());
		//if we want to check distances
		//double[] tab = inst.exchangeBackCoeff(new double[]{wi.Left(), wi.Right(), wi.LeftD(), wi.RightD()});
		//System.out.println(Math.sqrt(tab[0])+", "+Math.sqrt(tab[1]));
	}
	//END TEST
		
	}
	
}
