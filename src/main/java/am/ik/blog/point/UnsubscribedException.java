package am.ik.blog.point;

public class UnsubscribedException extends RuntimeException {
	private final Long entryId;

	public UnsubscribedException(Long entryId) {
		this.entryId = entryId;
	}

	public Long entryId() {
		return entryId;
	}
}
