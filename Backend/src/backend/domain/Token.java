package backend.domain;

public class Token {
	private String token;
	private User user;
	
	public Token(String token, User user) {
		this.token = token;
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public User getUser() {
		return user;
	}
}
