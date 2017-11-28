package am.ik.blog.point;

import static java.util.Collections.emptySet;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlogPoint implements Serializable {
	public static final BlogPoint FAILURE = new BlogPoint(-1, emptySet());
	private final int point;
	private final Set<Long> entryIds;

	@JsonCreator
	public BlogPoint(@JsonProperty("point") int point,
			@JsonProperty("entryIds") Set<Long> entryIds) {
		this.point = point;
		this.entryIds = Collections.unmodifiableSet(entryIds);
	}

	public int getPoint() {
		return point;
	}

	public Set<Long> getEntryIds() {
		return entryIds;
	}
}
