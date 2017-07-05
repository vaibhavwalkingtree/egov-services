package org.egov.egf.master.persistence.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.egov.common.domain.model.Pagination;
import org.egov.common.persistence.repository.JdbcRepository;
import org.egov.egf.master.domain.model.AccountDetailKey;
import org.egov.egf.master.domain.model.AccountDetailKeySearch;
import org.egov.egf.master.persistence.entity.AccountDetailKeyEntity;
import org.egov.egf.master.persistence.entity.AccountDetailKeySearchEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailKeyJdbcRepository extends JdbcRepository {
	private static final Logger LOG = LoggerFactory.getLogger(AccountDetailKeyJdbcRepository.class);

	static {
		LOG.debug("init accountDetailKey");
		init(AccountDetailKeyEntity.class);
		LOG.debug("end init accountDetailKey");
	}

	public AccountDetailKeyEntity create(AccountDetailKeyEntity entity) {

		entity.setId(UUID.randomUUID().toString().replace("-", ""));
		super.create(entity);
		return entity;
	}

	public AccountDetailKeyEntity update(AccountDetailKeyEntity entity) {
		super.update(entity);
		return entity;

	}

	public Pagination<AccountDetailKey> search(AccountDetailKeySearch domain) {
		AccountDetailKeySearchEntity accountDetailKeySearchEntity = new AccountDetailKeySearchEntity();
		accountDetailKeySearchEntity.toEntity(domain);

		String searchQuery = "select :selectfields from :tablename :condition  :orderby   ";

		Map<String, Object> paramValues = new HashMap<>();
		StringBuffer params = new StringBuffer();
		String orderBy = "";

		searchQuery = searchQuery.replace(":tablename", AccountDetailKeyEntity.TABLE_NAME);

		searchQuery = searchQuery.replace(":selectfields", " * ");

		// implement jdbc specfic search
if( accountDetailKeySearchEntity.getId()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "id =: id");
paramValues.put("id" ,accountDetailKeySearchEntity.getId());} 
if( accountDetailKeySearchEntity.getKey()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "key =: key");
paramValues.put("key" ,accountDetailKeySearchEntity.getKey());} 
if( accountDetailKeySearchEntity.getAccountDetailTypeId()!=null) {
if (params.length() > 0) 
params.append(" and "); 
params.append( "accountDetailType =: accountDetailType");
paramValues.put("accountDetailType" ,accountDetailKeySearchEntity.getAccountDetailTypeId());} 

		 

		Pagination<AccountDetailKey> page = new Pagination<>();
		page.setOffSet(accountDetailKeySearchEntity.getOffset());
		page.setPageSize(accountDetailKeySearchEntity.getPageSize());

		if (params.length() > 0) {

			searchQuery = searchQuery.replace(":condition", " where " + params.toString());

		} else {
			searchQuery = searchQuery.replace(":condition", "");
		}

		searchQuery = searchQuery.replace(":orderby", "order by id ");

		page = getPagination(searchQuery, page);
		searchQuery = searchQuery + " :pagination";

		searchQuery = searchQuery.replace(":pagination", "limit " + accountDetailKeySearchEntity.getPageSize() + " offset "
				+ accountDetailKeySearchEntity.getOffset() * accountDetailKeySearchEntity.getPageSize());

		BeanPropertyRowMapper row = new BeanPropertyRowMapper(AccountDetailKeyEntity.class);

		List<AccountDetailKeyEntity> accountDetailKeyEntities = namedParameterJdbcTemplate.query(searchQuery.toString(), paramValues, row);

		page.setTotalResults(accountDetailKeyEntities.size());

		List<AccountDetailKey> accountdetailkeys = new ArrayList<AccountDetailKey>();
		for (AccountDetailKeyEntity accountDetailKeyEntity : accountDetailKeyEntities) {

			accountdetailkeys.add(accountDetailKeyEntity.toDomain());
		}
		page.setPagedData(accountdetailkeys);

		return page;
	}

	public AccountDetailKeyEntity findById(AccountDetailKeyEntity entity) {
		List<String> list = allUniqueFields.get(entity.getClass().getSimpleName());

		final List<Object> preparedStatementValues = new ArrayList<>();

		for (String s : list) {
			preparedStatementValues.add(getValue(getField(entity, s), entity));
		}

		List<AccountDetailKeyEntity> accountdetailkeys = jdbcTemplate.query(getByIdQuery.get(entity.getClass().getSimpleName()),
				preparedStatementValues.toArray(), new BeanPropertyRowMapper<AccountDetailKeyEntity>());
		if (accountdetailkeys.isEmpty()) {
			return null;
		} else {
			return accountdetailkeys.get(0);
		}

	}

}