import Jcg.polyhedron.*;
import Jcg.geometry.*;

public class Window {
	private Tuple<Double,Double,Double,Double,Double,Halfedge<Point_3>> t;
	
	public Window(Double a, Double b, Double c, Double d, Double e, Halfedge<Point_3> h){
		t = new Tuple<>(a,b,c,d,e,h);
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
	
	//Computing the minimal distance (squared) to the real source over a window
	public double MinimalDSquare(){
		if(Left()==Right()){
			System.out.println("Erreur dans MinimalDSquare, fenêtre de taille nulle");
			return Sigma();
		}
		double xPseudosource = (Math.pow(LeftD(),2.)-Math.pow(RightD(),2.)+Math.pow(Math.abs(Left()-Right()),2.))/(2*(Right()-Left()));
		
		double result = (Math.pow(LeftD(),2.)-Math.pow(Math.abs(xPseudosource), 2.));
		
		if (xPseudosource<0.) result=LeftD()*LeftD();
		else if (xPseudosource>Right()-Left()) result=RightD()*RightD();
		
		return result;
	}
}
