package kodlamaio.hrms.business.abstracts;

import kodlamaio.hrms.entities.concretes.User;

public interface UserService {
	void save(User user);
	User findByEmailAdressAndPassword(String email, String password);

}