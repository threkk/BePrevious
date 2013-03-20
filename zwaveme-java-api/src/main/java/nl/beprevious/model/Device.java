package nl.beprevious.model;

public interface Device {

	public int getID();

	public String getName();

	public void setName(String name);

	public void executeCommand(Command command);
}
