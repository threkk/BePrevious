package nl.hva.boxlabapp.gdx;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public interface Exercise3DHandler {
	Quaternion[] getRotation();
	Vector3[] getTranslation();
	boolean initSensors();
	boolean isConnected();
	void disconnect();
}
