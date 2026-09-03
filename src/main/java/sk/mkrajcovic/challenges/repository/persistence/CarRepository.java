package sk.mkrajcovic.challenges.repository.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.model.read.CarDetail;
import sk.mkrajcovic.challenges.search.SearchCarsCriteria;

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
        WHERE (:#{#criteria.brand} IS NULL OR c.brandSearch LIKE %:#{#criteria.brand}%)
        AND (:#{#criteria.name} IS NULL OR c.nameSearch LIKE %:#{#criteria.name}%)
        AND (:#{#criteria.horsePower} IS NULL OR c.horsePower = :#{#criteria.horsePower})
        AND (:#{#criteria.torque} IS NULL OR c.torque = :#{#criteria.torque})
        AND (:#{#criteria.wheelDrive} IS NULL OR c.wheelDrive = :#{#criteria.wheelDrive})
    """)
	public List<CarDetail> findCars(@Param("criteria") SearchCarsCriteria criteria);

}
