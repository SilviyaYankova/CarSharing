package carsharing;

import carsharing.entity.Company;
import carsharing.repository.*;
import carsharing.repository.impl.CarRepositoryImpl;
import carsharing.repository.impl.CompanyRepositoryImpl;
import carsharing.repository.impl.CustomerRepositoryImpl;
import carsharing.view.Dialog;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    static final String DB_NAME = "carsharing";
    //task
    static final String DB_URL = "jdbc:h2:file:./src/carsharing/db/";
    // local
//    static final String DB_URL = "jdbc:h2:file:./src/carsharing/";


    public static void main(String[] args) throws SQLException {
        try (var connection = DriverManager.getConnection(DB_URL + DB_NAME)) {
            Repository<Company> companyRepository = new CompanyRepositoryImpl(connection);
            CarRepository carRepository = new CarRepositoryImpl(connection);
            CustomerRepository customerRepository = new CustomerRepositoryImpl(connection);
            connection.setAutoCommit(true);
            new Dialog(companyRepository, carRepository, customerRepository).run();
        }
    }
}