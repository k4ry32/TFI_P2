package dao;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;

public interface GenericDao<T> {
    
    public boolean crear(T entidad, Connection conn) throws Exception;
    
    public Optional<T> leer(long id, Connection conn) throws Exception;
    
    public List<T> leerTodos(Connection conn) throws Exception;
    
    public boolean actualizar(T entidad, Connection conn) throws Exception;
    
    public boolean eliminar(long id, Connection conn) throws Exception;
}