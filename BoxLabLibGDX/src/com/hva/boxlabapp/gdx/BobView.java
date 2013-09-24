package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.math.Vector2;

public class BobView {

	private Bob bob;
	
	public BobView() {
		bob = new Bob(new Vector2(7,2));
	}
	
	public Bob getBob(){
		return bob;
	}
	
}
