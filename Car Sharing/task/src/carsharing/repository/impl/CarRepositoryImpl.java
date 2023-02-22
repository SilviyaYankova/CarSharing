package carsharing.repository.impl;

import carsharing.entity.Car;
import carsharing.repository.CarRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CarRepositoryImpl implements CarRepository {
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS CAR " +
                    "(\n" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, \n" +
                    "name VARCHAR(255) UNIQUE NOT NULL,\n" +
                    "company_id INT NOT NULL, \n" +
                    "foreign key (company_id) references COMPANY (id)\n" +
                    ");";
    private static final String ADD_ONE = "INSERT INTO CAR (NAME, COMPANY_ID) VALUES (?, ?)";
    private static final String GET_ALL = "SELECT * FROM CAR";
    private static final String GET_ALL_BY_COMPANY = "SELECT * FROM CAR WHERE COMPANY_ID = ?;";
    private static final String GET_BY_ID = "SELECT * FROM CAR WHERE ID = ?";

    Connection connection;

    public CarRepositoryImpl(Connection connection) throws SQLException {
        this.connection = connection;
        try (var prepStmt = connection.prepareStatement(CREATE_TABLE)) {
            prepStmt.executeUpdate();
        }
    }

    @Override
    public Car add(Car car) throws SQLException {
        try (var prepStmt = connection.prepareStatement(ADD_ONE)) {
            prepStmt.setString(1, car.getName());
            prepStmt.setLong(2, car.getCompanyId());
            prepStmt.executeUpdate();
            return car;
        }
    }

    @Override
    public List<Car> getAll() throws SQLException {
        List<Car> result = new ArrayList<>();
        try (var resultSet = connection.prepareStatement(GET_ALL).executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                long company_id = resultSet.getLong("company_id");
                Car car = new Car(name, company_id);
                car.setId(id);
                result.add(car);
            }
        }
        return result;
    }

    @Override
    public List<Car> getCarsByCompany(Long companyId) throws SQLException {
        List<Car> companyCars = new ArrayList<>();
        try (var prepStmt = connection.prepareStatement(GET_ALL_BY_COMPANY)) {
            prepStmt.setLong(1, companyId);
            companyCars = getAll().stream()
                                  .filter(c -> Objects.equals(c.getCompanyId(), companyId))
                                  .collect(Collectors.toList());
        }

        return companyCars;
    }

    @Override
    public Car getById(Long rentedCarId) throws SQLException {
        Car car = null;
        try (var stmt = connection.prepareStatement(GET_BY_ID)) {
            stmt.setLong(1, rentedCarId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                long company_id = rs.getLong("company_id");
                car = new Car(name, company_id);
                car.setId(id);
            }
        }
        return car;
    }
}
