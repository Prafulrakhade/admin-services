package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import jakarta.persistence.*;

import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "template_type", schema = "master")
@IdClass(CodeAndLanguageCodeID.class)
public class TemplateType extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -854194758755759037L;

	@Id
	@AttributeOverrides({
		@AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, length = 36)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3))
			})
	private String code;

	@Column(name = "lang_code", length = 3)
	private String langCode;

	@Column(name = "descr", length = 256)
	private String description;
}
