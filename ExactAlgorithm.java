import java.util.*;

import Jcg.geometry.*;
import Jcg.polyhedron.*;
import Jama.Matrix;
//import java.math.*;
//import processing.core.PApplet;

public class ExactAlgorithm {
	
	PriorityQueue<Window> Q;
	
	ArrayList<TreeSet<Window>> T;
	
	public Polyhedron_3<Point_3> polyhedron3D;
	
	//just for TEST purpose
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
			index++;
		}
	}
	
	//Précision à 1E-7
	static boolean equal(double a, double b){
		if(Math.abs(a-b)<0.0000001) return true;
		return false;
	}
	
	static double Norme(Halfedge<Point_3> h){
		Point_3 p1 = h.vertex.getPoint();
		Point_3 p2 = h.opposite.vertex.getPoint();
		
		return (Double) p1.distanceFrom(p2);
	}
	
	//Résolution équation degré 2
	static Pair<Double,Double> Solve(double a, double b, double c){
		double Delta = b*b-4*a*c;
		if (equal(Delta,0.)) Delta = 0.;
		if(Delta<0) return null;
		return new Pair<Double,Double>((-b+Math.sqrt(Delta))/(2*a),(-b-Math.sqrt(Delta))/(2*a));
		
	}
	
	//Calcule les coordonnées de la pseudosource avec pour origine l'extrémité gauche de la fenêtre
	public Point_2 Pseudosource(Window w){
		double b0 = w.Left();
		double b1 = w.Right();
		double d0 = w.LeftD();
		double d1 = w.RightD();
		d0 = d0*d0;
		d1 = d1*d1;
		double x = 1./2.*((b1-b0)+(d0-d1)/(b1-b0));
		double y = Math.sqrt(d0-x*x);
		
		return new Point_2(x,y);
	}
	
	//Calcule le point de superposition de 2 window avec LE MEME INTERVALLE
	//return le point d'intersection ou -1 si il n'y a pas de separation
	
	//on découpe les window qui s'intersectent de sorte que 
	//le fight soit entre deux window sur le même intervalle
	private double intersectPoint(Window w1, Window w2){
		
		Point_2 p1 = Pseudosource(w1);
		Point_2 p2 = Pseudosource(w2);

		System.out.println("IntersectPoint p1 "+p1.toString());
		System.out.println("IntersectPoint p2 "+p2.toString());

		double np1 = p1.x*p1.x+p1.y*p1.y;
		double np2 = p2.x*p2.x+p2.y*p2.y;
		
		double sigma1 = w1.Sigma();
		double sigma2 = w2.Sigma();
		
		double alpha = p2.x-p1.x;
		double beta = sigma2-sigma1;
		double gamma = np1-np2-beta*beta;
		
		double A = alpha*alpha-beta*beta;
		double B = gamma*alpha+2*p2.x*beta*beta;
		double C = 1./4.*gamma*gamma-np2*beta*beta;

		double b0 = w1.Left();
		double b1 = w1.Right();
		
		Pair<Double,Double> psol = Solve(A,B,C);
		if (A==0 && B!= 0){
			return -C/B;
		}
		//racines du binôme
		if (psol != null){
			double r1 = psol.first();
			//double r2 = psol.second();
			//System.out.println("roots :"+r1+", "+r2);
			//la theorie cest que c'est une racine double --> to be checked
			if (b0<r1 && r1<b1){
				return r1;
			}		
		}
		return -1.;
	}
	
	//on considere que c'est la w1 qui était déjà en place et qui est challengée par w2
	//ainsi on insère w2 dans la pile en retirant w1 si w2 gagne
	//et on ne fait rien si w1 gagne
	private void handleConflict(Window w1, Window w2){
		double intersect = this.intersectPoint(w1, w2);
		if (intersect == -1 && w1.LeftD()+w1.Sigma()<w2.LeftD()+w2.Sigma()){
			TreeSet<Window> tswi = T.get(w1.Halfedge().index);
			tswi.remove(w1);
			tswi.add(w2);

			Q.add(w2);

		}
		
		//on a un point d'intersect
		else {
			double[] paracoeff1 = this.exchangeCoeff(new double[]{w1.Left(), w1.Right(), w1.LeftD()*w1.LeftD(), w1.RightD()});
			double[] paracoeff2 = this.exchangeCoeff(new double[]{w1.Left(), w1.Right(), w2.LeftD(), w2.RightD()});
			double d11 = intersect*intersect + paracoeff1[0]*intersect+paracoeff1[1];
			double d20 = intersect*intersect + paracoeff2[0]*intersect+paracoeff2[1];
			w1.UpdateRight(intersect);w1.UpdateRightD(d11);
			w2.UpdateLeft(intersect);w2.UpdateLeftD(d20);
			TreeSet<Window> tswi = T.get(w1.Halfedge().index);
			tswi.remove(w1);
			tswi.add(w1);
			tswi.add(w2);

			Q.add(w1);
			Q.add(w2);

		}
	}
	
	
	
	
	//		s
	//      |\ 
	//      | \
	//      |  \
	//      | h \
	//	   p1____\__p2
	//		\	  \	/
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
		
		//cas ou la pseudosource et p1 sont confondus
		if (source.equals(p1)){
			System.out.println("Case 0-1");
			Window wsc1 = new Window(0., l1, l2, l, w.Sigma(), h1,w.Pseudosource());
			Window wsc2 = new Window(0., l2, 0., l2, w.Sigma(), h2, w.Pseudosource());
			result.add(wsc1);
			//result.add(wsc2);
			return result;
		}
		
		//cas ou la pseudosource et p2 sont confondus
		if (source.equals(p2)){
			System.out.println("Case 0-2");
			Window wsc1 = new Window(0., l1, l1, 0., w.Sigma(), h1, w.Pseudosource());
			Window wsc2 = new Window(0., l2, l, l1, w.Sigma(), h2,w.Pseudosource());
			//result.add(wsc1);
			result.add(wsc2);
			return result;
		}
		
		Point_3 wl = (Point_3)Point_3.linearCombination(new Point_3[]{p1,p2}, new Double[]{(l-w.Left())/l, w.Left()/l});
		Point_3 wr = (Point_3)Point_3.linearCombination(new Point_3[]{p1,p2}, new Double[]{(l-w.Right())/l, w.Right()/l});
		
		//Point_2 s_2 = new Point_2(source.x, source.y);
		Point_2 s_2 = this.Pseudosource(w);
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
			//double[] coeff = this.exchangeCoeff(new double[]{u2,v2,cu2[1]*cu2[1],cv2[1]*cv2[1]});
			Window w2 = new Window(u2,v2,cu2[1],cv2[1], w.Sigma(),h2,w.Pseudosource());
			result.add(w2);
		}
		
		//second : left Window is not hit. Only second edge is.
		if (u1>=0 && u1<v1 && v1<=l1){
			System.out.println("case 2");
			//double[] coeff = this.exchangeCoeff(new double[]{u1,v1,cu1[1]*cu1[1],cv1[1]*cv1[1]});
			Window w1 = new Window(u1,v1,cu1[1],cv1[1], w.Sigma(),h1,w.Pseudosource());
			result.add(w1);
		}
		
		//third: both edges are hit by the ray
		if (u2>=0 && v2>l2){
			System.out.println("case 3");
			double lsp3 = Math.sqrt(p3.minus(source).squaredLength().doubleValue());
			//double[] coeff = this.exchangeCoeff(new double[]{u2,l2,cu2[1]*cu2[1],lsp3});
			//double[] coeffp = this.exchangeCoeff(new double[]{0.,v1,lsp3,cv1[1]*cv1[1]});
			
			Window w2 = new Window(u2,l2,cu2[1],lsp3, w.Sigma(),h2,w.Pseudosource());
			Window w1 = new Window(0.,v1,lsp3,cv1[1], w.Sigma(),h1,w.Pseudosource());
			result.add(w2); result.add(w1);
		}
		
		//fourth: case b0 = 0 or b1 = l --> creation of a new pseudosource and we add two more windows (dans le cas d'angle mort)
		if (w.Left()==0 && u1>0 && u1<l1){
			System.out.println("case 4-1");
			double d0 = Math.sqrt(w.Left()*w.Left() + w.LeftD()*w.Left() + w.RightD());
			//double[] coeff = this.exchangeCoeff(new double[]{0.,l2,0.,l2*l2});
			//double[] coeffp = this.exchangeCoeff(new double[]{0.,u1,l2*l2,(cu1[1]-d0)*(cu1[1]-d0)});
			Window w2 = new Window(0.,l2,0.,l2, w.Sigma()+d0,h2,h.opposite.vertex);
			Window w1 = new Window(0.,u1,l2,cu1[1]-d0, w.Sigma()+d0 ,h1,h.opposite.vertex);
			result.add(w1); result.add(w2);
		}
		
		if (w.Right()==l && v2>0 && v2<l2){
			System.out.println("case 4-2");
			double d1 = Math.sqrt(w.Right()*w.Right() + w.LeftD()*w.Right() + w.RightD());
			//double[] coeff = this.exchangeCoeff(new double[]{v2,l2,(cv2[1]-d1)*(cv2[1]-d1),l1*l1});
			//double[] coeffp = this.exchangeCoeff(new double[]{0.,l1,l1*l1,0.});
			Window w2 = new Window(v2,l2,cv2[1]-d1,l1, w.Sigma()+d1,h2,h.vertex);
			Window w1 = new Window(0.,l1,l1,0., w.Sigma()+d1 ,h1,h.vertex);
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
	

	//main function in order to test previous fun
	/*
	public static void main(String args[]) {
		ExactAlgorithm inst = new ExactAlgorithm();	
		
		//BEGIN TEST
		System.out.println("test 1");
		//param window test (here percentage)
		double coeffl = 0.;
		double coeffr = 1.;
		double sigma = 0.;
		double sigma2 = 0.;
		Point_3 s = new Point_3(1.,2.,0);
		Point_3 s2 = new Point_3(0.5,5,0);
		Point_3 P1 = new Point_3(1,2,0);
		Point_3 P2 = new Point_3(3,1,0);
		Point_3 P3 = new Point_3(1,1,0);
		Vertex<Point_3> vs = new Vertex<Point_3>(s);
		Vertex<Point_3> vs2 = new Vertex<Point_3>(s);
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
		double d0 = Math.sqrt(s.minus(wl).squaredLength().doubleValue());
		double d1 = Math.sqrt(s.minus(wr).squaredLength().doubleValue());
		double d20 = Math.sqrt(s2.minus(wl).squaredLength().doubleValue());
		double d21 = Math.sqrt(s2.minus(wr).squaredLength().doubleValue());
		//double[] coeff = inst.exchangeCoeff(new double[]{coeffl, coeffr,d0,d1});
		Window w = new Window(coeffl, coeffr, d0, d1, 0., h, vs);
		ArrayList<Window> lw = inst.FindOppositeWindows(w);
		for (Window wi : lw){
			System.out.println(wi.to_string());
			//if we want to check distances
			//double[] tab = inst.exchangeBackCoeff(new double[]{wi.Left(), wi.Right(), wi.LeftD(), wi.RightD()});
			//System.out.println(Math.sqrt(tab[0])+", "+Math.sqrt(tab[1]));
		}
		//END TEST
		
		//TEST2
		System.out.println("*** *** ***");
		System.out.println("test2");
		System.out.println("distances"+d0+", "+d1+", "+d20+", "+d21);
		Window w1 = new Window(coeffl, coeffr, d0, d1, sigma, h, vs);
		Window w2 = new Window(coeffl, coeffr, d20, d21, sigma2, h, vs2);
		System.out.println(inst.intersectPoint(w1, w2));
	}
	*/
	
	public void Geodesics(Vertex<Point_3> s){
    	
		this.Start(s);
		System.out.println("iniQsize = "+Q.size());
		int compteur = 0;
		
		while(!Q.isEmpty()){
			if (compteur ==20) break;
			compteur++;
			//On prend la plus proche
			Window w = Q.poll();
			System.out.println("\n\n\n\ncurrent window"+w.to_string());
			//On ramène le triangle dans un plan orthogonal à (Oz)
			Halfedge<Point_3> h = w.Halfedge();
			Rotation R = new Rotation(h);
			//R.basicInfo();
			System.out.println(R.toString());
			System.out.println("transform triangle");
			R.TransformTriangle(h);
		
			//On trouve les opposite Windows
			ArrayList<Window> New = FindOppositeWindows(w);
			System.out.println("transform back triangle");
			R.TransformTriangleBack(h);
			
			for (Window wi : New){
				System.out.println("windows to be merged" + wi.to_string());
			}
			
			//Pour chacune...
			for(Window wi : New){
				//On prend les windows déjà présentes sur la halfedge correspondante
				Halfedge<Point_3> hi = wi.Halfedge();
				TreeSet<Window> Ti = this.T.get(hi.index);
				if (Ti.tailSet(wi, true).isEmpty()){
					System.out.println("il n'y a personne sur l'edge");
					Ti.add(wi);
					Q.add(wi);
					continue;
				}
				double[] Coeff = exchangeCoeff(new double[] {wi.Left(),wi.Right(),wi.LeftD(),wi.RightD()});
				//On définit un tableau pour définir les nouvelles windows correctement découpées
				double last = wi.Left();
				//On découpe
				//ArrayList<Window> Remove = new ArrayList<Window>();
				//ArrayList<Window> Add = new ArrayList<Window>();
				System.out.println("nombre de windows à comparer : " + Ti.tailSet(wi, true).size());
				for(Window Wcompare : Ti.tailSet(wi, true)){
					System.out.println("already there"+Wcompare.to_string());
					//Pas d'intersection (fin de la boucle)
					if(Wcompare.Left()>wi.Right()) break;
					//Pas d'intersection au début (cas absurde 
					//if(Wcompare.Right()<wi.Left()) continue;
					
					//Si y a un espace vide
					if(!equal(Wcompare.Left(),last)) {
						System.out.println("existence de Vide sur l'arête");
						double[] D = exchangeBackCoeff(new double[] {last, Wcompare.Left(),Coeff[0],Coeff[1]});
						Window WindowCut = new Window(last,Wcompare.Left(),D[0],D[1],wi.Sigma(),wi.Halfedge(),wi.Pseudosource());
						//CutWindows.add(WindowCut);
						Ti.add(WindowCut);
						last = WindowCut.Right();
					}
					//On découpe
					System.out.println("decoupage pre-conflit");
					double[] D = exchangeBackCoeff(new double[] {last, Wcompare.Left(),Coeff[0],Coeff[1]});
					Window WindowCut = new Window(last,Math.min(Wcompare.Right(),wi.Right()),D[0],D[1],wi.Sigma(),wi.Halfedge(),wi.Pseudosource());
					last = WindowCut.Right();
					//On compare puis on push les windows
					handleConflict(WindowCut, Wcompare);
				}
				//Si espace vide à la fin
				if (!equal(last,wi.Right())){
					Window WindowCut = new Window(last,wi.Left(),wi.LeftD(),wi.RightD(),wi.Sigma(),wi.Halfedge(),wi.Pseudosource());
					//CutWindows.add(WindowCut);
					Ti.add(WindowCut);
				}
			}
			//On remet le triangle comme il faut
			//R.TransformTriangleBack(h);
		}
	}

	//initialise la queue avec les bonnes windows
	public void Start(Vertex<Point_3> s){
		Halfedge<Point_3> h = s.getHalfedge();
		Halfedge<Point_3> e = h.opposite;
		
		do {
			Window w = new Window(0.,Norme(e),0.,Norme(e),0.,e,s);
			this.Q.add(w);
			e = e.opposite.next;
		} while(e.opposite!=h);

	}
	
}
