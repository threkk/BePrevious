package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.math.Vector2;

public class BobController {
	
	// Shoddy work
	private Bob bob;
	
	public BobController(BobView view) {
		this.bob = view.getBob();
	}
	
	public void moveBob(int x){
		bob.setPosition(bob.getPosition().add(new Vector2(x,0)));
	}

}
