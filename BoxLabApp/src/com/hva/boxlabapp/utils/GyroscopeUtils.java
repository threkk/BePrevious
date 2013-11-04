package com.hva.boxlabapp.utils;

public class GyroscopeUtils {

	/**
	 * Receives the Euler angles in radians and returns the components of the quaternion.
	 * 
	 * @param x The x or phi axis in radians.
	 * @param y The y axis or theta in radians.
	 * @param z The z axis or psi in radians
	 * @return A 4 positions array which contains the x, y, z and w in this order.
	 */
	public static final float[] eulerToQuaternion(float x, float y, float z){
		float[] quaternion = new float[4];
		
		quaternion[0] = (float) ((Math.cos(x/2)*Math.cos(y/2)*Math.cos(z/2))
				+ (Math.sin(x/2)*Math.sin(y/2)*Math.sin(z/2)));
		quaternion[1] = (float) ((Math.sin(x/2)*Math.cos(y/2)*Math.cos(z/2))
				- (Math.cos(x/2)*Math.sin(y/2)*Math.sin(z/2)));
		quaternion[2] = (float) ((Math.cos(x/2)*Math.sin(y/2)*Math.cos(z/2))
				+ (Math.sin(x/2)*Math.cos(y/2)*Math.sin(z/2)));
		quaternion[3] = (float) ((Math.cos(x/2)*Math.cos(y/2)*Math.sin(z/2))
				- (Math.sin(x/2)*Math.sin(y/2)*Math.cos(z/2)));

		return quaternion;
	}
	
	/**
	 * Receives a quaternion and returns the same in Euler angles.
	 * 
	 * @param q0 First component of the quaternion, the independent term.
	 * @param q1 Second component of the quaternion, the i term.
	 * @param q2 Third component of the quaternion, the j term.
	 * @param q3 Forth component of the quaternion, th k term.
	 * @return A 3 position array which contains the x, y and z in this order.
	 */
	public static final float[] quatenionToEuler(float q0, float q1, float q2, float q3){
		float[] euler = new float[3];
		
		euler[0] = (float) Math.atan2(
				2 * (q0 * q1 + q2 * q3),
				1 - 2 * (q1 * q1 + q2 * q2)
				);
		euler[1] = (float) Math.asin(2 * (q0 * q2 - q3 * q1));
		euler[2] = (float) Math.atan2(
				2 * (q0 * q3 + q1 * q2),
				1 - 2 * (q2 * q2 + q3 * q3)
				);
		
		return euler;
	}
}
