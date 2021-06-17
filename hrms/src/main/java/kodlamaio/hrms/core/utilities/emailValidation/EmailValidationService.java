package kodlamaio.hrms.core.utilities.emailValidation;

public interface EmailValidationService {
	public void sendVerificationMaill(String email);
	public boolean isEmailVerified(String email);

}
