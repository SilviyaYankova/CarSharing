package carsharing.repository;

import java.sql.SQLException;
import java.util.List;

public interface Repository<T> {

    T add(T entity) throws SQLException;

    List<T> getAll() throws SQLException;

    T getById(Long companyId) throws SQLException;
}
