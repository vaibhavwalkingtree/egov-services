package org.egov.egf.voucher.domain.service;

import java.util.List;

import org.egov.common.constants.Constants;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.domain.exception.CustomBindException;
import org.egov.common.domain.exception.InvalidDataException;
import org.egov.common.domain.model.Pagination;
import org.egov.egf.master.web.contract.FinancialStatusContract;
import org.egov.egf.master.web.contract.FunctionContract;
import org.egov.egf.master.web.contract.FunctionaryContract;
import org.egov.egf.master.web.contract.FundContract;
import org.egov.egf.master.web.contract.FundsourceContract;
import org.egov.egf.master.web.contract.SchemeContract;
import org.egov.egf.master.web.contract.SubSchemeContract;
import org.egov.egf.master.web.repository.FinancialConfigurationContractRepository;
import org.egov.egf.master.web.repository.FinancialStatusContractRepository;
import org.egov.egf.master.web.repository.FunctionContractRepository;
import org.egov.egf.master.web.repository.FunctionaryContractRepository;
import org.egov.egf.master.web.repository.FundContractRepository;
import org.egov.egf.master.web.repository.FundsourceContractRepository;
import org.egov.egf.master.web.repository.SchemeContractRepository;
import org.egov.egf.master.web.repository.SubSchemeContractRepository;
import org.egov.egf.voucher.domain.model.Voucher;
import org.egov.egf.voucher.domain.model.VoucherSearch;
import org.egov.egf.voucher.domain.repository.VoucherRepository;
import org.egov.egf.voucher.web.contract.Boundary;
import org.egov.egf.voucher.web.contract.DepartmentResponse;
import org.egov.egf.voucher.web.repository.BoundaryRepository;
import org.egov.egf.voucher.web.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;

@Service
@Transactional(readOnly = true)
public class VoucherService {

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private SmartValidator validator;

	@Autowired
	private FinancialStatusContractRepository financialStatusContractRepository;

	@Autowired
	private FinancialConfigurationContractRepository financialConfigurationContractRepository;

	@Autowired
	private FundContractRepository fundContractRepository;

	@Autowired
	private FunctionContractRepository funnctionContractRepository;

	@Autowired
	private FundsourceContractRepository fundsourceContractRepository;

	@Autowired
	private SchemeContractRepository schemeContractRepository;

	@Autowired
	private SubSchemeContractRepository subSchemeContractRepository;

	@Autowired
	private FunctionaryContractRepository functionaryContractRepository;

	@Autowired
	private BoundaryRepository boundaryRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Transactional
	public List<Voucher> create(List<Voucher> vouchers, BindingResult errors, RequestInfo requestInfo) {

		try {

			vouchers = fetchRelated(vouchers);

			validate(vouchers, Constants.ACTION_CREATE, errors);

			if (errors.hasErrors()) {
				throw new CustomBindException(errors);
			}

		} catch (CustomBindException e) {

			throw new CustomBindException(errors);
		}

		return voucherRepository.save(vouchers, requestInfo);

	}

	@Transactional
	public List<Voucher> update(List<Voucher> vouchers, BindingResult errors, RequestInfo requestInfo) {

		try {

			vouchers = fetchRelated(vouchers);

			validate(vouchers, Constants.ACTION_UPDATE, errors);

			if (errors.hasErrors()) {
				throw new CustomBindException(errors);
			}

		} catch (CustomBindException e) {

			throw new CustomBindException(errors);
		}

		return voucherRepository.update(vouchers, requestInfo);

	}

	private BindingResult validate(List<Voucher> vouchers, String method, BindingResult errors) {

		try {

			switch (method) {

			case Constants.ACTION_VIEW:
				// validator.validate(voucherContractRequest.getVoucher(),
				// errors);
				break;
			case Constants.ACTION_CREATE:
				Assert.notNull(vouchers, "Vouchers to create must not be null");
				for (Voucher voucher : vouchers) {
					validator.validate(voucher, errors);
				}
				break;
			case Constants.ACTION_UPDATE:
				Assert.notNull(vouchers, "Vouchers to update must not be null");
				for (Voucher voucher : vouchers) {
					validator.validate(voucher, errors);
				}
				break;
			default:

			}

		} catch (IllegalArgumentException e) {
			errors.addError(new ObjectError("Missing data", e.getMessage()));
		}

		return errors;

	}

	public List<Voucher> fetchRelated(List<Voucher> vouchers) {
		if (null != vouchers)
			for (Voucher voucher : vouchers) {

				// fetch related items
				if (voucher.getFund() != null) {
					FundContract fund = fundContractRepository.findById(voucher.getFund());
					if (fund == null) {
						throw new InvalidDataException("fund", "fund.invalid", " Invalid fund");
					}
					voucher.setFund(fund);
				}

				if (voucher.getStatus() != null) {
					FinancialStatusContract status = financialStatusContractRepository.findById(voucher.getStatus());
					if (status == null) {
						throw new InvalidDataException("status", "status.invalid", " Invalid status");
					}
					voucher.setStatus(status);
				}

				if (voucher.getFunction() != null) {
					FunctionContract function = funnctionContractRepository.findById(voucher.getFunction());
					if (function == null) {
						throw new InvalidDataException("function", "function.invalid", " Invalid function");
					}
					voucher.setFunction(function);
				}

				if (voucher.getFundsource() != null) {
					FundsourceContract fundsource = fundsourceContractRepository.findById(voucher.getFundsource());
					if (fundsource == null) {
						throw new InvalidDataException("fundsource", "fundsource.invalid", " Invalid fundsource");
					}
					voucher.setFundsource(fundsource);
				}

				if (voucher.getScheme() != null) {
					SchemeContract scheme = schemeContractRepository.findById(voucher.getScheme());
					if (scheme == null) {
						throw new InvalidDataException("scheme", "scheme.invalid", " Invalid scheme");
					}
					voucher.setScheme(scheme);
				}

				if (voucher.getSubScheme() != null) {
					SubSchemeContract subScheme = subSchemeContractRepository.findById(voucher.getSubScheme());
					if (subScheme == null) {
						throw new InvalidDataException("subScheme", "subScheme.invalid", " Invalid subScheme");
					}
					voucher.setSubScheme(subScheme);
				}

				if (voucher.getFunctionary() != null) {
					FunctionaryContract functionary = functionaryContractRepository.findById(voucher.getFunctionary());
					if (functionary == null) {
						throw new InvalidDataException("functionary", "functionary.invalid", " Invalid functionary");
					}
					voucher.setFunctionary(functionary);
				}

				if (voucher.getDivision() != null) {
					Boundary devision = boundaryRepository.getBoundaryById(voucher.getDivision().getId(),
							voucher.getDivision().getTenantId());
					if (devision == null) {
						throw new InvalidDataException("division", "division.invalid", " Invalid division");
					}
					voucher.setDivision(devision);
				}

				if (voucher.getDepartment() != null) {
					DepartmentResponse department = departmentRepository
							.getDepartmentById(voucher.getDepartment().getId(), voucher.getDepartment().getTenantId());
					if (department == null || department.getDepartment() == null
							|| department.getDepartment().isEmpty()) {
						throw new InvalidDataException("department", "department.invalid", " Invalid department");
					}
					voucher.setDepartment(department.getDepartment().get(0));
				}

			}

		return vouchers;
	}

	public Pagination<Voucher> search(VoucherSearch voucherSearch) {
		return voucherRepository.search(voucherSearch);
	}

	@Transactional
	public Voucher save(Voucher voucher) {
		return voucherRepository.save(voucher);
	}

	@Transactional
	public Voucher update(Voucher voucher) {
		return voucherRepository.update(voucher);
	}

}