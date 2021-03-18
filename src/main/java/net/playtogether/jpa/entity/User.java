package net.playtogether.jpa.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User extends NamedEntity {


	@Email
	@Column(name = "correo")
	private String correo;

	@NotNull
	@Column(name = "username")
	private String username;

	@NotNull
	@Column(name = "password")
	private String password;

	@NotNull
	@Column(name = "birthdate")
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate birthdate;

	@NotNull
	@Column(name = "phone")
	@Pattern(regexp = "[0-9]{9}")
	private String phone;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "id")
	private List<Payment> payment;

	@OneToOne(optional = true)
	private Statistic statistics;

	@Column(name = "type")
	private UserType type;

	@ManyToMany
	@JoinTable(name = "team_users", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "team_id"))
	private List<Team> teams;

}
