import java.util.Arrays;
import Jama.Matrix;
import Jcg.geometry.*;

public class Rotation {
	Matrix Rotation;
	
	public Rotation(){
		Rotation = new Matrix(3,3);
	}
	
	public Rotation(Point_3 p1, Point_3 p2, Point_3 p3){
		Plane_3 P = new Plane_3(p1, p2, p3);
		Vector_3 v = P.orthogonalVector();
		Vector_3 Oz = new Vector_3(0.,0.,1.);
		
		
	}
	
	public double[] Transform(double[] vector){
		double[] result = new double[3];
		Arrays.fill(result, 0);
		for(int i = 0; i<3; i++){
			for(int j = 0; j<3; j++){
				//result[i]+=vector[j]*Rotation[i][j];
			}
		}
		return result;
	}
	
	
	
}
