package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class ShipController {
	
	// Shoddy work
	private ModelInstance ship;
	
	public ShipController(ModelInstance ship) {
		this.ship = ship;
	}
	
	public void moveShip(int... position){
		ship.transform.setTranslation(new Vector3(position[0],position[1],position[2]));
	}

}
