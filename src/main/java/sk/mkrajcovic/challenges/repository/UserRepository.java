package sk.mkrajcovic.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
