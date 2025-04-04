
package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import io.mosip.kernel.masterdata.validator.StringFormatter;
import io.mosip.kernel.masterdata.validator.ValidLangCode;
import lombok.Data;

/**
 * @author Kishan Rathore
 *
 */
@Data
public class ExceptionalHolidayDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6447129753515705510L;

	@NotNull
	private LocalDate holidayDate;

	/**
	 * Holiday day is day of week as integer value, week start from Monday , Monday
	 * is 1 and Sunday is 7
	 */
	private String holidayDay;
	/**
	 * Holiday month is month of the year as integer value.
	 */
	private String holidayMonth;
	private String holidayYear;

	@NotNull
	@StringFormatter(min = 1, max = 64)
	private String holidayName;

	@Size(min = 0, max = 128)
	private String holidayReason;

	private String registrationCenterID;

	@NotNull
	private Boolean isDeleted;

	@ValidLangCode(message = "Language Code is Invalid")
	private String langCode;

	@NotNull
	private Boolean isActive;

}
