import java.util.*;

import processing.core.*;
import Jcg.geometry.*;
import Jcg.polyhedron.*;

/**
 * A simple 3d viewer for visualizing surface meshes
 * 
 * @author Luca Castelli Aleardi (INF555, 2012)
 *
 */
public class MeshViewer extends PApplet {

	SurfaceMesh mesh;
	//String filename="OFF/twisted.off";
	//String filename="OFF/sphere.off";
	String filename="OFF/star.off";
	//String filename="OFF/cube.off";
	//String filename="OFF/torus_33.off";
	//String filename="OFF/tore.off";
	//String filename="OFF/tri_round_cube.off";
	//String filename="OFF/nefertiti.off";
	//String filename="OFF/sphere.off";
	//String filename="OFF/tri_triceratops.off";
	
	int shortestPath=0;
	int nMethods=3; // number of simplification methods proposed
	
	public void setup() {
		  size(800,600,P3D);
		  ArcBall arcball = new ArcBall(this);
		  
		  this.mesh=new SurfaceMesh(this, filename);
	}
	
	/*public void updatedMethod() {
		if(this.simplificationMethod==0) {
		}
	}*/

		 
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
			    case('d'):case('D'): {
			    	int count = 0;
			    	for(Vertex<Point_3> v : this.mesh.polyhedron3D.vertices){
			    		v.index=count;
			    		v.tag=0;
			    		count++;
			    	}
			    	Dijkstra D = new Dijkstra(this.mesh.polyhedron3D);
			    	Vertex<Point_3> s = this.mesh.polyhedron3D.vertices.get(0);
			    	ArrayList<Pair<Double, Vertex<Point_3>>> A = D.ShortestPaths(s);
			    	for(int i = 0; i<A.size(); i++){
			    		System.out.println("i = " + i + " " + A.get(i).first());
			    	}
			    	
			    	Vertex<Point_3> t = this.mesh.polyhedron3D.vertices.get(400);
			    	int index = t.index;
			    	do{
			    		this.mesh.polyhedron3D.vertices.get(index).tag=2;
			    		index=A.get(index).second().index;
			    	} while(index!=0);
			    };
			    break;
			    case('e'):case('E'): {
			    	int countvertices = 0;
			    	for(Vertex<Point_3> v : this.mesh.polyhedron3D.vertices){
			    		v.index=countvertices;
			    		v.tag=0;
			    		countvertices++;
			    	}
			    	
			    	/*int countedges = 0;
			    	for(Halfedge<Point_3> h : this.mesh.polyhedron3D.halfedges){
			    		h.index=countedges;
			    		h.tag=0;
			    		countedges++;
			    	}*/
			    	
			    	//for(Halfedge<Point_3> h : this.mesh.polyhedron3D.halfedges){
			    	//	System.out.println(h.index);
			    	//}
			    	
			    	ExactAlgorithm E = new ExactAlgorithm(this.mesh.polyhedron3D);
			    	Vertex<Point_3> s = this.mesh.polyhedron3D.vertices.get(0);
			    	System.out.println("/////////////computing windows/////////////");
			    	E.Geodesics(s);
			    	//TreeSet<Window> T = E.T.get(0);
			    	//for(Window w : T){
			    	//	System.out.println(w.to_string());
			    	//}
			    	System.out.println("THE END OF THE ALGORITHM");
			    	System.out.println("here are the results\n\n");
			    	for (Vertex<Point_3> v : E.polyhedron3D.vertices){
			    		Halfedge<Point_3> h = v.getHalfedge();
			    		int index = h.index;
			    		if (v.getPoint().x==-0. &&v.getPoint().y==-0. &&v.getPoint().z==-0.){
			    			System.out.print(v.getPoint().toString());
				    		System.out.println("-->"+0.0);
				    		continue;
			    		}
			    		int flag = 0;
			    		TreeSet<Window> Ti = E.T.get(index);
			    		while (Ti.isEmpty()){
			    			//System.out.println("caaca");
			    			if (flag == 0){
			    				h = h.next;
			    				flag = 1;
			    			}
			    			if (flag == 1){
			    				h = h.opposite;
			    				flag = 0;
			    			}
			    			index = h.index;
			    			Ti = E.T.get(index);
			    		}
			    		
			    		Window lastw = new Window(0., 0., 0. , 0. , 0., null, null);
			    		
			    		if (flag == 1) lastw = Ti.pollLast();
			    		else if (flag == 0) lastw = Ti.pollFirst();
			    		System.out.print(v.getPoint().toString());
			    		System.out.println("-->"+lastw.RightD());
			    		
			    	}
			    	
			    	
			    	
			    	for (Halfedge<Point_3> he : E.polyhedron3D.halfedges){
			    		//E.stickAllWindowsOnEdge(he);
			    		int i = he.index;
			    		TreeSet<Window> Tsi = E.T.get(i);
			    		for (Window wtsi : Tsi){
			    			System.out.println(wtsi.to_string());
			    		}
			    	}
			    	
			    	System.out.println("END OF RESULTS");
			    };
			    break;
			  } //end switch key
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
