import Jcg.polyhedron.*;
import Jcg.geometry.*;

public class Window {
	private Tuple<Double,Double,Double,Double,Double,Boolean,Vertex<Point_3>,Vertex<Point_3>,Integer> t;
	
	public Window(Double a, Double b, Double c, Double d, Double e, Boolean b1, Vertex<Point_3> v1, Vertex<Point_3> v2, Integer i){
		t = new Tuple<>(a,b,c,d,e,b1,v1,v2,i);
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
	public boolean Orientation(){
		return t.f;
	}
	public Vertex<Point_3> LeftVertex(){
		return t.g;
	}
	public Vertex<Point_3> RightVertex(){
		return t.h;
	}
	public int Pseudosource(){
		return t.i;
	}
	
	//Computing the minimal distance (squared) to the real source over a window
	public double MinimalDSquare(){
		if(Left()==Right()){
			System.out.println("Erreur dans MinimalDSquare, fenêtre de taille nulle");
			return Sigma();
		}
		double xPseudosource = (Math.pow(LeftD(),2.)-Math.pow(RightD(),2.)+Math.pow(Math.abs(Left()-Right()),2.))/(2*(Right()-Left()));
		
		return (Math.pow(LeftD(),2.)-Math.pow(Math.abs(xPseudosource), 2.));
	}
}
