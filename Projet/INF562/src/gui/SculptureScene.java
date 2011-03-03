package gui;

import java.awt.BorderLayout;

import javax.media.j3d.Canvas3D;

public class SculptureScene extends Screen {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Canvas3D scene;
	
	
	public SculptureScene(){
		scene = new rendu.Sculpture();
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER,scene);
		
	}



	
	
}
