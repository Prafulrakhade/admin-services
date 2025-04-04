package io.mosip.admin.bulkdataupload.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * Entity for Machine Details
 * 
 * @author Megha Tanga
 * @since 1.0.1
 *
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "machine_master", schema = "master")
public class Machine extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	@Id
	@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false, length = 10))
	private String id;
	
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

	/**
	 * Field for machine name
	 */
	@Column(name = "name", nullable = false, length = 64)
	private String name;

	/**
	 * Field for machine serial number
	 */
	@Column(name = "serial_num", nullable = true, length = 64)
	private String serialNum;

	/**
	 * Field for machine ip address
	 */
	@Column(name = "ip_address", length = 17)
	private String ipAddress;
	/**
	 * Field for machine mac address
	 */
	@Column(name = "mac_address", nullable = true, length = 64)
	private String macAddress;

	@Column(name = "regcntr_id", length = 10)
	private String regCenterId;

	/**
	 * Field for machine specific id
	 */
	@Column(name = "mspec_id", nullable = false, length = 36)
	private String machineSpecId;

	/**
	 * Field for validity end Date and Time for machine
	 */
	@Column(name = "validity_end_dtimes")
	private LocalDateTime validityDateTime;

	@Column(name = "zone_code", length = 36)
	private String zoneCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "mspec_id", referencedColumnName = "id", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private MachineSpecification machineSpecification;

	@Transient
	private String mapStatus;
	
	/**
	 * Field for reg machine public key
	 */
	@Column(name = "public_key", nullable = true, length = 1024)
	private String publicKey;
	
	/**
	 * Field for reg machine public key SHA256 hash
	 */
	@Column(name = "key_index", nullable = true)
	private String keyIndex;

	/**
	 * Field for reg machine signature verification public key
	 */
	@Column(name = "sign_public_key", nullable = true, length = 1024)
	private String signPublicKey;

	/**
	 * Field for reg machine signature verification public key SHA256 hash
	 */
	@Column(name = "sign_key_index", nullable = true)
	private String signKeyIndex;
}
