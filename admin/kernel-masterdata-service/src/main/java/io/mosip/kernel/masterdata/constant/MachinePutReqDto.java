package io.mosip.kernel.masterdata.constant;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.mosip.kernel.masterdata.validator.OptionalStringFormatter;
import io.mosip.kernel.masterdata.validator.StringFormatter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Request dto to create Machine
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Data // @ApiModel(value =
public class MachinePutReqDto {

	/**
	 * Field for machine id
	 */
	@NotNull
	@StringFormatter(min = 1, max = 10)
	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.String")
	private String id;
	/**
	 * Field for machine name
	 */
	@NotNull
	@StringFormatter(min = 1, max = 64)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;
	/**
	 * Field for machine serial number
	 */
	@Size(min = 0, max = 64)
	@ApiModelProperty(value = "serialNum", required = false, dataType = "java.lang.String")
	private String serialNum;

	@Deprecated
	private Boolean isActive;
	/**
	 * Field for machine mac address
	 */
	
	@OptionalStringFormatter( max = 64)
	@ApiModelProperty(value = "macAddress", required = true, dataType = "java.lang.String")
	private String macAddress;
	/**
	 * Field for machine IP address
	 */
	
	@NotNull
	@Size(max = 17)
	@ApiModelProperty(value = "ipAddress", required = true, dataType = "java.lang.String")
	private String ipAddress;
	/**
	 * Field for machine specification Id
	 */
	@NotNull
	@StringFormatter(min = 1, max = 36)
	@ApiModelProperty(value = "machineSpecId", required = true, dataType = "java.lang.String")
	private String machineSpecId;
	/**
	 * Field for language code
	 */
	@Deprecated
	private String langCode;

	/**
	 * Field for is validity of the Device
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime validityDateTime;

	@NotNull
	@StringFormatter(min = 1, max = 36)
	@ApiModelProperty(value = "zoneCode", required = true, dataType = "java.lang.String")
	private String zoneCode;
	
	@ApiModelProperty(value = "regCenterId",  dataType = "java.lang.String")
	private String regCenterId;

	@ApiModelProperty(value = "publicKey", required = false, dataType = "java.lang.String")
	private String publicKey;

	@ApiModelProperty(value = "signPublicKey", required = false, dataType = "java.lang.String")
	private String signPublicKey;

}
