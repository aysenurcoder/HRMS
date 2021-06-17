package kodlamaio.hrms.business.concretes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kodlamaio.hrms.business.abstracts.CandidateService;
import kodlamaio.hrms.business.constants.Messages;
import kodlamaio.hrms.core.abstracts.UserDao;
import kodlamaio.hrms.core.utilities.adapters.UserCheckService;
import kodlamaio.hrms.core.utilities.business.BusinessRules;
import kodlamaio.hrms.core.utilities.emailValidation.EmailValidationService;
import kodlamaio.hrms.core.utilities.results.DataResult;
import kodlamaio.hrms.core.utilities.results.ErrorResult;
import kodlamaio.hrms.core.utilities.results.Result;
import kodlamaio.hrms.core.utilities.results.SuccessDataResult;
import kodlamaio.hrms.core.utilities.results.SuccessResult;
import kodlamaio.hrms.dataAccess.abstracts.CandidateDao;
import kodlamaio.hrms.entities.concretes.Candidate;

@Service
public class CandidateManager implements CandidateService {

	private CandidateDao candidateDao;
	private UserCheckService userCheckService;
	private EmailValidationService emailValidationService;
	private UserDao userDao;
	
	@Autowired
	public CandidateManager(CandidateDao candidateDao,UserCheckService userCheckService
			,EmailValidationService emailValidationService,UserDao userDao) {
		super();
		this.candidateDao = candidateDao;
		this.userCheckService=userCheckService;
		this.emailValidationService=emailValidationService;
		this.userDao=userDao;
	}

	@Override
	public void save(Candidate candidate) {
		this.candidateDao.save(candidate);
		
	}

	@Override
	public DataResult<List<Candidate>> getAll() {
		return new SuccessDataResult<List<Candidate>>(this.candidateDao.findAll(),"Başvuranlar listelendi");
	}
	
	
	@Override
	public Result register(Candidate candidate) {
		Result result = BusinessRules.run(existIdentityNumber(candidate.getIdentityNumber()),
				existEmail(candidate.getEmail()), checkIfRealPerson(candidate), isEmailVerified(candidate.getEmail()));
		
		if (result != null) {
			return result;
		}

		this.candidateDao.save(candidate);
		return new SuccessResult(Messages.candidateAdded);  
	}
	
	private Result existEmail(String email) {
		if (this.userDao.getByEmailEquals(email) != null) {
			return new ErrorResult("Bu email adresi ile daha önce kayıt oluşturulmuştur");
		}

		return new SuccessResult();
	}

	private Result existIdentityNumber(String nationalityId) {
		if (this.candidateDao.getByIdentityNumberEquals(nationalityId) != null) {
			return new ErrorResult("TC No ile daha önce kayıt oluşturulmuştur");
		}

		return new SuccessResult();
	}

	private Result checkIfRealPerson(Candidate candidate) {
		if (this.userCheckService.checkIfRealPerson(candidate) == false) {
			return new ErrorResult("Kullanıcı bilgileri hatalı");
		}

		return new SuccessResult();
	}

	private Result isEmailVerified(String email) {
		if (!this.emailValidationService.isEmailVerified(email)) {
			return new ErrorResult("Lütfen epostanıza gelen doğrulama linkine tıklayınız");
		}

		return new SuccessResult("Email doğrulandı: " + email);
	}


}
