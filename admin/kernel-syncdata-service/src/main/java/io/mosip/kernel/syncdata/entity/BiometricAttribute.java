package io.mosip.kernel.syncdata.entity;

import io.mosip.kernel.syncdata.entity.id.CodeAndLanguageCodeID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "biometric_attribute", schema = "master")
@IdClass(CodeAndLanguageCodeID.class)
public class BiometricAttribute extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1302520630931339354L;
	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, length = 36)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3)) })
	private String code;
	private String langCode;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "descr", length = 128)
	private String description;

	@Column(name = "bmtyp_code", length = 36, nullable = false)
	private String biometricTypeCode;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "bmtyp_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private BiometricType biometricType;

}
