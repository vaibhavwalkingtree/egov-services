
/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.asset.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.egov.asset.contract.AssetRequest;
import org.egov.asset.model.Asset;
import org.egov.asset.model.AssetCriteria;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.model.Location;
import org.egov.asset.model.YearWiseDepreciation;
import org.egov.asset.model.enums.AssetStatusObjectName;
import org.egov.asset.model.enums.Status;
import org.egov.asset.repository.builder.AssetQueryBuilder;
import org.egov.asset.repository.rowmapper.AssetRowMapper;
import org.egov.asset.repository.rowmapper.YearWiseDepreciationRowMapper;
import org.egov.asset.service.AssetMasterService;
import org.egov.common.contract.request.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class AssetRepository {

    private static final Logger logger = LoggerFactory.getLogger(AssetRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AssetRowMapper assetRowMapper;

    @Autowired
    private AssetQueryBuilder assetQueryBuilder;

    @Autowired
    private YearWiseDepreciationRowMapper yearWiseDepreciationRowMapper;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AssetMasterService assetMasterService;

    public List<Asset> findForCriteria(final AssetCriteria assetCriteria) {

        final List<Object> preparedStatementValues = new ArrayList<>();
        final String queryStr = assetQueryBuilder.getQuery(assetCriteria, preparedStatementValues);
        List<Asset> assets = null;
        try {
            logger.info("queryStr::" + queryStr + "preparedStatementValues::" + preparedStatementValues.toString());
            assets = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), assetRowMapper);
            logger.info("AssetRepository::" + assets);
        } catch (final Exception ex) {
            logger.info("the exception from findforcriteria : " + ex);
        }
        return assets;
    }

    public String findAssetName(final String tenantId, final String name) {

        final String queryStr = AssetQueryBuilder.FINDBYNAMEQUERY;
        logger.info("queryStr::" + queryStr + "preparedStatementValues::" + name + "tenantid" + tenantId);
        String assetName = null;
        try {
            assetName = jdbcTemplate.queryForObject(queryStr, new Object[] { name, tenantId }, String.class);
            logger.info("AssetRepository::" + assetName);
        } catch (final Exception ex) {
            logger.info("the exception from findbyname method indicates no duplicate assets available : " + ex);
        }
        return assetName;
    }

    public String getAssetCode() {
        final String query = "SELECT nextval('seq_egasset_assetcode')";
        final Integer result = jdbcTemplate.queryForObject(query, Integer.class);
        logger.info("result:" + result);
        StringBuilder code = null;
        try {
            code = new StringBuilder(String.format("%06d", result));
        } catch (final Exception ex) {
            logger.info("the exception from seq number gen for code : " + ex);
        }
        return code.toString();
    }

    public Integer getNextAssetId() {
        final String query = "SELECT nextval('seq_egasset_asset')";
        final Integer result = jdbcTemplate.queryForObject(query, Integer.class);
        return result;
    }

    private List<Asset> findAssetByCode(final String code) {
        final AssetCriteria assetCriteria = new AssetCriteria();
        assetCriteria.setCode(code);
        return findForCriteria(assetCriteria);
    }

    @Transactional
    public Asset create(final AssetRequest assetRequest) {

        logger.info("the asset request in repository create : " + assetRequest);
        final RequestInfo requestInfo = assetRequest.getRequestInfo();
        final Asset asset = assetRequest.getAsset();

        String property = null;
        try {
            mapper.setSerializationInclusion(Include.NON_EMPTY);
            final Asset asset2 = new Asset();
            asset2.setAssetAttributes(asset.getAssetAttributes());
            property = mapper.writeValueAsString(asset2);
        } catch (final JsonProcessingException e) {
            logger.info("the exception in insert from parsing attributes : " + e);
        }

        final String query = assetQueryBuilder.getInsertQuery();

        String modeOfAcquisition = null;
        String status = null;

        if (asset.getModeOfAcquisition() != null)
            modeOfAcquisition = asset.getModeOfAcquisition().toString();

        if (asset.getStatus() != null)
            status = asset.getStatus();

        if (assetRequest.getAsset().getEnableYearWiseDepreciation())
            asset.setDepreciationRate(null);

        final Location location = asset.getLocationDetails();

        final Object[] obj = new Object[] { asset.getId(), asset.getAssetCategory().getId(), asset.getName(),
                asset.getCode(), asset.getDepartment().getId(), asset.getAssetDetails(), asset.getDescription(),
                asset.getDateOfCreation(), asset.getRemarks(), asset.getLength(), asset.getWidth(),
                asset.getTotalArea(), modeOfAcquisition, status, asset.getTenantId(), location.getZone(),
                location.getRevenueWard(), location.getStreet(), location.getElectionWard(), location.getDoorNo(),
                location.getPinCode(), location.getLocality(), location.getBlock(), property,
                requestInfo.getUserInfo().getId(), new Date(), requestInfo.getUserInfo().getId(), new Date(),
                asset.getGrossValue(), asset.getAccumulatedDepreciation(), asset.getAssetReference(),
                asset.getVersion(), asset.getEnableYearWiseDepreciation(), asset.getDepreciationRate() };
        try {
            jdbcTemplate.update(query, obj);
        } catch (final Exception ex) {
            logger.info("the exception from insert query : " + ex);
        }
        if (assetRequest.getAsset().getEnableYearWiseDepreciation())
            saveYearWiseDepreciation(assetRequest);
        return asset;
    }

    public Asset update(final AssetRequest assetRequest) {
        final RequestInfo requestInfo = assetRequest.getRequestInfo();
        final Asset asset = assetRequest.getAsset();

        String property = null;
        try {
            mapper.setSerializationInclusion(Include.NON_NULL);
            final Asset asset2 = new Asset();
            asset2.setAssetAttributes(asset.getAssetAttributes());
            property = mapper.writeValueAsString(asset2);

        } catch (final JsonProcessingException e) {
            logger.info("exception from parsing the assetattributes : " + e);
        }

        final String query = assetQueryBuilder.getUpdateQuery();

        logger.info("asset update query::" + query);

        String modeOfAcquisition = null;
        String status = null;

        if (asset.getModeOfAcquisition() != null)
            modeOfAcquisition = asset.getModeOfAcquisition().toString();

        if (asset.getStatus() != null)
            status = asset.getStatus();

        final Location location = asset.getLocationDetails();

        final Object[] obj = new Object[] { asset.getAssetCategory().getId(), asset.getName(),
                asset.getDepartment().getId(), asset.getAssetDetails(), asset.getDescription(), asset.getRemarks(),
                asset.getLength(), asset.getWidth(), asset.getTotalArea(), modeOfAcquisition, status,
                location.getZone(), location.getRevenueWard(), location.getStreet(), location.getElectionWard(),
                location.getDoorNo(), location.getPinCode(), location.getLocality(), location.getBlock(), property,
                requestInfo.getUserInfo().getId(), new Date(), asset.getGrossValue(),
                asset.getAccumulatedDepreciation(), asset.getAssetReference(), asset.getVersion(), asset.getCode(),
                asset.getTenantId() };
        try {
            logger.info("query1::" + query + "," + Arrays.toString(obj));
            final int i = jdbcTemplate.update(query, obj);
            logger.info("output of update query : " + i);
        } catch (final Exception ex) {
            logger.info("the exception from update asset : " + ex);
        }
        final List<AssetStatus> assetStatuses = assetMasterService.getStatuses(AssetStatusObjectName.ASSETMASTER,
                Status.DISPOSED, asset.getTenantId());
        logger.debug("assetStatus check for asset update:: " + assetStatuses);
        if (!assetStatuses.isEmpty()) {
            final AssetStatus assetStatus = assetStatuses.get(0);
            if (!assetStatus.getStatusValues().get(0).getCode().equalsIgnoreCase(status))
                updateDepreciationData(assetRequest);
        }
        return asset;
    }

    private void updateDepreciationData(final AssetRequest assetRequest) {
        final Asset asset = assetRequest.getAsset();
        final List<Asset> assets = findAssetByCode(asset.getCode());
        final Asset oldAsset = assets.get(0);
        if (!oldAsset.getEnableYearWiseDepreciation() && asset.getEnableYearWiseDepreciation()) {
            final String queryForPrevDepreciationCheck = AssetQueryBuilder.DATACHECKINYEARWISEDEPRECIATION;
            final List<YearWiseDepreciation> yearWiseDepreciations = jdbcTemplate.query(queryForPrevDepreciationCheck,
                    new Long[] { oldAsset.getId() }, yearWiseDepreciationRowMapper);
            if (!yearWiseDepreciations.isEmpty()) {
                validateYearWiseDepreciations(yearWiseDepreciations, assetRequest);
                updateAssetDepreciationRate(asset, false);
            } else {
                saveYearWiseDepreciation(assetRequest);
                updateAssetDepreciationRate(asset, false);
            }
        }
        if (oldAsset.getEnableYearWiseDepreciation() && !asset.getEnableYearWiseDepreciation())
            updateAssetDepreciationRate(asset, true);
    }

    private void validateYearWiseDepreciations(final List<YearWiseDepreciation> oldYearWiseDepr,
            final AssetRequest assetRequest) {
        final List<YearWiseDepreciation> yearWiseDepreciations = assetRequest.getAsset().getYearWiseDepreciation();
        if (oldYearWiseDepr.size() != yearWiseDepreciations.size()) {
            logger.info("adding and updating year wise depreciations");
            addUpdateYearWiseDepreciation(oldYearWiseDepr, yearWiseDepreciations, assetRequest);
        } else {
            logger.info("updating year wise depreciations");
            updateYearWiseDepreciation(assetRequest);
        }
    }

    private void addUpdateYearWiseDepreciation(final List<YearWiseDepreciation> oldYearWiseDepr,
            final List<YearWiseDepreciation> yearWiseDepreciations, final AssetRequest assetRequest) {
        final List<YearWiseDepreciation> iywds = new ArrayList<YearWiseDepreciation>();
        final List<YearWiseDepreciation> uywds = new ArrayList<YearWiseDepreciation>();
        for (final YearWiseDepreciation ywd : yearWiseDepreciations)
            for (final YearWiseDepreciation oldYwd : oldYearWiseDepr) {
                final boolean checkForNewData = ywd.getFinancialYear().equals(oldYwd.getFinancialYear())
                        && ywd.getAssetId().equals(oldYwd.getAssetId());
                if (!checkForNewData)
                    iywds.add(ywd);
                if (checkForNewData && !ywd.getDepreciationRate().equals(oldYwd.getDepreciationRate()))
                    uywds.add(ywd);
            }
        logger.debug("year wise depreciations will be inserted :: " + iywds);
        logger.debug("year wise depreciations will be updated :: " + uywds);
        if (!iywds.isEmpty()) {
            assetRequest.getAsset().setYearWiseDepreciation(iywds);
            logger.debug("Asset Request for inserting year wise depreciations :: " + assetRequest);
            saveYearWiseDepreciation(assetRequest);
        }
        if (!uywds.isEmpty()) {
            assetRequest.getAsset().setYearWiseDepreciation(uywds);
            logger.debug("Asset Request for updating year wise depreciations :: " + assetRequest);
            updateYearWiseDepreciation(assetRequest);
        }

    }

    private void updateAssetDepreciationRate(final Asset asset, final boolean changeDepRateInAsset) {
        String depreciationRateUpdateQuery = null;
        if (changeDepRateInAsset)
            depreciationRateUpdateQuery = AssetQueryBuilder.ASSETINCLUDEDEPRECIATIONRATEUPDATEQUERY;
        else
            depreciationRateUpdateQuery = AssetQueryBuilder.ASSETEXCLUDEDEPRECIATIONRATEUPDATEQUERY;
        logger.debug("Asset Depreciation Rate Update Query : " + depreciationRateUpdateQuery);
        final List<Object> preparedStatementValues = new ArrayList<>();
        preparedStatementValues.add(asset.getEnableYearWiseDepreciation());
        if (changeDepRateInAsset)
            preparedStatementValues.add(asset.getDepreciationRate());
        preparedStatementValues.add(asset.getCode());
        preparedStatementValues.add(asset.getTenantId());
        logger.info("Asset Depreciation Rate Update Parameters : " + preparedStatementValues);
        jdbcTemplate.update(depreciationRateUpdateQuery, preparedStatementValues.toArray());
    }

    private void saveYearWiseDepreciation(final AssetRequest assetRequest) {
        final RequestInfo requestInfo = assetRequest.getRequestInfo();
        final Asset asset = assetRequest.getAsset();
        final List<YearWiseDepreciation> yearWiseDepreciations = asset.getYearWiseDepreciation();

        logger.debug("Year Wise Details Insert Query ::" + AssetQueryBuilder.BATCHINSERTQUERY);
        jdbcTemplate.batchUpdate(AssetQueryBuilder.BATCHINSERTQUERY, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                final YearWiseDepreciation yearWiseDepreciation = yearWiseDepreciations.get(index);
                ps.setDouble(1, yearWiseDepreciation.getDepreciationRate());
                ps.setString(2, yearWiseDepreciation.getFinancialYear());
                ps.setLong(3, asset.getId());
                ps.setObject(4, yearWiseDepreciation.getUsefulLifeInYears());
                ps.setString(5, asset.getTenantId());
                ps.setString(6, requestInfo.getUserInfo().getId().toString());
                ps.setLong(7, new Date().getTime());
                ps.setString(8, requestInfo.getUserInfo().getId().toString());
                ps.setLong(9, new Date().getTime());
            }

            @Override
            public int getBatchSize() {
                return yearWiseDepreciations.size();
            }
        });
    }

    private void updateYearWiseDepreciation(final AssetRequest assetRequest) {
        final RequestInfo requestInfo = assetRequest.getRequestInfo();
        final Asset asset = assetRequest.getAsset();
        final List<YearWiseDepreciation> yearWiseDepreciations = asset.getYearWiseDepreciation();

        logger.debug("Year Wise Details Update Query ::" + AssetQueryBuilder.BATCHUPDATEQUERY);
        jdbcTemplate.batchUpdate(AssetQueryBuilder.BATCHUPDATEQUERY, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                final YearWiseDepreciation yearWiseDepreciation = yearWiseDepreciations.get(index);
                ps.setDouble(1, yearWiseDepreciation.getDepreciationRate());
                ps.setObject(2, yearWiseDepreciation.getUsefulLifeInYears());
                ps.setString(3, requestInfo.getUserInfo().getId().toString());
                ps.setLong(4, new Date().getTime());
                ps.setString(5, requestInfo.getUserInfo().getId().toString());
                ps.setLong(6, new Date().getTime());
                ps.setLong(7, asset.getId());
                ps.setString(8, yearWiseDepreciation.getFinancialYear());
                ps.setString(9, asset.getTenantId());
            }

            @Override
            public int getBatchSize() {
                return yearWiseDepreciations.size();
            }
        });
    }
}