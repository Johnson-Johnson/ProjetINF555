import Jama.Matrix;
import Jcg.geometry.*;
import Jcg.polyhedron.*;

public class Rotation {
	Matrix Rotation;
	
	public Rotation(){
		Rotation = new Matrix(3,3);
	}
	

	public String toString(){
		String l0 = "( "+Rotation.get(0, 0)+", "+ Rotation.get(0, 1)+", "+ Rotation.get(0, 2)+")";
		String l1 = "( "+Rotation.get(1, 0)+", "+ Rotation.get(1, 1)+", "+ Rotation.get(1, 2)+")";
		String l2 = "( "+Rotation.get(2, 0)+", "+ Rotation.get(2, 1)+", "+ Rotation.get(2, 2)+")";
		return l0+"\n"+l1+"\n"+l2;
	}
	
	public void basicInfo(){
		System.out.println("det = "+Rotation.det());
		Matrix Id = Rotation.times(Rotation.transpose());
		String l0 = "( "+Id.get(0, 0)+", "+ Id.get(0, 1)+", "+ Id.get(0, 2)+")";
		String l1 = "( "+Id.get(1, 0)+", "+ Id.get(1, 1)+", "+ Id.get(1, 2)+")";
		String l2 = "( "+Id.get(2, 0)+", "+ Id.get(2, 1)+", "+ Id.get(2, 2)+")";
		System.out.println(l0+"\n"+l1+"\n"+l2);
	}
	
	public Matrix getRotation(Point_3 p1, Point_3 p2, Point_3 p3){
		Vector_3 p12 = (Vector_3) p2.minus(p1);
		Vector_3 p13 = (Vector_3) p3.minus(p1);
		Vector_3 z = new Vector_3(0.,0.,1.);
		Vector_3 v = p12.crossProduct(p13);
		Double norm = Math.sqrt(v.squaredLength().doubleValue());
		Vector_3 n = v.multiplyByScalar(1/norm); //normale au plan p1,p2,P3
		double c = n.innerProduct(z).doubleValue();
		double s2 = 1-c*c;
		double s;
		if (n.z>0) s = Math.sqrt(s2);
		else s = - Math.sqrt(s2);
		
		Vector_3 ncz = n.crossProduct(z);
		Double normncz = Math.sqrt(ncz.squaredLength().doubleValue());
		Vector_3 nczn = ncz.multiplyByScalar(1/normncz); //vecteur selon lequel on doit tourner pour ramener dans un plan ortho a z
		double ux = nczn.x;
		double uy = nczn.y;
		
		double[][] array = {{ux*ux*(1-c)+c,ux*uy*(1-c),uy*s},{ux*uy*(1-c),uy*uy*(1-c)+c,-ux*s},{-uy*s,ux*s,c}};
		return (new Matrix(array));
	}
	
	public Rotation(Halfedge<Point_3> h){
		Point_3 p1 = h.opposite.vertex.getPoint();
		Point_3 p2 = h.vertex.getPoint();
		Point_3 p3 = h.opposite.next.vertex.getPoint();
		Rotation = getRotation(p1,p2,p3);
	}
	

	public Point_3 Transform(Point_3 p){
		double[][] arrayp3 = {{p.x},{p.y},{p.z}};
		Matrix mp3 = new Matrix(arrayp3);
		Matrix res = Rotation.times(mp3);
		Point_3 resp = new Point_3(res.get(0, 0), res.get(1, 0), res.get(2, 0));
		System.out.println(resp.toString());
		return resp;
	}
	
	/*public Point_3 TransformBack(Point_3 p){
		double[][] arrayp3 = {{p.x},{p.y},{p.z}};
		Matrix mp3 = new Matrix(arrayp3);
		Matrix Inv = Rotation.inverse();
		Matrix res = Inv.times(mp3);
		System.out.println("after rotation " + p.toString());
		return new Point_3(res.get(0, 0), res.get(1, 0), res.get(2, 0));
	}*/
	
	//ne fonctionne que si la rotation a été initialisée
	public void TransformTriangle(Halfedge<Point_3> h){
		Point_3 p1 = h.opposite.vertex.getPoint();
		Point_3 p2 = h.vertex.getPoint();
		Point_3 p3 = h.opposite.next.vertex.getPoint();
		h.vertex.setPoint(Transform(p2));
		h.opposite.vertex.setPoint(Transform(p1));
		h.opposite.next.vertex.setPoint(Transform(p3));
	}
	
	//ne fonctionne que si les points ont déjà été tournés bien entendu
	public void TransformTriangleBack(Halfedge<Point_3> h){
		Rotation = Rotation.transpose();
		TransformTriangle(h);
	}
	
	
	/*public void TransformVertex(Vertex<Point_3> v){
		Point_3 p = v.getPoint();
		v.setPoint(Transform(p));
	}
	
	public void TransformBackVertex(Vertex<Point_3> v){
		Point_3 p = v.getPoint();
		v.setPoint(TransformBack(p));
	}*/
}