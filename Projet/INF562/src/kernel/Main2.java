package kernel;

import Jcg.polyhedron.MeshRepresentation;

public class Main2 {
	
	public static void main(String[] args){
		MeshRepresentation mesh = new MeshRepresentation();
		mesh.readOffFile(args[1]);
	}

}
