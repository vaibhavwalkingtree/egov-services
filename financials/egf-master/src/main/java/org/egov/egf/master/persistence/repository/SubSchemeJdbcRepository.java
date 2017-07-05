package org.egov.egf.master.persistence.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.egov.common.domain.model.Pagination;
import org.egov.common.persistence.repository.JdbcRepository;
import org.egov.egf.master.domain.model.SubScheme;
import org.egov.egf.master.domain.model.SubSchemeSearch;
import org.egov.egf.master.persistence.entity.SubSchemeEntity;
import org.egov.egf.master.persistence.entity.SubSchemeSearchEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

@Service
public class SubSchemeJdbcRepository extends JdbcRepository {
	private static final Logger LOG = LoggerFactory.getLogger(SubSchemeJdbcRepository.class);

	static {
		LOG.debug("init subScheme");
		init(SubSchemeEntity.class);
		LOG.debug("end init subScheme");
	}

	public SubSchemeEntity create(SubSchemeEntity entity) {

		entity.setId(UUID.randomUUID().toString().replace("-", ""));
		super.create(entity);
		return entity;
	}

	public SubSchemeEntity update(SubSchemeEntity entity) {
		super.update(entity);
		return entity;

	}

	public Pagination<SubScheme> search(SubSchemeSearch domain) {
		SubSchemeSearchEntity subSchemeSearchEntity = new SubSchemeSearchEntity();
		subSchemeSearchEntity.toEntity(domain);

		String searchQuery = "select :selectfields from :tablename :condition  :orderby   ";

		Map<String, Object> paramValues = new HashMap<>();
		StringBuffer params = new StringBuffer();
		String orderBy = "";

		searchQuery = searchQuery.replace(":tablename", SubSchemeEntity.TABLE_NAME);

		searchQuery = searchQuery.replace(":selectfields", " * ");

		// implement jdbc specfic search
if( subSchemeSearchEntity.getId()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "id =: id");
paramValues.put("id" ,subSchemeSearchEntity.getId());} 
if( subSchemeSearchEntity.getSchemeId()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "scheme =: scheme");
paramValues.put("scheme" ,subSchemeSearchEntity.getSchemeId());} 
if( subSchemeSearchEntity.getCode()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "code =: code");
paramValues.put("code" ,subSchemeSearchEntity.getCode());} 
if( subSchemeSearchEntity.getName()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "name =: name");
paramValues.put("name" ,subSchemeSearchEntity.getName());} 
if( subSchemeSearchEntity.getValidFrom()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "validFrom =: validFrom");
paramValues.put("validFrom" ,subSchemeSearchEntity.getValidFrom());} 
if( subSchemeSearchEntity.getValidTo()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "validTo =: validTo");
paramValues.put("validTo" ,subSchemeSearchEntity.getValidTo());} 
if( subSchemeSearchEntity.getActive()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "active =: active");
paramValues.put("active" ,subSchemeSearchEntity.getActive());} 
if( subSchemeSearchEntity.getDepartmentId()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "departmentId =: departmentId");
paramValues.put("departmentId" ,subSchemeSearchEntity.getDepartmentId());} 

		 

		Pagination<SubScheme> page = new Pagination<>();
		page.setOffSet(subSchemeSearchEntity.getOffset());
		page.setPageSize(subSchemeSearchEntity.getPageSize());

		if (params.length() > 0) {

			searchQuery = searchQuery.replace(":condition", " where " + params.toString());

		} else {
			searchQuery = searchQuery.replace(":condition", "");
		}

		searchQuery = searchQuery.replace(":orderby", "order by id ");

		page = getPagination(searchQuery, page);
		searchQuery = searchQuery + " :pagination";

		searchQuery = searchQuery.replace(":pagination", "limit " + subSchemeSearchEntity.getPageSize() + " offset "
				+ subSchemeSearchEntity.getOffset() * subSchemeSearchEntity.getPageSize());

		BeanPropertyRowMapper row = new BeanPropertyRowMapper(SubSchemeEntity.class);

		List<SubSchemeEntity> subSchemeEntities = namedParameterJdbcTemplate.query(searchQuery.toString(), paramValues, row);

		page.setTotalResults(subSchemeEntities.size());

		List<SubScheme> subschemes = new ArrayList<SubScheme>();
		for (SubSchemeEntity subSchemeEntity : subSchemeEntities) {

			subschemes.add(subSchemeEntity.toDomain());
		}
		page.setPagedData(subschemes);

		return page;
	}

	public SubSchemeEntity findById(SubSchemeEntity entity) {
		List<String> list = allUniqueFields.get(entity.getClass().getSimpleName());

		final List<Object> preparedStatementValues = new ArrayList<>();

		for (String s : list) {
			preparedStatementValues.add(getValue(getField(entity, s), entity));
		}

		List<SubSchemeEntity> subschemes = jdbcTemplate.query(getByIdQuery.get(entity.getClass().getSimpleName()),
				preparedStatementValues.toArray(), new BeanPropertyRowMapper<SubSchemeEntity>());
		if (subschemes.isEmpty()) {
			return null;
		} else {
			return subschemes.get(0);
		}

	}

}