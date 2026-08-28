package sk.mkrajcovic.challenges.repository.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

	@Query("""
		SELECT c.id as id,
		       c.brand as brand,
		       c.name as name,
		       c.horsePower as horsePower,
		       c.torque as torque,
		       c.wheelDrive as wheelDrive
		FROM Car c
	""")
	public List<CarData> findCarsByParams();

	interface CarData {
		Integer getId();
		String getBrand();
		String getName();
		int getHorsePower();
		int getTorque();
		String getWheelDrive();
	}

}
