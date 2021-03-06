package org.egov.lcms.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.lcms.models.Advocate;
import org.egov.lcms.models.AuditDetails;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class AdvocateRowMapper implements RowMapper<Advocate>{

	@Override
	public Advocate mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Advocate advocate = new Advocate();
		advocate.setCode(rs.getString("code"));
		advocate.setName(rs.getString("name"));
		advocate.setOrganizationName(rs.getString("organizationName"));
		advocate.setIsIndividual(rs.getBoolean("isIndividual"));
		advocate.setTitle(rs.getString("title"));
		advocate.setFirstName(rs.getString("firstName"));
		advocate.setSecondName(rs.getString("secondName"));
		advocate.setLastName(rs.getString("lastName"));
		advocate.setAddress(rs.getString("address"));
		advocate.setContactNo(rs.getString("contactNo"));
		advocate.setDateOfEmpanelment(rs.getLong("dateOfEmpanelment"));
		advocate.setStandingCommitteeDecisionDate(rs.getLong("standingCommitteeDecisionDate"));
		advocate.setDateOfEmpanelment(rs.getLong("dateOfEmpanelment"));
		advocate.setStandingCommitteeDecisionDate(rs.getLong("standingCommitteeDecisionDate"));
		advocate.setEmpanelmentFromDate(rs.getLong("empanelmentFromDate"));
		advocate.setAadhar(rs.getString("aadhar"));
		advocate.setGender(rs.getString("gender"));
		advocate.setAge(rs.getString("age"));
		advocate.setDob(rs.getLong("dob"));
		advocate.setMobileNumber(rs.getString("mobileNumber"));
		advocate.setEmailId(rs.getString("emailId"));
		advocate.setPan(rs.getString("pan"));
		advocate.setVatTinNo(rs.getString("vatTinNo"));
		advocate.setNewsPaperAdvertismentDate(rs.getLong("newsPaperAdvertismentDate"));
		advocate.setEmpanelmentToDate(rs.getLong("empanelmentToDate"));
		advocate.setBankName(rs.getString("bankName"));
		advocate.setBankBranch(rs.getString("bankBranch"));
		advocate.setBankAccountNo(rs.getString("bankAccountNo"));
		advocate.setIsfcCode(rs.getString("isfcCode"));
		advocate.setMicr(rs.getString("micr"));
		advocate.setIsActive(rs.getBoolean("isActive"));
		advocate.setIsTerminate(rs.getBoolean("isTerminate"));
		advocate.setInActiveDate(rs.getLong("inActiveDate"));
		advocate.setTerminationDate(rs.getLong("terminationDate"));
		advocate.setReasonOfTermination(rs.getString("reasonOfTermination"));
		advocate.setTenantId(rs.getString("tenantId"));
		AuditDetails auditDetails = new AuditDetails();
		auditDetails.setCreatedBy(rs.getString("createdBy"));
		auditDetails.setLastModifiedBy(rs.getString("lastModifiedBy"));
		auditDetails.setCreatedTime(rs.getBigDecimal("createdTime"));
		auditDetails.setLastModifiedTime(rs.getBigDecimal("lastModifiedTime"));
		advocate.setAuditDetails(auditDetails);
		
		return advocate;
	}

}
