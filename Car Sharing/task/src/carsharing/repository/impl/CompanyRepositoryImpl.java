package carsharing.repository.impl;

import carsharing.entity.Car;
import carsharing.entity.Company;
import carsharing.repository.CompanyRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyRepositoryImpl implements CompanyRepository {
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS COMPANY  " +
                    "(id INT PRIMARY KEY AUTO_INCREMENT, " +
                    " name VARCHAR(255) UNIQUE NOT NULL )";

    private static final String ADD_ONE = "INSERT INTO COMPANY (NAME) VALUES (?)";
    private static final String GET_ALL = "SELECT * FROM COMPANY ORDER BY ID";
    private static final String GET_BY_ID = "SELECT * FROM COMPANY WHERE ID = ?";

    Connection connection;

    public CompanyRepositoryImpl(Connection connection) throws SQLException {
        this.connection = connection;
        try (var prepStmt = connection.prepareStatement(CREATE_TABLE)) {
            prepStmt.executeUpdate();
        }
    }

    @Override
    public Company add(Company company) throws SQLException {
        try (var prepStmt = connection.prepareStatement(ADD_ONE)) {
            prepStmt.setString(1, company.getName());
            prepStmt.executeUpdate();
            return company;
        }
    }

    @Override
    public List<Company> getAll() throws SQLException {
        List<Company> result = new ArrayList<>();
        try (var resultSet = connection.prepareStatement(GET_ALL).executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Company company = new Company(name);
                company.setId(id);
                result.add(company);
            }
        }
        return result;
    }

    @Override
    public Company getById(Long companyId) throws SQLException {
        Company company = null;
        try (var stmt = connection.prepareStatement(GET_BY_ID)) {
            stmt.setLong(1, companyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                company = new Company(name);
                company.setId(id);
            }
        }
        return company;
    }
}
