package carsharing.repository;

import carsharing.entity.Car;

import java.sql.SQLException;
import java.util.List;

public interface CarRepository extends Repository<Car> {
    List<Car> getCarsByCompany(Long id) throws SQLException;
}
