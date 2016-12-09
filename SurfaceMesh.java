import Jcg.geometry.*;
import Jcg.polyhedron.*;


/**
 * 
 * Class for rendering a surface triangle mesh
 * @author Luca Castelli Aleardi (INF555, 2012)
 *
 */
public class SurfaceMesh {
	
	double scaleFactor=50; // scaling factor: useful for 3d rendering
	MeshViewer view;
	public Polyhedron_3<Point_3> polyhedron3D; // triangle mesh
	
	/**
	 * Create a surface mesh from an OFF file
	 */	
	public SurfaceMesh(MeshViewer view, String filename) {
		this.view=view;

		// shared vertex representation of the mesh
    	SharedVertexRepresentation sharedVertex=new SharedVertexRepresentation(filename);
    	LoadMesh<Point_3> load3D=new LoadMesh<Point_3>();
    	
    	polyhedron3D=load3D.createTriangleMesh(sharedVertex.points,sharedVertex.faceDegrees,
				sharedVertex.faces,sharedVertex.sizeHalfedges);

    	//System.out.println(polyhedron3D.verticesToString());   	
    	//System.out.println(polyhedron3D.facesToString());
    	polyhedron3D.isValid(false);
    	    	
    	this.scaleFactor=this.computeScaleFactor();
	}
	
	/**
	 * Draw a segment between two points
	 */	
	public void drawSegment(Point_3 p, Point_3 q) {
		float s=(float)this.scaleFactor;
		this.view.line(	(float)p.getX().doubleValue()*s, (float)p.getY().doubleValue()*s, 
				(float)p.getZ().doubleValue()*s, (float)q.getX().doubleValue()*s, 
				(float)q.getY().doubleValue()*s, (float)q.getZ().doubleValue()*s);
	}

	/**
	 * Draw a triangle face
	 */	
	public void drawTriangle(Point_3 p, Point_3 q, Point_3 r) {
		float s=(float)this.scaleFactor;
		view.vertex( (float)(p.getX().doubleValue()*s), (float)(p.getY().doubleValue()*s), (float)(p.getZ().doubleValue()*s));
		view.vertex( (float)(q.getX().doubleValue()*s), (float)(q.getY().doubleValue()*s), (float)(q.getZ().doubleValue()*s));
		view.vertex( (float)(r.getX().doubleValue()*s), (float)(r.getY().doubleValue()*s), (float)(r.getZ().doubleValue()*s));
	}

	
	/**
	 * Draw the entire mesh
	 */
	public void draw() {
		this.drawAxis();
		
		view.beginShape(view.TRIANGLES);
		for(Face<Point_3> f: this.polyhedron3D.facets) {
			Halfedge<Point_3> e=f.getEdge();
			Point_3 p=e.vertex.getPoint();
			Point_3 q=e.getNext().vertex.getPoint();
			Point_3 r=e.getNext().getNext().vertex.getPoint();
			
			view.noStroke();
			view.fill(200,200,200,255); // color of the triangle
			this.drawTriangle(p, q, r); // draw a triangle face
		}
		view.endShape();
		
		view.strokeWeight(2); // line width (for edges)
		view.stroke(20);
		for(Halfedge<Point_3> e: this.polyhedron3D.halfedges) {
			Point_3 p=e.vertex.getPoint();
			Point_3 q=e.opposite.vertex.getPoint();
			
			if(e.vertex.tag==2&&e.opposite.vertex.tag==2){
				this.view.stroke(200);
				this.drawSegment(p, q);
				this.view.stroke(20);
			}
			
			else {
				this.view.strokeWeight(1);
				this.drawSegment(p, q);
				this.view.strokeWeight(2); // draw edge (p,q)
			}
		}
		
		//the backtrack
		/*ExactAlgorithm E = new ExactAlgorithm(this.polyhedron3D);
    	Vertex<Point_3> source = this.polyhedron3D.vertices.get(0);
    	E.Geodesics(source);
    	System.out.println("geodesics computed\n\n");
    	BackTrack wayBack = new BackTrack(E.polyhedron3D, source, E.T);
    	wayBack.traceRay(0.5, E.polyhedron3D.halfedges.get(1));
    	int n = wayBack.result.size();
    	for (int i=0; i<n-1; i++){
    		this.view.stroke(200);
    		drawSegment(wayBack.result.get(i), wayBack.result.get(i+1));
    		this.view.stroke(20);
    	}*/
		
		view.strokeWeight(1);
	}
	
	/**
	 * Draw the X, Y and Z axis
	 */
	public void drawAxis() {
		double s=1;
		Point_3 p000=new Point_3(0., 0., 0.);
		Point_3 p100=new Point_3(s, 0., 0.);
		Point_3 p010=new Point_3(0.,s, 0.);
		Point_3 p011=new Point_3(0., 0., s);
		
		drawSegment(p000, p100);
		drawSegment(p000, p010);
		drawSegment(p000, p011);
	}


	/**
	 * Return the value after truncation
	 */
	public static double round(double x, int precision) {
		return ((int)(x*precision)/(double)precision);
	}
	
	/**
	 * Compute the scale factor (depending on the max distance of the point set)
	 */
	public double computeScaleFactor() {
		if(this.polyhedron3D==null || this.polyhedron3D.vertices.size()<1)
			return 1;
		double maxDistance=0.;
		Point_3 origin=new Point_3(0., 0., 0.);
		for(Vertex<Point_3> v: this.polyhedron3D.vertices) {
			double distance=Math.sqrt(v.getPoint().squareDistance(origin).doubleValue());
			maxDistance=Math.max(maxDistance, distance);
		}
		return Math.sqrt(3)/maxDistance*150;
	}
	
}
