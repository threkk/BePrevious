package nl.hva.boxlabapp.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;


public class LegRenderer {
	
	private ModelInstance hip, thigh, shin, foot;
	private ModelInstance thigh2, shin2, foot2;
    private PerspectiveCamera cam;
    private Environment environment;
    private ModelBatch modelBatch;
	
	public LegRenderer(LegModel leg) {
				
		this.hip = new ModelInstance(leg.getHip());
		this.thigh = new ModelInstance(leg.getThigh());
		this.shin = new ModelInstance(leg.getShin());
		this.foot = new ModelInstance(leg.getFoot());
		
		this.thigh2 = new ModelInstance(leg.getThigh());
		this.shin2 = new ModelInstance(leg.getShin());
		this.foot2 = new ModelInstance(leg.getFoot());
		
		this.modelBatch = new ModelBatch();
		
		// Camera
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 0.1f;
        cam.far = 900f;
        cam.update();
        
        // Environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	
        // Initial position
        this.setInitialPosition();
	}
	
	
	public void render(){
		modelBatch.begin(cam);
		modelBatch.render(hip, environment);
		modelBatch.render(thigh, environment);
		modelBatch.render(shin, environment);
		modelBatch.render(foot, environment);
		modelBatch.render(thigh2, environment);
		modelBatch.render(shin2, environment);
		modelBatch.render(foot2, environment);
		modelBatch.end();
	}
	
	public void dispose(){
		modelBatch.dispose();
	}
	
	public Camera getCamera(){
		return cam;
	}
	
	public ModelInstance[] getInstance(){
		ModelInstance[] leg = {hip, thigh, shin, foot};
		return leg;
	}
	
	public void setInitialPosition(){

        hip.transform.translate(0, 10.25f, 1);
        thigh.transform.translate(0, 7f, 0);
        shin.transform.translate(0, 2.65f, 0);
        foot.transform.translate(-0.75f, 0, 0);
		
        thigh2.transform.translate(0, 7f, 2);
        shin2.transform.translate(0, 2.65f, 2);
        foot2.transform.translate(-0.75f, 0, 2);
        
//		hip = mb.createBox(3f, 2f, 1.5f, blue, attributes);
//		thigh = mb.createBox(1f, 4.5f, 1f, red, attributes);
//		shin = mb.createBox(1f, 4.25f, 1f, green, attributes);
//		foot = mb.createBox(1f, 1f, 2.5f, yellow, attributes);
	}
}
