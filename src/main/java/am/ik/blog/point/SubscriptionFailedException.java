package am.ik.blog.point;

public class SubscriptionFailedException extends RuntimeException {
	private final String detail;
	private final int statusCode;

	public SubscriptionFailedException(String message, String detail, int statusCode) {
		super(message);
		this.detail = detail;
		this.statusCode = statusCode;
	}

	public String detail() {
		return detail;
	}

	public int statusCode() {
		return statusCode;
	}
}
