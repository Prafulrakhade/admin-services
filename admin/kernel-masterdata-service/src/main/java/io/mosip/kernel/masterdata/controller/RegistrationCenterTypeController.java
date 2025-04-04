package io.mosip.kernel.masterdata.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MissingDataDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypePutDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.StatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterTypeExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseCodeDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.GenericService;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;
import io.mosip.kernel.masterdata.utils.AuditUtil;
import io.mosip.kernel.masterdata.validator.CenterTypeCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class for RegistrationCenterType operations.
 * 
 * @author Sagar Mahapatra
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "RegistrationCenterType" })
@Validated
public class RegistrationCenterTypeController {

	/**
	 * Autowired reference for {@link RegistrationCenterTypeService}.
	 */
	@Autowired
	RegistrationCenterTypeService registrationCenterTypeService;

	@Autowired
	private AuditUtil auditUtil;
	
	@Autowired
	private GenericService genericService;

	/**
	 * Controller method for creating a registration center type.
	 * 
	 * @param registrationCenterTypeDto the request dto containing the data of
	 *                                  registration center type to be added.
	 * @return the response dto.
	 */
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostregistrationcentertypes())")
	@PostMapping("/registrationcentertypes")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	public ResponseWrapper<CodeAndLanguageCodeID> createRegistrationCenterType(
			@Valid @RequestBody RequestWrapper<RegistrationCenterTypeDto> registrationCenterTypeDto) {
		auditUtil.auditRequest(
				MasterDataConstant.CREATE_API_IS_CALLED + RegistrationCenterTypeDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.CREATE_API_IS_CALLED + RegistrationCenterTypeDto.class.getCanonicalName(),
				"ADM-546");
		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				registrationCenterTypeService.createRegistrationCenterType(registrationCenterTypeDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Controller method for updating a registration center type.
	 * 
	 * @param registrationCenterTypeDto the request dto containing the data of
	 *                                  registration center type to be updated.
	 * @return the response dto.
	 */
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPutregistrationcentertypes())")
	@PutMapping("/registrationcentertypes")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	public ResponseWrapper<CodeAndLanguageCodeID> updateRegistrationCenterType(
			@Valid @RequestBody RequestWrapper<RegistrationCenterTypePutDto> registrationCenterTypeDto) {
		auditUtil.auditRequest(
				MasterDataConstant.UPDATE_API_IS_CALLED + RegistrationCenterTypeDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.UPDATE_API_IS_CALLED + RegistrationCenterTypeDto.class.getCanonicalName(),
				"ADM-547");
		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				registrationCenterTypeService.updateRegistrationCenterType(registrationCenterTypeDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Controller method for deleting a registration center type.
	 * 
	 * @param code the code of the registration center type that needs to be
	 *             deleted.
	 * @return the response.
	 */
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getDeleteregistrationcentertypes())")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	@DeleteMapping("/registrationcentertypes/{code}")
	public ResponseWrapper<CodeResponseDto> deleteRegistrationCenterType(@CenterTypeCode(min =2, max = 36) @PathVariable("code") String code) {
		ResponseWrapper<CodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterTypeService.deleteRegistrationCenterType(code));
		return responseWrapper;
	}

	/**
	 * Method to get all the registration Center Types;
	 * 
	 * @return list of registration center types
	 */
	@GetMapping("/registrationcentertypes/all")
	@ResponseFilter
	//@PreAuthorize("hasAnyRole(@authorizedRoles.getGetregistrationcentertypesall())")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN','INDIVIDUAL','PRE_REGISTRATION','REGISTRATION_PROCESSOR','REGISTRATION_CLIENT')")
	@ApiOperation(value = "Retrieve all the registration center types with additional metadata", notes = "Retrieve all the registration center types with the additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of registration center types"),
			@ApiResponse(code = 500, message = "Error occured while retrieving registration center types") })
	public ResponseWrapper<PageDto<RegistrationCenterTypeExtnDto>> getAllRegistrationCenterTypes(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<RegistrationCenterTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterTypeService.getAllRegistrationCenterTypes(pageNumber, pageSize,
				sortBy, orderBy.name()));
		return responseWrapper;
	}

	/**
	 * API that returns the values required for the column filter columns.
	 * 
	 * @param request the request DTO {@link FilterResponseDto} wrapper in
	 *                {@link RequestWrapper}.
	 * @return the response i.e. the list of values for the specific filter column
	 *         name and type.
	 */
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostregistrationcentertypesfiltervalues())")
	@PostMapping("/registrationcentertypes/filtervalues")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	public ResponseWrapper<FilterResponseCodeDto> registrationCenterTypeFilterValues(
			@RequestBody @Valid RequestWrapper<FilterValueDto> request) {
		auditUtil.auditRequest(
				MasterDataConstant.FILTER_API_IS_CALLED + RegistrationCenterType.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.FILTER_API_IS_CALLED + RegistrationCenterType.class.getCanonicalName(), "ADM-548");
		ResponseWrapper<FilterResponseCodeDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(registrationCenterTypeService.registrationCenterTypeFilterValues(request.getRequest()));
		auditUtil.auditRequest(String.format(MasterDataConstant.SUCCESSFUL_FILTER, MachineDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_SEARCH_DESC, MachineDto.class.getCanonicalName()),
				"ADM-549");
		return responseWrapper;
	}

	/**
	 * Function to fetch all document types.
	 * 
	 * @return {@link RegistrationCenterTypeExtnDto} DocumentTypeResponseDto
	 */
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostregistrationcentertypessearch())")
	@PostMapping("/registrationcentertypes/search")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	public ResponseWrapper<PageResponseDto<RegistrationCenterTypeExtnDto>> searchRegistrationCenterType(
			@RequestBody @Valid RequestWrapper<SearchDto> request) {
		auditUtil.auditRequest(
				MasterDataConstant.SEARCH_API_IS_CALLED + RegistrationCenterTypeExtnDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.SEARCH_API_IS_CALLED + RegistrationCenterTypeExtnDto.class.getCanonicalName(),
				"ADM-550");
		ResponseWrapper<PageResponseDto<RegistrationCenterTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				registrationCenterTypeService.searchRegistrationCenterTypes(request.getRequest()));
		auditUtil.auditRequest(
				String.format(MasterDataConstant.SUCCESSFUL_SEARCH,
						RegistrationCenterTypeExtnDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM, String.format(MasterDataConstant.SUCCESSFUL_SEARCH_DESC,
						RegistrationCenterTypeExtnDto.class.getCanonicalName()),
				"ADM-551");
		return responseWrapper;
	}
	
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPatchregistrationcentertypes())")
	@PatchMapping("/registrationcentertypes")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	public ResponseWrapper<StatusResponseDto> updateRegistrationCenterTypeStatus(@Valid @RequestParam boolean isActive,
			@CenterTypeCode(min =2, max = 36) @RequestParam String code) {
		auditUtil.auditRequest(
				MasterDataConstant.STATUS_API_IS_CALLED + RegistrationCenterTypeDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.STATUS_API_IS_CALLED + RegistrationCenterTypeDto.class.getCanonicalName(),
				"ADM-552");
		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterTypeService.updateRegistrationCenterType(code, isActive));
		auditUtil.auditRequest(
				MasterDataConstant.STATUS_UPDATED_SUCCESS + RegistrationCenterTypeDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.STATUS_UPDATED_SUCCESS + RegistrationCenterTypeDto.class.getCanonicalName(),
				"ADM-553");
		return responseWrapper;
	}
	
	/**
	 * Function to fetch missing ids/codes in the provided language code
	 *
	 * @return List<String> list of missing ids/ codes
	 */
	@ResponseFilter
	//@PreAuthorize("hasAnyRole(@authorizedRoles.getGetregistrationcentertypesmissingidslangcode())")
	@GetMapping("/registrationcentertypes/missingids/{langcode}")
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	public ResponseWrapper<List<MissingDataDto>> getMissingRegistrationCentersTypesDetails(
			@PathVariable("langcode") String langCode, @RequestParam(required = false) String fieldName) {
		ResponseWrapper<List<MissingDataDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(genericService.getMissingData(RegistrationCenterType.class, langCode, "code", fieldName));
		return responseWrapper;
	}
}
