package sk.mkrajcovic.challenges.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.model.Authority.AuthorityId;

@Entity
@Table(name = "authorities")
@IdClass(AuthorityId.class) // handles the default composite key structure
@NoArgsConstructor
public class Authority {

	@Id
	@ManyToOne
	@JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
	private User user;

	@Id
	@Column(name = "authority", length = 50, nullable = false)
	private String role;

	public Authority(User user, String role) {
		this.user = user;
		this.role = role;
	}

	// composite key class required for the default schema
	@SuppressWarnings("serial")
	@EqualsAndHashCode(callSuper = false)
	@NoArgsConstructor
	static class AuthorityId implements Serializable {
		private String user;
		private String role;
	}
}
