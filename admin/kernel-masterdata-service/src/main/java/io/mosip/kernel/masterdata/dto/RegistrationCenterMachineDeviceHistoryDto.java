package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.StringFormatter;
import lombok.Data;

/**
 * DTO class
 * 
 * @author Srinivasan
 *
 */
@Data
public class RegistrationCenterMachineDeviceHistoryDto {

	@NotNull
	@StringFormatter(min = 1, max = 10)
	private String regCenterId;

	@NotNull
	@StringFormatter(min = 1, max = 10)
	private String machineId;

	@NotNull
	@StringFormatter(min = 1, max = 36)
	private String deviceId;

	private LocalDateTime effectiveDateTime;

	private Boolean isActive;
}
