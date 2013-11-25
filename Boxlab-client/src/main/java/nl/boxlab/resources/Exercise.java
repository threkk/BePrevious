package nl.boxlab.resources;

public enum Exercise {

	LEG_EXTENSION(1, "Leg extensions", "html/exercise01.html"), ABDUCTION(2,
			"Abduction", "html/exercise02.html"), STRAIGHT_LEG_EXTENSION(3,
			"Straight Leg Extension", "html/exercise03.html"), KNEE_FLEXION(4,
			"Knee Flexion", "html/exercise04.html"), HIP_FLEXION(5,
			"Hip Flexion", "html/exercise05.html"), HYPER_EXTENSION_1(6,
			"Hyper Extension", "html/exercise06.html"), HYPER_EXTENSION_2(7,
			"Hyper Extension", "html/exercise07.html"), ABDUCTION_SIDEWAYS(8,
			"Lying Abduction Sideways", "html/exercise08.html"), ABDUCTION_SUPINE(
			9, "Lying Abduction Supine Position", "html/exercise09.html"), GLUTEN_TIGHTEN(
			10, "Gluten Tighten", "html/exercise10.html");

	private int id;
	private String name;
	private String htmlResource;

	private Exercise(int id, String name, String htmlResource) {
		this.id = id;
		this.name = name;
		this.htmlResource = htmlResource;
	}

	public int getId() {
		return id;
	}

	public String getHtmlResource() {
		return htmlResource;
	}

	public String getName() {
		return name;
	}

	public static Exercise valueOf(Integer exerciseId) {
		Exercise result = null;
		if (exerciseId != null) {
			for (Exercise e : values()) {
				if (e.id == exerciseId.intValue()) {
					result = e;
					break;
				}
			}
		}
		return result;
	}

	public String toString() {
		return getName();
	}
}
