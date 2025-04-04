package io.mosip.kernel.masterdata.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DevicePutReqDto;
import io.mosip.kernel.masterdata.dto.DeviceRegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.PageDto;
import io.mosip.kernel.masterdata.dto.SearchDtoWithoutLangCode;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.StatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DeviceExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.response.DeviceSearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseCodeDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.service.DeviceService;
import io.mosip.kernel.masterdata.utils.AuditUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller with api to save and get Device Details
 * 
 * @author Megha Tanga
 * @author Sidhant Agarwal
 * @author Neha Sinha
 * @since 1.0.0
 *
 */

@RestController
@RequestMapping(value = "/devices")
@Api(tags = { "Device" })
public class DeviceController {

	/**
	 * Reference to DeviceService.
	 */
	@Autowired
	private DeviceService deviceService;

	@Autowired
	private AuditUtil auditUtil;

	/**
	 * Get api to fetch a all device details based on language code
	 * 
	 * @param langCode pass language code as String
	 * 
	 * @return DeviceResponseDto all device details based on given language code
	 *         {@link DeviceResponseDto}
	 */
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN','REGISTRATION_PROCESSOR','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER')")
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getGetdeviceslanguagecode())")
	@GetMapping(value = "/{langCode}")
	@ApiOperation(value = "Retrieve all Device for the given Languge Code", notes = "Retrieve all Device for the given Languge Code")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device retrieved from database for the given Languge Code"),
			@ApiResponse(code = 404, message = "When No Device Details found for the given Languge Code"),
			@ApiResponse(code = 500, message = "While retrieving Device any error occured") })
	public ResponseWrapper<DeviceResponseDto> getDeviceLang(@PathVariable("langCode") String langCode) {

		ResponseWrapper<DeviceResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.getDeviceLangCode(langCode));
		return responseWrapper;
	}

	/**
	 * Get api to fetch a all device details based on device type and language code
	 * 
	 * @param langCode   pass language code as String
	 * 
	 * @param deviceType pass device Type id as String
	 * 
	 * @return DeviceLangCodeResponseDto all device details based on given device
	 *         type and language code {@link DeviceLangCodeResponseDto}
	 */
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN','REGISTRATION_PROCESSOR','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER')")
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getGetdeviceslanguagecodedevicetype())")
	@GetMapping(value = "/{langCode}/{deviceType}")
	@ApiOperation(value = "Retrieve all Device for the given Languge Code and Device Type", notes = "Retrieve all Device for the given Languge Code and Device Type")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device retrieved from database for the given Languge Code"),
			@ApiResponse(code = 404, message = "When No Device Details found for the given Languge Code and Device Type"),
			@ApiResponse(code = 500, message = "While retrieving Device any error occured") })
	public ResponseWrapper<DeviceLangCodeResponseDto> getDeviceLangCodeAndDeviceType(
			@PathVariable("langCode") String langCode, @PathVariable("deviceType") String deviceType) {

		ResponseWrapper<DeviceLangCodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.getDeviceLangCodeAndDeviceType(langCode, deviceType));
		return responseWrapper;
	}

	/**
	 * Post API to insert a new row of Device data
	 * 
	 * @param request
	 * 
	 * 
	 * @return ResponseWrapper<DeviceExtnDto>
	 */
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostdevices())")
	@PostMapping
	@ApiOperation(value = "Service to save Device", notes = "Saves Device and return Device id")
	@ApiResponses({ @ApiResponse(code = 201, message = "When Device successfully created"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating device any error occured") })
	public ResponseWrapper<DeviceExtnDto> createDevice(@Valid @RequestBody RequestWrapper<DeviceDto> request) {
		ResponseWrapper<DeviceExtnDto> responseWrapper = new ResponseWrapper<>();
		auditUtil.auditRequest(MasterDataConstant.CREATE_API_IS_CALLED + DeviceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.CREATE_API_IS_CALLED + DeviceDto.class.getCanonicalName(), "ADM-500");
		responseWrapper.setResponse(deviceService.createDevice(request.getRequest()));
		return responseWrapper;

	}

	/**
	 * API to update an existing row of Device data
	 * 
	 * @param RequestWrapper<DevicePutReqDto>
	 * 
	 * @return responseWrapper
	 */
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	@ResponseFilter
	@PutMapping
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPutdevices())")
	@ApiOperation(value = "Service to update Device", notes = "Update Device and return updated Device")
	@ApiResponses({ @ApiResponse(code = 200, message = "When Device updated successfully"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When Device is not found"),
			@ApiResponse(code = 500, message = "While updating device any error occured") })
	public ResponseWrapper<DeviceExtnDto> updateDevice(
			@Valid @RequestBody RequestWrapper<DevicePutReqDto> devicePutReqDto) {
		auditUtil.auditRequest(MasterDataConstant.UPDATE_API_IS_CALLED + DeviceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.UPDATE_API_IS_CALLED + DeviceDto.class.getCanonicalName(), "ADM-502");
		ResponseWrapper<DeviceExtnDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.updateDevice(devicePutReqDto.getRequest()));
		auditUtil.auditRequest(MasterDataConstant.SUCCESSFUL_UPDATE + DeviceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.SUCCESSFUL_UPDATE + DeviceDto.class.getCanonicalName(),"ADM-900");
		return responseWrapper;
	}

	/**
	 * API to delete Device
	 * 
	 * @param id The Device Id
	 * 
	 * @return {@link ResponseEntity} The id of the Device which is deleted
	 */
	@ResponseFilter
	@DeleteMapping("/{id}")
	@ApiOperation(value = "Service to delete device", notes = "Delete Device and return Device Id")
	@ApiResponses({ @ApiResponse(code = 200, message = "When Device deleted successfully"),
			@ApiResponse(code = 404, message = "When Device not found"),
			@ApiResponse(code = 500, message = "Error occurred while deleting Device") })
	public ResponseWrapper<IdResponseDto> deleteDevice(@PathVariable("id") String id) {

		ResponseWrapper<IdResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.deleteDevice(id));
		return responseWrapper;
	}

	/**
	 * 
	 * Function to fetch Device detail those are mapped with given registration Id
	 * 
	 * @param regCenterId pass registration Id as String
	 * 
	 * @return @return DeviceRegistrationCenterDto all devices details
	 *         {@link DeviceRegistrationCenterDto}
	 */
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getGetdevicesmappeddevicesregcenterid())")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN','REGISTRATION_PROCESSOR','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER')")
	@GetMapping(value = "/mappeddevices/{regCenterId}")
	@ApiOperation(value = "Retrieve all Devices which are mapped to given Registration Center Id", notes = "Retrieve all Devices which are mapped to given Registration Center Id")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device Details retrieved from database for the given Registration Center Id"),
			@ApiResponse(code = 404, message = "When No Device Details not mapped with the Given Registation Center ID"),
			@ApiResponse(code = 500, message = "While retrieving Device Detail any error occured") })
	public ResponseWrapper<PageDto<DeviceRegistrationCenterDto>> getDevicesByRegistrationCenter(
			@PathVariable("regCenterId") String regCenterId,
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page number for the requested data", defaultValue = "0") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "1") int size,
			@RequestParam(name = "orderBy", defaultValue = "cr_dtimes") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String orderBy,
			@RequestParam(name = "direction", defaultValue = "DESC") @ApiParam(value = "order the requested data based on param", defaultValue = "DESC") String direction) {

		ResponseWrapper<PageDto<DeviceRegistrationCenterDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(deviceService.getDevicesByRegistrationCenter(regCenterId, page, size, orderBy, direction));
		return responseWrapper;
	}

	/**
	 * Api to search Device based on filters provided.
	 * 
	 * @param request the request DTO.
	 * @return the pages of {@link DeviceSearchDto}.
	 */
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostdevicessearch())")
	@PostMapping(value = "/search")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	@ApiOperation(value = "Retrieve all Devices for the given Filter parameters", notes = "Retrieve all Devices for the given Filter parameters")
	public ResponseWrapper<PageResponseDto<DeviceSearchDto>> searchDevice(
			@Valid @RequestBody RequestWrapper<SearchDtoWithoutLangCode> request) {
		auditUtil.auditRequest(MasterDataConstant.SEARCH_API_IS_CALLED + DeviceDto.class.getSimpleName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.SEARCH_API_IS_CALLED + DeviceDto.class.getSimpleName(), "ADM-503");
		ResponseWrapper<PageResponseDto<DeviceSearchDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.searchDevice(request.getRequest()));
		auditUtil.auditRequest(String.format(MasterDataConstant.SUCCESSFUL_SEARCH, DeviceDto.class.getSimpleName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_SEARCH_DESC, Device.class.getSimpleName()), "ADM-504");

		return responseWrapper;
	}

	/**
	 * Api to filter Device based on column and type provided.
	 * 
	 * @param request the request DTO.
	 * @return the {@link FilterResponseDto}.
	 */
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostdevicesfiltervalues())")
	@PostMapping("/filtervalues")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	public ResponseWrapper<FilterResponseCodeDto> deviceFilterValues(
			@RequestBody @Valid RequestWrapper<FilterValueDto> request) {
		auditUtil.auditRequest(MasterDataConstant.FILTER_API_IS_CALLED + DeviceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.FILTER_API_IS_CALLED + DeviceDto.class.getCanonicalName(), "ADM-505");
		ResponseWrapper<FilterResponseCodeDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.deviceFilterValues(request.getRequest()));
		auditUtil.auditRequest(String.format(MasterDataConstant.SUCCESSFUL_FILTER, DeviceDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_SEARCH_DESC, DeviceDto.class.getCanonicalName()),
				"ADM-506");
		return responseWrapper;
	}

	/**
	 * PUT API to decommission device
	 * 
	 * @param deviceId input from user
	 * @return device ID of decommissioned device
	 */
	@ResponseFilter
	@ApiOperation(value = "Decommission Device")
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPutdevicesdecommissiondeviceid())")
	@PutMapping("/decommission/{deviceId}")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	public ResponseWrapper<IdResponseDto> decommissionDevice(@PathVariable("deviceId") String deviceId) {
		auditUtil.auditRequest(MasterDataConstant.DECOMMISION_API_CALLED + DeviceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.DECOMMISION_API_CALLED + DeviceDto.class.getCanonicalName(),"ADM-901");
		ResponseWrapper<IdResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.decommissionDevice(deviceId));
		auditUtil.auditRequest(MasterDataConstant.DECOMMISSION_SUCCESS + DeviceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.DECOMMISSION_SUCCESS_DESC + DeviceDto.class.getCanonicalName(),"ADM-902");
		return responseWrapper;
	}

	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPatchdevices())")
	@PatchMapping
	@ApiOperation(value = "Service to update Device", notes = "Update Device and return updated Device")
	public ResponseWrapper<StatusResponseDto> updateDeviceStatus(@RequestParam boolean isActive,
			@RequestParam String id) {
		auditUtil.auditRequest(MasterDataConstant.STATUS_API_IS_CALLED + DeviceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.STATUS_API_IS_CALLED + DeviceDto.class.getCanonicalName(), "ADM-507");
		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceService.updateDeviceStatus(id, isActive));
		auditUtil.auditRequest(MasterDataConstant.STATUS_UPDATED_SUCCESS + DeviceDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.STATUS_UPDATED_SUCCESS + DeviceDto.class.getCanonicalName(),"ADM-903");
		return responseWrapper;
	}

}
