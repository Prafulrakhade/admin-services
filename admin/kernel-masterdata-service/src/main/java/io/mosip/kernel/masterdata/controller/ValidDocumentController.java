package io.mosip.kernel.masterdata.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.DocCategoryAndTypeMappingResponseDto;
import io.mosip.kernel.masterdata.dto.ValidDocCategoryAndDocTypeResponseDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.ValidDocumentMapDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentCategoryTypeMappingExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.ValidDocumentExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.DocCategoryAndTypeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;
import io.mosip.kernel.masterdata.service.ValidDocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class to create and delete valid document.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@RestController
@Validated
@Api(tags = { "ValidDocument" })
public class ValidDocumentController {

	/**
	 * Reference to ValidDocumentService.
	 */
	@Autowired
	ValidDocumentService documentService;

	/**
	 * Api to create valid document.
	 * 
	 * @param document the DTO for valid document.
	 * @return ValidDocumentID.
	 */
	//@PreAuthorize("hasAnyRole('GLOBAL_ADMIN','ZONAL_ADMIN')")
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostvaliddocuments())")
	@PostMapping("/validdocuments")
	@ApiOperation(value = "Service to create valid document", notes = "Create valid document and return composite id")
	public ResponseWrapper<ValidDocumentID> createValidDocument(
			@Valid @RequestBody RequestWrapper<ValidDocumentDto> document) {

		ResponseWrapper<ValidDocumentID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.createValidDocument(document.getRequest()));
		return responseWrapper;
	}

	/**
	 * Api to delete valid docuemnt.
	 * 
	 * @param docCatCode  the document category code.
	 * @param docTypeCode the document type code.
	 * @return the PostValidDocumentResponseDto.
	 */
	//@PreAuthorize("hasAnyRole('GLOBAL_ADMIN','ZONAL_ADMIN')")
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getDeletevaliddocumentsdoccategorycode())")
	@DeleteMapping("/validdocuments/{doccategorycode}/{doctypecode}")
	@ApiOperation(value = "Service to delete valid document", notes = "Delete valid document and return composite id")
	public ResponseWrapper<DocCategoryAndTypeResponseDto> deleteValidDocuemnt(
			@PathVariable("doccategorycode") String docCatCode, @PathVariable("doctypecode") String docTypeCode) {

		ResponseWrapper<DocCategoryAndTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.deleteValidDocuemnt(docCatCode, docTypeCode));
		return responseWrapper;
	}

	/**
	 * Service to fetch all valid document categories and associated document types
	 * for a languagecode
	 * 
	 * @param langCode language in which document categories and associated document
	 *                 types should be fetch
	 * @return the valid documents
	 */
	@ResponseFilter
	//@PreAuthorize("hasAnyRole(@authorizedRoles.getGetvaliddocumentslanguagecode())")
	@GetMapping("/validdocuments/{langCode}")
	@ApiOperation(value = "Service to fetch all valid document categories and associated document types for a languagecode")
	public ResponseWrapper<ValidDocCategoryAndDocTypeResponseDto> getValidDocumentByLangCode(
			@PathVariable("langCode") String langCode) {
		ResponseWrapper<ValidDocCategoryAndDocTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.getValidDocumentByLangCode(langCode));
		return responseWrapper;
	}
	
	/**
	 * This controller method provides with all valid documents.
	 * 
	 * @param pageNumber the page number
	 * @param pageSize   the size of each page
	 * @param sortBy     the attributes by which it should be ordered
	 * @param orderBy    the order to be used
	 * 
	 * @return the response i.e. pages containing the valid documents.
	 */
	//@PreAuthorize("hasAnyRole('GLOBAL_ADMIN','ZONAL_ADMIN')")
	@ResponseFilter
	//@PreAuthorize("hasAnyRole(@authorizedRoles.getGetvaliddocumentsall())")
	@GetMapping("/validdocuments/all")
	@ApiOperation(value = "Retrieve all the document categories and associated document types with additional metadata", notes = "Retrieve all the document categories and associated document types with the additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of document categories and associated document types"),
			@ApiResponse(code = 500, message = "Error occured while retrieving document categories and associated document types") })
	public ResponseWrapper<PageDto<ValidDocumentExtnDto>> getAllValidDocument(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<ValidDocumentExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.getValidDocuments(pageNumber, pageSize, sortBy, orderBy.name()));
		return responseWrapper;
	}

	/**
	 * POST API to search document type-category mapping
	 * 
	 * @param request input from user
	 * @return dto containing mapped doc type-cat values
	 */
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostvaliddocumentssearch())")
	@PostMapping("/validdocuments/search")
	//@PreAuthorize("hasAnyRole('GLOBAL_ADMIN','ZONAL_ADMIN')")
	public ResponseWrapper<PageResponseDto<DocumentCategoryTypeMappingExtnDto>> searchValidDocument(
			@RequestBody @Valid RequestWrapper<SearchDto> request) {
		ResponseWrapper<PageResponseDto<DocumentCategoryTypeMappingExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.searchValidDocument(request.getRequest()));
		return responseWrapper;
	}

	/**
	 * POST API to filter document type-category mapping
	 * 
	 * @param request input from user
	 * @return column values of input request
	 */
	//@PreAuthorize("hasAnyRole('GLOBAL_ADMIN','ZONAL_ADMIN')")
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostvaliddocumentsfiltervalues())")
	@PostMapping("/validdocuments/filtervalues")
	public ResponseWrapper<FilterResponseDto> categoryTypeFilterValues(
			@RequestBody @Valid RequestWrapper<FilterValueDto> request) {
		ResponseWrapper<FilterResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.categoryTypeFilterValues(request.getRequest()));
		return responseWrapper;
	}

	/**
	 * Api to map Document Category to a Document Type.
	 * 
	 * @param docCatCode  the document category code.
	 * @param docTypeCode the document type code.
	 * @return the DocCategoryAndTypeMappingResponseDto.
	 */
	//@PreAuthorize("hasAnyRole('GLOBAL_ADMIN','ZONAL_ADMIN')")
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPutvaliddocumentsmapdoccategorycode())")
	@PutMapping("/validdocuments/map/{doccategorycode}/{doctypecode}")
	public ResponseWrapper<DocCategoryAndTypeMappingResponseDto> mapDocCategoryAndDocType(
			@PathVariable("doccategorycode") @NotBlank @Size(min = 1, max = 36) String docCatCode,
			@PathVariable("doctypecode") @NotBlank @Size(min = 1, max = 36) String docTypeCode) {

		ResponseWrapper<DocCategoryAndTypeMappingResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.mapDocCategoryAndDocType(docCatCode, docTypeCode));
		return responseWrapper;
	}

	/**
	 * Api to un-map Document Category from a Document Type.
	 * 
	 * @param docCatCode  the document category code.
	 * @param docTypeCode the document type code.
	 * @return the DocCategoryAndTypeMappingResponseDto.
	 */
	//@PreAuthorize("hasAnyRole('GLOBAL_ADMIN','ZONAL_ADMIN')")
	@ResponseFilter
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPutvaliddocumentsunmapdoccategorycode())")
	@PutMapping("/validdocuments/unmap/{doccategorycode}/{doctypecode}")
	public ResponseWrapper<DocCategoryAndTypeMappingResponseDto> unmapDocCategoryAndDocType(
			@PathVariable("doccategorycode") @NotBlank @Size(min = 1, max = 36) String docCatCode,
			@PathVariable("doctypecode") @NotBlank @Size(min = 1, max = 36) String docTypeCode) {

		ResponseWrapper<DocCategoryAndTypeMappingResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.unmapDocCategoryAndDocType(docCatCode, docTypeCode));
		return responseWrapper;
	}
	/**
	 * Service to fetch all valid document categories and associated document types
	 * for a docCate
	 * 
	 * @param languagecode language in which document categories and associated document
	 *                 types should be fetch
	 * @return the valid documents
	 */
	@ResponseFilter
	//@PreAuthorize("hasAnyRole(@authorizedRoles.getGetvaliddocumentsdoccategorycode())")
	@GetMapping("/validdocuments/{docCategoryCode}/{langCode}")
	@ApiOperation(value = "Service to fetch all valid document categories and associated document types for a docCategoryCode")
	public ResponseWrapper<List<ValidDocumentMapDto>> getValidDocumentByDocCategoryCode(
			@PathVariable("docCategoryCode") String docCategoryCode, @PathVariable("langCode") String langCode) {
		ResponseWrapper<List<ValidDocumentMapDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.getValidDocumentByDocCategoryCode(docCategoryCode,langCode));
		return responseWrapper;
	}
}
