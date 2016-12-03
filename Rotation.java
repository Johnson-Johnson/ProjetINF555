import Jama.Matrix;
import Jcg.geometry.*;
<<<<<<< HEAD
=======
import Jcg.polyhedron.*;
>>>>>>> a1b1285002c37551a1a3f3a132d1531ba067741f

public class Rotation {
	Matrix Rotation;
	
	public Rotation(){
		Rotation = new Matrix(3,3);
	}
	
<<<<<<< HEAD
=======
	//douteux
>>>>>>> a1b1285002c37551a1a3f3a132d1531ba067741f
	public Rotation(Point_3 p1, Point_3 p2, Point_3 p3){
		Vector_3 p12 = (Vector_3) p2.minus(p1);
		Vector_3 p13 = (Vector_3) p3.minus(p1);
		Vector_3 v = p12.crossProduct(p13);
		Double norm = Math.sqrt(v.squaredLength().doubleValue());
<<<<<<< HEAD
		Vector_3 n = v.multiplyByScalar(norm);
=======
		Vector_3 n = v.multiplyByScalar(1/norm);
>>>>>>> a1b1285002c37551a1a3f3a132d1531ba067741f
		double c = n.innerProduct(new Vector_3(0., 0., 1.)).doubleValue();
		double s2 = 1-c*c;
		double s;
		if (n.z>0) s = Math.sqrt(s2);
		else s = - Math.sqrt(s2);
		double[][] array = {{c,-s,0.},{s,c,0.},{0.,0.,1.}};
		Rotation = new Matrix(array);
	}
	
<<<<<<< HEAD
=======
	public static Rotation GetRotation(Halfedge<Point_3> h){
		Point_3 p1 = h.vertex.getPoint();
		Point_3 p2 = h.next.vertex.getPoint();
		Point_3 p3 = h.opposite.vertex.getPoint();
		Rotation R = new Rotation(p1,p2,p3);
		return R;
	}
	
>>>>>>> a1b1285002c37551a1a3f3a132d1531ba067741f
	public Point_3 Transform(Point_3 p){
		double[][] arrayp3 = {{p.x},{p.y},{p.z}};
		Matrix mp3 = new Matrix(arrayp3);
		Matrix res = Rotation.times(mp3);
<<<<<<< HEAD
		return new Point_3(res.get(0, 0), res.get(1, 0), res.get(2, 0));
	}	
=======
		System.out.println(p.toString());
		return new Point_3(res.get(0, 0), res.get(1, 0), res.get(2, 0));
	}
	
	public Point_3 TransformBack(Point_3 p){
		double[][] arrayp3 = {{p.x},{p.y},{p.z}};
		Matrix mp3 = new Matrix(arrayp3);
		Matrix Inv = Rotation.inverse();
		Matrix res = Inv.times(mp3);
		System.out.println(p.toString());
		return new Point_3(res.get(0, 0), res.get(1, 0), res.get(2, 0));
	}
	
	public void TransformTriangle(Halfedge<Point_3> h){
		Point_3 p1 = h.vertex.getPoint();
		Point_3 p2 = h.next.vertex.getPoint();
		Point_3 p3 = h.opposite.vertex.getPoint();
		Rotation R = new Rotation(p1,p2,p3);
		p1 = R.Transform(p1);
		p2 = R.Transform(p2);
		p3 = R.Transform(p3);
	}
	
	public void TransformTriangleBack(Halfedge<Point_3> h){
		Point_3 p1 = h.vertex.getPoint();
		Point_3 p2 = h.next.vertex.getPoint();
		Point_3 p3 = h.opposite.vertex.getPoint();
		Rotation R = new Rotation(p1,p2,p3);
		p1 = R.TransformBack(p1);
		p2 = R.TransformBack(p2);
		p3 = R.TransformBack(p3);
	}
	
	public void TransformVertex(Vertex<Point_3> v){
		Point_3 p = v.getPoint();
		v.setPoint(Transform(p));
	}
	
	public void TransformBackVertex(Vertex<Point_3> v){
		Point_3 p = v.getPoint();
		v.setPoint(TransformBack(p));
	}
>>>>>>> a1b1285002c37551a1a3f3a132d1531ba067741f
}