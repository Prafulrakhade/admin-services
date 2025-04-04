package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional;

import io.mosip.kernel.masterdata.dto.response.FilterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.TitleErrorCode;
import io.mosip.kernel.masterdata.dto.TitleDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.TitleResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.TitleExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Title;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.TitleRepository;
import io.mosip.kernel.masterdata.service.TitleService;
import io.mosip.kernel.masterdata.utils.AuditUtil;
import io.mosip.kernel.masterdata.utils.EventPublisherUtil;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataCreationUtil;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;
import io.mosip.kernel.websub.api.exception.WebSubClientException;

/**
 * Implementing service class for fetching titles from master db
 * 
 * @author Sidhant Agarwal
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Service
public class TitleServiceImpl implements TitleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TitleServiceImpl.class);

	@Autowired
	private TitleRepository titleRepository;

	@Autowired
	private FilterTypeValidator filterTypeValidator;

	@Autowired
	private MasterdataSearchHelper masterDataSearchHelper;

	@Autowired
	private FilterColumnValidator filterColumnValidator;

	@Autowired
	private MasterDataFilterHelper masterDataFilterHelper;

	@Autowired
	private PageUtils pageUtils;

	@Autowired
	private AuditUtil auditUtil;

	@Autowired
	private MasterdataCreationUtil masterdataCreationUtil;

	@Value("${mosip.kernel.masterdata.title_event:MASTERDATA_TITLES}")
	private String topic;

	@Value("${websub.publish.url}")
	private String hubURL;

	@Autowired
	private PublisherClient<String, EventModel, HttpHeaders> publisher;

	@Scheduled(fixedDelayString = "${masterdata.websub.resubscription.delay.millis}",
			initialDelayString = "${masterdata.subscriptions-delay-on-startup}")
	public void subscribeTopics() {
		try {
			publisher.registerTopic(topic, hubURL);
		} catch (WebSubClientException exception) {
			LOGGER.warn(exception.getMessage());
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TitleService#getAllTitles()
	 */
	@Cacheable(value = "titles", key = "'title'")
	@Override
	public TitleResponseDto getAllTitles() {
		TitleResponseDto titleResponseDto = null;
		List<TitleDto> titleDto = null;
		List<Title> titles = null;
		try {
			titles = titleRepository.findAll(Title.class);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		if (titles != null && !titles.isEmpty()) {
			titleDto = MapperUtils.mapAll(titles, TitleDto.class);
		} else {
			throw new DataNotFoundException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
					TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
		}
		titleResponseDto = new TitleResponseDto();
		titleResponseDto.setTitleList(titleDto);
		return titleResponseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#getByLanguageCode(java.lang.
	 * String)
	 */
	@Cacheable(value = "titles", key = "'title'.concat('-').concat(#languageCode)", condition = "#languageCode != null")
	@Override
	public TitleResponseDto getByLanguageCode(String languageCode) {
		TitleResponseDto titleResponseDto = null;
		List<TitleDto> titleDto = null;
		List<Title> title = null;

		try {
			title = titleRepository.getThroughLanguageCode(languageCode);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		if (title.isEmpty()) {
			throw new DataNotFoundException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
					TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
		}
		titleDto = MapperUtils.mapAll(title, TitleDto.class);

		titleResponseDto = new TitleResponseDto();
		titleResponseDto.setTitleList(titleDto);

		return titleResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#saveTitle(io.mosip.kernel.
	 * masterdata.dto.RequestDto)
	 */
	@CacheEvict(value = "titles", allEntries = true)
	@Override
	public CodeAndLanguageCodeID saveTitle(TitleDto titleRequestDto) {

		Title title;
		try {
			titleRequestDto = masterdataCreationUtil.createMasterData(Title.class, titleRequestDto);
			Title entity = MetaDataUtils.setCreateMetaData(titleRequestDto, Title.class);
			title = titleRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException | IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			auditUtil.auditRequest(String.format(MasterDataConstant.CREATE_ERROR_AUDIT, TitleDto.class.getSimpleName()),
					MasterDataConstant.AUDIT_SYSTEM, String.format(MasterDataConstant.FAILURE_DESC,
							TitleErrorCode.TITLE_INSERT_EXCEPTION.getErrorCode(), ExceptionUtils.parseException(e)),
					"ADM-819");
			throw new MasterDataServiceException(TitleErrorCode.TITLE_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		MapperUtils.map(title, codeLangCodeId);
		auditUtil.auditRequest(String.format(MasterDataConstant.SUCCESSFUL_CREATE, TitleDto.class.getSimpleName()),
				MasterDataConstant.AUDIT_SYSTEM, String.format(MasterDataConstant.SUCCESSFUL_CREATE_DESC,
						TitleDto.class.getSimpleName(), codeLangCodeId.getCode()),
				"ADM-820");
		return codeLangCodeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#updateTitle(io.mosip.kernel.
	 * masterdata.dto.RequestDto)
	 */
	@CacheEvict(value = "titles", allEntries = true)
	@Override
	public CodeAndLanguageCodeID updateTitle(TitleDto titles) {

		TitleDto titleDto = titles;

		CodeAndLanguageCodeID titleId = new CodeAndLanguageCodeID();

		MapperUtils.mapFieldValues(titleDto, titleId);
		try {

			Title title = titleRepository.findById(Title.class, titleId);

			if (title != null) {
				titles = masterdataCreationUtil.updateMasterData(Title.class, titles);
				MetaDataUtils.setUpdateMetaData(titleDto, title, false);
				titleRepository.update(title);
				if (!titles.getIsActive()) {
					masterdataCreationUtil.updateMasterDataDeactivate(Title.class, titles.getCode());
				}
				EventModel eventModel = EventPublisherUtil.populateEventModel(titles, MasterDataConstant.PUBLISHER_ID, topic,"titles");
				publisher.publishUpdate(topic, eventModel, MediaType.APPLICATION_JSON_UTF8_VALUE, null, hubURL);
			} else {
				auditUtil.auditRequest(String.format(MasterDataConstant.FAILURE_UPDATE, TitleDto.class.getSimpleName()),
						MasterDataConstant.AUDIT_SYSTEM,
						String.format(MasterDataConstant.FAILURE_DESC, TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
								TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage()),
						"ADM-821");
				throw new RequestException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
						TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
			}

		} catch (WebSubClientException exception) {
			LOGGER.warn(exception.getMessage());
		} catch (DataAccessLayerException | DataAccessException | IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			auditUtil.auditRequest(String.format(MasterDataConstant.FAILURE_UPDATE, TitleDto.class.getSimpleName()),
					MasterDataConstant.AUDIT_SYSTEM,
					String.format(MasterDataConstant.FAILURE_DESC, TitleErrorCode.TITLE_UPDATE_EXCEPTION.getErrorCode(),
							TitleErrorCode.TITLE_UPDATE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e)),
					"ADM-822");
			throw new MasterDataServiceException(TitleErrorCode.TITLE_UPDATE_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_UPDATE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		auditUtil.auditRequest(String.format(MasterDataConstant.SUCCESSFUL_UPDATE, TitleDto.class.getSimpleName()),
				MasterDataConstant.AUDIT_SYSTEM, String.format(MasterDataConstant.SUCCESSFUL_UPDATE_DESC,
						TitleDto.class.getSimpleName(), titleId.getCode()),
				"ADM-823");
		return titleId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#deleteTitle(java.lang.String,
	 * java.lang.String)
	 */
	@CacheEvict(value = "titles", allEntries = true)
	@Override
	@Transactional
	public CodeResponseDto deleteTitle(String code) {
		try {

			final List<Title> titleList = titleRepository.findByCode(code);

			if (!titleList.isEmpty()) {
				titleList.stream().map(MetaDataUtils::setDeleteMetaData).forEach(titleRepository::update);
			} else {
				throw new RequestException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
						TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_DELETE_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_DELETE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		CodeResponseDto responseDto = new CodeResponseDto();
		responseDto.setCode(code);
		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TitleService#getTitles(int, int,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<TitleExtnDto> getTitles(int pageNumber, int pageSize, String sortBy, String orderBy) {
		List<TitleExtnDto> titles = null;
		PageDto<TitleExtnDto> pageDto = null;
		try {
			Page<Title> pageData = titleRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				titles = MapperUtils.mapAll(pageData.getContent(), TitleExtnDto.class);
				pageDto = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(), pageData.getTotalElements(),
						titles);
			} else {
				throw new DataNotFoundException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
						TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		return pageDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#searchTitles(io.mosip.kernel.
	 * masterdata.dto.request.SearchDto)
	 */
	@SuppressWarnings("null")
	@Override
	public PageResponseDto<TitleExtnDto> searchTitles(SearchDto searchDto) {
		PageResponseDto<TitleExtnDto> pageDto = new PageResponseDto<>();
		List<TitleExtnDto> titles = null;

		if (filterTypeValidator.validate(TitleExtnDto.class, searchDto.getFilters())) {
			Pagination pagination = searchDto.getPagination();
			List<SearchSort> sort = searchDto.getSort();
			searchDto.setPagination(new Pagination(0, Integer.MAX_VALUE));
			searchDto.setSort(Collections.emptyList());
			pageUtils.validateSortField(Title.class, sort);
			Page<Title> page = masterDataSearchHelper.searchMasterdata(Title.class, searchDto, null);
			if (page.getContent() != null && !page.getContent().isEmpty()) {
				titles = MapperUtils.mapAll(page.getContent(), TitleExtnDto.class);
				pageDto = pageUtils.sortPage(titles, sort, pagination);
			}

		}
		return pageDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TemplateService#filterTemplates(io.mosip.
	 * kernel.masterdata.dto.request.FilterValueDto)
	 */
	@Override
	public FilterResponseDto filterTitles(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();

		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters(), Title.class)) {
			filterValueDto.getFilters().stream().forEach(filter -> {
				FilterResult<String> filterResult = masterDataFilterHelper.filterValues(Title.class, filter, filterValueDto);
				filterResult.getFilterData().forEach(filteredValue -> {
					if (filteredValue != null) {
						ColumnValue columnValue = new ColumnValue();
						columnValue.setFieldID(filter.getColumnName());
						columnValue.setFieldValue(filteredValue);
						columnValueList.add(columnValue);
					}
				});
				filterResponseDto.setTotalCount(filterResult.getTotalCount());
			});
			filterResponseDto.setFilters(columnValueList);
		}
		return filterResponseDto;
	}

}
