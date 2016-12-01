import Jcg.polyhedron.*;
import Jcg.geometry.*;

public class Window {
	private Tuple<Double,Double,Double,Double,Double,Halfedge<Point_3>,Vertex<Point_3>> t;
	
	public Window(Double a, Double b, Double c, Double d, Double e, Halfedge<Point_3> h, Vertex<Point_3> v){
		t = new Tuple<>(a,b,c,d,e,h,v);
	}
	public void UpdateLeft(Double a1){
		this.t.UpdateA(a1);
	}
	public void UpdateRight(Double a1){
		this.t.UpdateB(a1);
	}
	public void UpdateLeftD(Double a1){
		this.t.UpdateC(a1);
	}
	public void UpdateRightD(Double a1){
		this.t.UpdateD(a1);
	}
	public void UpdateSigma(Double a1){
		this.t.UpdateE(a1);
	}
	public double Left(){
		return t.a;
	}
	public double Right(){
		return t.b;
	}
	public double LeftD(){
		return t.c;
	}
	public double RightD(){
		return t.d;
	}
	public double Sigma(){
		return t.e;
	}
	public Halfedge<Point_3> Halfedge(){
		return t.f;
	}
	public Vertex<Point_3> Pseudosource(){
		return t.g;
	}
	
	//Computing the minimal distance (squared) to the real source over a window
	public double MinimalDSquare(){
		if(Left()==Right()){
			System.out.println("Erreur dans MinimalDSquare, fenêtre de taille nulle");
			return Sigma();
		}
		double d0 = Left()*Left()+LeftD()*Left()+RightD();
		double d1 = Right()*Right()+LeftD()*Right()+RightD();
		double xPseudosource = (Math.pow(d0,2.)-Math.pow(d1,2.)+Math.pow(Math.abs(Left()-Right()),2.))/(2*(Right()-Left()));
		
		double result = (Math.pow(d0,2.)-Math.pow(Math.abs(xPseudosource), 2.));
		
		if (xPseudosource<0.) result=d0*d0;
		else if (xPseudosource>Right()-Left()) result=d1*d1;
		
		return result;
	}
	
	public String to_string(){
		String s0 = "on the Halfedge " + this.Halfedge().toString();
		String s1 = "edge coeff: ("+this.Left()+", "+this.Right()+")" + " and distances: ("+this.LeftD()+", "+this.RightD()+")";
		String s2 = "Sigma: "+this.Sigma() + " and pseudosource: " +this.Pseudosource().getPoint().toString() ;
		return s0+"\n"+s1+"\n"+s2;
	}
}
