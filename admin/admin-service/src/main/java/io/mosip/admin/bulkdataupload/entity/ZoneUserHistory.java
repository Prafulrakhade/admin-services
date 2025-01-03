package io.mosip.admin.bulkdataupload.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import io.mosip.admin.bulkdataupload.entity.id.ZoneUserHistoryId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(ZoneUserHistoryId.class)
@Table(name = "zone_user_h", schema = "master")
public class ZoneUserHistory extends BaseEntity implements Serializable {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -8194849518681293756L;

	@Id
	@Column(name = "zone_code", nullable = false, length = 36)
	private String zoneCode;

	@Id
	@Column(name = "usr_id", nullable = false, length = 256)
	private String userId;
	
	@Id
	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effDTimes;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
}
