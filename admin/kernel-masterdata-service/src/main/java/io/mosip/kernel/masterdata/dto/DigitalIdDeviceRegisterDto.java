package io.mosip.kernel.masterdata.dto;

import jakarta.validation.constraints.NotNull;


import io.mosip.kernel.masterdata.validator.StringFormatter;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Srinivasan
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DigitalIdDeviceRegisterDto {
	/** The serial no. */

	@NotNull
	@StringFormatter(min = 0, max = 64)
	@ApiModelProperty(value = "serialNumber", required = true, dataType = "java.lang.String")
	private String serialNo;

	/** The Device Provider Name. */

	@NotNull
	@StringFormatter(min = 0, max = 128)
	@ApiModelProperty(value = "providerName", required = true, dataType = "java.lang.String")
	private String dp;

	/** The Device Provider id. */
	@NotNull
	@StringFormatter(min = 0, max = 36)
	@ApiModelProperty(value = "providerId", required = true, dataType = "java.lang.String")
	private String dpId;

	/** The make. */
	@NotNull
	@StringFormatter(min = 0, max = 36)
	@ApiModelProperty(value = "make", required = true, dataType = "java.lang.String")
	private String make;

	/** The model. */

	@NotNull
	@StringFormatter(min = 0, max = 36)
	@ApiModelProperty(value = "model", required = true, dataType = "java.lang.String")
	private String model;

	/** type *//*
				 * @ApiModelProperty(value = "type", dataType = "java.lang.String")
				 * 
				 * @ValidType(message = "Type Value is Invalid") private String type;
				 */

	/** The date time. */
	private String dateTime;

	/**
	 * Field for deviceTypeCode
	 */

	@NotNull
	@StringFormatter(min = 0, max = 36)
	@ApiModelProperty(value = "deviceTypeCode", required = true, dataType = "java.lang.String")
	private String deviceTypeCode;

	/**
	 * Field for deviceSubTypeCode
	 */

	@NotNull
	@StringFormatter(min = 1, max = 36)
	@ApiModelProperty(value = "deviceSTypeCode", required = true, dataType = "java.lang.String")
	private String deviceSTypeCode;

}
