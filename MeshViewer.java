import processing.core.*;

import Jcg.geometry.*;

/**
 * A simple 3d viewer for visualizing surface meshes
 * 
 * @author Luca Castelli Aleardi (INF555, 2012)
 *
 */
public class MeshViewer extends PApplet {

	SurfaceMesh mesh;
	//String filename="OFF/high_genus.off";
	//String filename="OFF/sphere.off";
	//String filename="OFF/cube.off";
	//String filename="OFF/torus_33.off";
	//String filename="OFF/tore.off";
	String filename="OFF/tri_round_cube.off";
	//String filename="OFF/tri_hedra.off";
	//String filename="OFF/tri_horse.off";
	
	int shortestPath=0;
	int nMethods=3; // number of simplification methods proposed
	
	public void setup() {
		  size(800,600,P3D);
		  ArcBall arcball = new ArcBall(this);
		  
		  this.mesh=new SurfaceMesh(this, filename);
	}
	
	public void updatedMethod() {
		/*if(this.simplificationMethod==0) {
		}*/
	}

		 
		public void draw() {
		  background(0);
		  //this.lights();
		  directionalLight(101, 204, 255, -1, 0, 0);
		  directionalLight(51, 102, 126, 0, -1, 0);
		  directionalLight(51, 102, 126, 0, 0, -1);
		  directionalLight(102, 50, 126, 1, 0, 0);
		  directionalLight(51, 50, 102, 0, 1, 0);
		  directionalLight(51, 50, 102, 0, 0, 1);
		  		 
		  translate(width/2.f,height/2.f,-1*height/2.f);
		  this.strokeWeight(1);
		  stroke(150,150,150);
		  
		  this.mesh.draw();
		}
		
		public void keyPressed(){
			  switch(key) {
			    case('s'):case('S'): ; 
			    break;
			    
			    case('c'):this.shortestPath=(this.shortestPath+1)%this.nMethods; 
			    this.updatedMethod();
			    break;
			  }
		}
		
		/**
		 * For running the PApplet as Java application
		 */
		public static void main(String args[]) {
			//PApplet pa=new MeshViewer();
			//pa.setSize(400, 400);
			PApplet.main(new String[] { "MeshViewer" });
		}
		
}
