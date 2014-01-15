package nl.hva.boxlabapp.devices;

public class Device {

	public enum Type {
		SHIMMER_2R(0, "Shimmer sensor node model 2R"),
		SHIMMER_3R(1, "Shimmer sensor node model 3R"),
		RASPBERRY(2, "Raspberry Pi server"),
		NULL(3, "No device attached yet");
		
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
		
	};
	
	public enum Position {
		CHEST(0, "Chest"),
		THIGH(1, "Thigh"),
		SHIN(2, "Shin"),
		SERVER(3, "Server");
		
		int id;
		String name;
		
		private Position(int id, String name){
			this.id = id;
			this.name = name;
		}
		
		public int getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public static Position valueOf(int id) {
			for (Position position : values()){
				if(position.id == id) {
					return position;
				}
			}
			return null;
		}
	}
	
	private long id;
	private String name;
	private Position position;
	private Type type;
	private String mac;
	
	public Device(){
		super();
	}
	
	public Device (long id, String name, int position, int type, String mac) {
		this.id = id;
		this.name = name;
		this.position = Position.valueOf(position);
		this.type = Type.valueOf(type);
		this.mac = mac;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		if(name == null) return "Unknown name";
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = Position.valueOf(position);
	}

	public Type getType() {
		if(type == null) return Type.NULL;
		return type;
	}

	public void setType(int type) {
		this.type = Type.valueOf(type);
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	public String toString() {
		return "Device [id=" + id + ", name=" + name + ", position=" + position
				+ ", type=" + type + ", mac=" + mac + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mac == null) ? 0 : mac.hashCode());
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
		Device other = (Device) obj;
		if (mac == null) {
			if (other.mac != null)
				return false;
		} else if (!mac.equals(other.mac))
			return false;
		return true;
	}
	
	
}
