package cz.tul.app;

public class ContactDTO {
	public long id;
	public String name;
	public String phone;
	public String email;

	public ContactDTO(long id, String name) {
		this.id = id;
		this.name = name;
	}
}
