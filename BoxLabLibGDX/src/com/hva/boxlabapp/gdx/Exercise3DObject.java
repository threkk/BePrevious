package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;

public class Exercise3DObject extends Game {

	@Override
	public void create() {
		// Fuck this shit.
		Texture.setEnforcePotImages(false);
		setScreen(new ModelScreen());
	}
}
