package carsharing.repository;

import carsharing.entity.Customer;

import java.sql.SQLException;

public interface CustomerRepository extends Repository<Customer> {
    Customer updateCustomer(Customer customer) throws SQLException;
}
