package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdAndEffectDateTimeID implements Serializable {

	private static final long serialVersionUID = 7001663925687776491L;

	@Column(name = "id", nullable = false)
	private String id;

	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectDateTime;

	//@Column(name = "lang_code", nullable = false, length = 3)
	//private String langCode;
}
