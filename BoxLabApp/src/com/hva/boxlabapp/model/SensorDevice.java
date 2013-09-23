package com.hva.boxlabapp.model;

import java.io.Serializable;

public class SensorDevice implements Serializable {

	private static final long serialVersionUID = -1530177624377206467L;

	public enum Type {
		SHIMMER_2R(1, "Shimmer sensor node model 2R");

		int id;
		String description;

		private Type(int id, String description) {
			this.id = id;
			this.description = description;
		}

		public int getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

		public static Type valueOf(int id) {
			for (Type type : values()) {
				if (type.id == id) {
					return type;
				}
			}
			return null;
		}
	}

	private long id;
	private String name;
	private Type type;

	public SensorDevice() {

	}

	public SensorDevice(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	public SensorDevice(long id, String name, Type type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorDevice other = (SensorDevice) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SensorDevice [id=" + id + ", name=" + name + ", type=" + type
				+ "]";
	}
}
