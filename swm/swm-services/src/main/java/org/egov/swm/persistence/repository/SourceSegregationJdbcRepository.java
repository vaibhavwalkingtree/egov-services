package org.egov.swm.persistence.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.swm.domain.model.CollectionDetailsSearch;
import org.egov.swm.domain.model.Pagination;
import org.egov.swm.domain.model.SourceSegregation;
import org.egov.swm.domain.model.SourceSegregationSearch;
import org.egov.swm.persistence.entity.SourceSegregationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SourceSegregationJdbcRepository extends JdbcRepository {

	public static final String TABLE_NAME = "egswm_sourcesegregation";

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public CollectionDetailsJdbcRepository collectionDetailsJdbcRepository;

	public Boolean uniqueCheck(String tenantId, String fieldName, String fieldValue, String uniqueFieldName,
			String uniqueFieldValue) {

		return uniqueCheck(TABLE_NAME, tenantId, fieldName, fieldValue, uniqueFieldName, uniqueFieldValue);
	}

	public Pagination<SourceSegregation> search(SourceSegregationSearch searchRequest) {

		String searchQuery = "select * from " + TABLE_NAME + " :condition  :orderby ";

		Map<String, Object> paramValues = new HashMap<>();
		StringBuffer params = new StringBuffer();

		if (searchRequest.getSortBy() != null && !searchRequest.getSortBy().isEmpty()) {
			validateSortByOrder(searchRequest.getSortBy());
			validateEntityFieldName(searchRequest.getSortBy(), SourceSegregationSearch.class);
		}

		String orderBy = "order by code";
		if (searchRequest.getSortBy() != null && !searchRequest.getSortBy().isEmpty()) {
			orderBy = "order by " + searchRequest.getSortBy();
		}

		if (searchRequest.getCode() != null) {
			if (params.length() > 0) {
				params.append(" and ");
			}
			params.append("code in (:code)");
			paramValues.put("code", searchRequest.getCode());
		}

		if (searchRequest.getCodes() != null) {
			if (params.length() > 0) {
				params.append(" and ");
			}
			params.append("code in (:codes)");
			paramValues.put("codes", new ArrayList<String>(Arrays.asList(searchRequest.getCodes().split(","))));
		}
		if (searchRequest.getTenantId() != null) {
			if (params.length() > 0) {
				params.append(" and ");
			}
			params.append("tenantId =:tenantId");
			paramValues.put("tenantId", searchRequest.getTenantId());
		}

		if (searchRequest.getSourceSegregationDate() != null) {
			if (params.length() > 0) {
				params.append(" and ");
			}
			params.append("sourceSegregationDate =:sourceSegregationDate");
			paramValues.put("sourceSegregationDate", searchRequest.getSourceSegregationDate());
		}

		if (searchRequest.getDumpingGroundCode() != null) {
			if (params.length() > 0) {
				params.append(" and ");
			}
			params.append("dumpingGround =:dumpingGround");
			paramValues.put("dumpingGround", searchRequest.getDumpingGroundCode());
		}

		Pagination<SourceSegregation> page = new Pagination<>();
		if (searchRequest.getOffset() != null) {
			page.setOffset(searchRequest.getOffset());
		}
		if (searchRequest.getPageSize() != null) {
			page.setPageSize(searchRequest.getPageSize());
		}

		if (params.length() > 0) {

			searchQuery = searchQuery.replace(":condition", " where " + params.toString());

		} else

			searchQuery = searchQuery.replace(":condition", "");

		searchQuery = searchQuery.replace(":orderby", orderBy);

		page = (Pagination<SourceSegregation>) getPagination(searchQuery, page, paramValues);
		searchQuery = searchQuery + " :pagination";

		searchQuery = searchQuery.replace(":pagination",
				"limit " + page.getPageSize() + " offset " + page.getOffset() * page.getPageSize());

		BeanPropertyRowMapper row = new BeanPropertyRowMapper(SourceSegregationEntity.class);

		List<SourceSegregation> sourceSegregationList = new ArrayList<>();

		List<SourceSegregationEntity> sourceSegregationEntities = namedParameterJdbcTemplate
				.query(searchQuery.toString(), paramValues, row);
		SourceSegregation ss;
		CollectionDetailsSearch cds = new CollectionDetailsSearch();
		for (SourceSegregationEntity sourceSegregationEntity : sourceSegregationEntities) {

			ss = sourceSegregationEntity.toDomain();
			cds.setTenantId(ss.getTenantId());
			cds.setSourceSegregationCode(ss.getCode());
			ss.setCollectionDetails(collectionDetailsJdbcRepository.search(cds));

			sourceSegregationList.add(ss);

		}

		page.setTotalResults(sourceSegregationList.size());

		page.setPagedData(sourceSegregationList);

		return page;
	}

}