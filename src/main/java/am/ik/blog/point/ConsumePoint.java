package am.ik.blog.point;

public class ConsumePoint {
	private Long entryId;
	private Integer amount;
	private String username;

	public Long getEntryId() {
		return entryId;
	}

	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "ConsumePoint{" + "entryId=" + entryId + ", amount=" + amount
				+ ", username='" + username + '\'' + '}';
	}
}
