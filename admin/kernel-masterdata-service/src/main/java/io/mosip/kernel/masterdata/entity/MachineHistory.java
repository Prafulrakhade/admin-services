
package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.IdAndEffectDateTimeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity for Machine History
 *
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "machine_master_h", schema = "master")
@IdClass(IdAndEffectDateTimeID.class)
public class MachineHistory extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	@Id
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "id", nullable = false, length = 10)),
			@AttributeOverride(name = "effectDateTime", column = @Column(name = "eff_dtimes", nullable = false)) })
	private String id;
	private LocalDateTime effectDateTime;
	
	@Column(name = "lang_code", nullable = true, length = 3)
	private String langCode;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;
	/**
	 * Field for machine name
	 */
	@Column(name = "name", nullable = false, length = 64)
	private String name;

	/**
	 * Field for machine mac address
	 */
	@Column(name = "mac_address", nullable = false, length = 64)
	private String macAddress;

	/**
	 * Field for machine serial number
	 */
	@Column(name = "serial_num", nullable = false, length = 64)
	private String serialNum;

	/**
	 * Field for machine ip address
	 */
	@Column(name = "ip_address", length = 17)
	private String ipAddress;

	/**
	 * Field for machine specific id
	 */
	@Column(name = "mspec_id", nullable = false, length = 36)
	private String machineSpecId;

	/**
	 * Field to hold effected date and time
	 */
	@Column(name = "validity_end_dtimes")
	private LocalDateTime validityDateTime;

	@Column(name = "zone_code", length = 36)
	private String zoneCode;
	
	@Column(name = "regcntr_id", length = 10)
	private String regCenterId;
	
	/**
	 * Field for reg machine public key
	 */
	@Column(name = "public_key", length=1024)
	private String publicKey;
	
	/**
	 * Field for reg machine public key SHA256 hash
	 */
	@Column(name = "key_index")
	private String keyIndex;

	/**
	 * Field for reg machine signature verification public key
	 */
	@Column(name = "sign_public_key", nullable = true, length=1024)
	private String signPublicKey;

	/**
	 * Field for reg machine signature verification public key SHA256 hash
	 */
	@Column(name = "sign_key_index", nullable = true)
	private String signKeyIndex;

}
