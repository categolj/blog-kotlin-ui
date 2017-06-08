package am.ik.blog.point;

public class LoginRequiredException extends RuntimeException {
	private final Long entryId;

	public LoginRequiredException(Long entryId) {
		this.entryId = entryId;
	}

	public Long entryId() {
		return entryId;
	}
}
