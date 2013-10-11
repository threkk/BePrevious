package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class LegController {
	
	// NEEDS TONS OF TESTING!!
	// At first glance this is how it should work with the 
	// input I think it gets from the sensors. Until we make
	// some real testing with real values, we can consider this
	// as the controller.
	
	private ModelInstance[] leg;
	
	public LegController(ModelInstance[] leg) {
		this.leg = leg;
	}
	
	public void translateHip(Vector3 vector){
		this.leg[0].transform.trn(vector);
	}
	
	public void translateThigh(Vector3 vector){
		this.leg[1].transform.trn(vector);
	}

	public void translateShin(Vector3 vector){
		this.leg[2].transform.trn(vector);
	}
	
	public void translateFoot(Vector3 vector){
		this.leg[3].transform.trn(vector);
	}
	
	// Not sure about rotation
	// The API or the source code don't give much information about
	// how its inner work.
	public void rotateHip(Quaternion rotation){
		this.leg[0].transform.rotate(rotation);
	}
	
	public void rotateThigh(Quaternion rotation){
		this.leg[1].transform.rotate(rotation);
	}
	
	public void rotateShin(Quaternion rotation){
		this.leg[2].transform.rotate(rotation);
	}
	
	public void rotateFoot(Quaternion rotation){
		this.leg[3].transform.rotate(rotation);
	}
	
	public void translateAndRotateHip(Vector3 vector, Quaternion rotation){
		this.translateHip(vector);
		this.rotateHip(rotation);
	}
	
	public void translateAndRotateThigh(Vector3 vector, Quaternion rotation){
		this.translateThigh(vector);
		this.rotateThigh(rotation);
	}
	
	public void translateAndRotateShin(Vector3 vector, Quaternion rotation){
		this.translateShin(vector);
		this.rotateShin(rotation);
	}
	
	public void translateAndRotateFoot(Vector3 vector, Quaternion rotation){
		this.translateFoot(vector);
		this.rotateFoot(rotation);
	}
}
