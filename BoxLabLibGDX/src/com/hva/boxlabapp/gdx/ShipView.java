package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;

public class ShipView {

	private Model ship;
	
	public ShipView() {
		 ModelLoader<?> loader = new ObjLoader();
	     ship = loader.loadModel(Gdx.files.internal("ship.obj"));	}
	
	public Model getShip(){
		return ship;
	}
	
}
