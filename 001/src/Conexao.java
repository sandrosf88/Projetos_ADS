import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private String banco = "loja_estoque";
    private String usuario = "root";
    private String senha = "";
    private String url = "jdbc:mysql://localhost:3306/" + banco;

    public Connection conectar() {
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            throw new RuntimeException("Conexão com o banco de dados falhou: " + e.getMessage());
        }
    }
}
