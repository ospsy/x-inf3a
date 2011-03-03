package rendu;


import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.universe.SimpleUniverse;

abstract public class scene3d extends Canvas3D {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SimpleUniverse simpleU;
	protected BranchGroup BGprincipal;
//	protected TransformGroup viewTG; //pour camera deux axes : root -> viewTG0 -> viewTG -> BGprincipal
//	private TransformGroup viewTG0;
	private TransformGroup view;
	protected BoundingSphere bounds;
	public scene3d() {
		super(SimpleUniverse.getPreferredConfiguration());
		init();
	}
	

	public abstract void createScene();
	
	/**
	 * établi le contrôle de la caméra à la souris 
	 * @param view
	 */
	public abstract void setLinkCameraMouse(TransformGroup view) ;


	public void init(){
		bounds = new BoundingSphere(new Point3d(),1000.0);
		simpleU = new SimpleUniverse(this);
		simpleU.getViewingPlatform().setNominalViewingTransform();
		
		BGprincipal = new BranchGroup();
		BranchGroup root = new BranchGroup();
		BGprincipal.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		BGprincipal.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
			
		
		
		//camera
		view = simpleU.getViewingPlatform().getViewPlatformTransform();
		setLinkCameraMouse(view);
		
		
//		
//		viewTG = new TransformGroup();
//		viewTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//		viewTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//		viewTG0 = new TransformGroup();
//		viewTG0.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//		viewTG0.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//		setLinkCameraMouse(viewTG, viewTG0);
//		viewTG0.addChild(viewTG);
//		viewTG.addChild(BGprincipal);
		root.addChild(BGprincipal);
		
		/**
		 * doit creer la scène en attachant les éléments à BGprincipal
		 */
		createScene();
		simpleU.addBranchGraph(root);
		
	}
	
	public void destroy(){
		simpleU.removeAllLocales();
		simpleU.cleanup();
		simpleU = null;
	}



	
	
}
