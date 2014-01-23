package nl.hva.boxlabapp.gdx;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class LegController {

	private ModelInstance[] leg;
	private Vector3 pThigh, pShin;

	public LegController(ModelInstance[] leg) {
		this.leg = leg;
		this.pThigh = new Vector3(0,7f,0);
		this.pShin = new Vector3(0,2.65f,0);
	}

	public void updateChest(Vector3 acceleration, Quaternion rotation, float time) {
		this.leg[0].transform.set(new Vector3(), rotation, new Vector3(1,1,1));
	}

	public void updateThigh(Vector3 acceleration, Quaternion rotation, float time) {
		Vector3 increment = acceleration.scl(time*time*2);
		pThigh = pThigh.add(increment);
		this.leg[1].transform.set(pThigh, rotation, new Vector3(1,1,1));
	}

	public void updateShin(Vector3 acceleration, Quaternion rotation, float time) {
		Vector3 increment = acceleration.scl(time*time*2);
		pShin = pShin.add(increment);
		this.leg[2].transform.set(pShin, rotation, new Vector3(1,1,1));
	}

}
