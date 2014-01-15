package nl.hva.boxlabapp.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class LegModel {

	private final long attributes = Usage.Position | Usage.Normal;
	
	private Model hip;
	private Model thigh;
	private Model shin;
	private Model foot;
	
	public LegModel() {
		ModelBuilder mb = new ModelBuilder();
		
		Material red = new Material(ColorAttribute.createDiffuse(Color.RED));
		Material blue = new Material(ColorAttribute.createDiffuse(Color.BLUE));
		Material yellow = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
		Material green = new Material(ColorAttribute.createDiffuse(Color.GREEN));
		
		hip = mb.createBox(3f, 2f, 1.5f, blue, attributes);
		thigh = mb.createBox(1f, 4.5f, 1f, red, attributes);
		shin = mb.createBox(1f, 4.25f, 1f, green, attributes);
		foot = mb.createBox(1f, 1f, 2.5f, yellow, attributes);
	}

	public Model getHip() {
		return hip;
	}

	public Model getThigh() {
		return thigh;
	}

	public Model getShin() {
		return shin;
	}

	public Model getFoot() {
		return foot;
	}
	
	public Model[] getLeg(){
		Model[] leg = {hip, thigh, shin, foot};
		return leg;
	}
	
}
