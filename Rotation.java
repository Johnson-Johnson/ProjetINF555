import Jama.Matrix;
import Jcg.geometry.*;

public class Rotation {
	Matrix Rotation;
	
	public Rotation(){
		Rotation = new Matrix(3,3);
	}
	
	public Rotation(Point_3 p1, Point_3 p2, Point_3 p3){
		Vector_3 p12 = (Vector_3) p2.minus(p1);
		Vector_3 p13 = (Vector_3) p3.minus(p1);
		Vector_3 v = p12.crossProduct(p13);
		Double norm = Math.sqrt(v.squaredLength().doubleValue());
		Vector_3 n = v.multiplyByScalar(norm);
		double c = n.innerProduct(new Vector_3(0., 0., 1.)).doubleValue();
		double s2 = 1-c*c;
		double s;
		if (n.z>0) s = Math.sqrt(s2);
		else s = - Math.sqrt(s2);
		double[][] array = {{c,-s,0.},{s,c,0.},{0.,0.,1.}};
		Rotation = new Matrix(array);
	}
	
	public Point_3 Transform(Point_3 p){
		double[][] arrayp3 = {{p.x},{p.y},{p.z}};
		Matrix mp3 = new Matrix(arrayp3);
		Matrix res = Rotation.times(mp3);
		return new Point_3(res.get(0, 0), res.get(1, 0), res.get(2, 0));
	}	
}