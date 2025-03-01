package dev.faun.iridiumskyblocknpc.configuration.beans;

public class SignEditorBean {
	private String title;
	private String line1;
	private String line2;
	private String line3;

	public SignEditorBean(String title, String line1, String line2,
			String line3) {
		this.title = title;
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
	}

	public SignEditorBean() {
		this.title = "";
		this.line1 = "";
		this.line2 = "";
		this.line3 = "";
	}

	public String getTitle() {
		return title;
	}

	public String getLine1() {
		return line1;
	}

	public String getLine2() {
		return line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}
}