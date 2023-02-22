package carsharing.repository.impl;

import carsharing.entity.Customer;
import carsharing.repository.CustomerRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryImpl implements CustomerRepository {
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS CUSTOMER " +
                    "(\n" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, \n" +
                    "name VARCHAR(255) UNIQUE NOT NULL,\n" +
                    "rented_car_id INT DEFAULT NULL,\n" +
                    "foreign key (rented_car_id) references CAR (id)\n" +
                    ");";

    private static final String ADD_ONE = "INSERT INTO CUSTOMER (NAME) VALUES (?)";
    private static final String GET_ALL = "SELECT * FROM CUSTOMER";
    private static final String UPDATE_CUSTOMER_WITH_CAR = "UPDATE CUSTOMER SET rented_car_id = ? where id = ?";
    private static final String UPDATE_CUSTOMER_WITHOUT_CAR = "UPDATE CUSTOMER SET rented_car_id = NULL where id = ?";


    Connection connection;

    public CustomerRepositoryImpl(Connection connection) throws SQLException {
        this.connection = connection;
        try (var prepStmt = connection.prepareStatement(CREATE_TABLE)) {
            prepStmt.executeUpdate();
        }
    }

    @Override
    public Customer add(Customer customer) throws SQLException {
        try (var prepStmt = connection.prepareStatement(ADD_ONE)) {
            prepStmt.setString(1, customer.getName());
            prepStmt.executeUpdate();
            return customer;
        }
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        List<Customer> result = new ArrayList<>();
        try (var resultSet = connection.prepareStatement(GET_ALL).executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                long rentedCarId = resultSet.getLong("rented_car_id");
                Customer customer = new Customer(name);
                customer.setId(id);
                customer.setRentedCarId(rentedCarId);
                result.add(customer);
            }
        }
        return result;
    }

    @Override
    public Customer getById(Long companyId) throws SQLException {
        return null;
    }

    @Override
    public Customer updateCustomer(Customer customer) throws SQLException {
        if (customer.getRentedCarId() != null) {
            try (var prepStmt = connection.prepareStatement(UPDATE_CUSTOMER_WITH_CAR)) {
                prepStmt.setLong(1, customer.getRentedCarId());
                prepStmt.setLong(2, customer.getId());
                prepStmt.executeUpdate();
                return customer;
            }
        } else {
            try (var prepStmt = connection.prepareStatement(UPDATE_CUSTOMER_WITHOUT_CAR)) {
                prepStmt.setLong(1, customer.getId());
                prepStmt.executeUpdate();
                return customer;
            }
        }
    }
}
