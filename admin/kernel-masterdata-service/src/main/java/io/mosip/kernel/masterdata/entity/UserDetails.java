package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_detail", schema = "master")
public class UserDetails extends BaseEntity implements Serializable {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = -8541947587557590379L;

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	private String id;

	@Column(name = "lang_code", length = 3)
	private String langCode;


	@Column(name = "name", length = 64)
	private String name;

	@Column(name = "status_code", length = 36)
	private String statusCode;

	@Column(name = "last_login_dtimes")
	private LocalDateTime lastLoginDateTime;

	@Column(name = "last_login_method", length = 64)
	private String lastLoginMethod;
	
	@Column(name = "regcntr_id", length = 10)
	private String regCenterId;

}
