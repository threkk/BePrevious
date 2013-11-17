package nl.boxlab.model;

public class ExerciseNote {

	private Integer id;
	private String message;
	private boolean read;

	
	public ExerciseNote(String message) {
	    this.message = message;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
	
	
}
