import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProdutoDAO {

    private Conexao conexao = new Conexao();
    
    public void inserir(Produto produto) {
        String sql = "INSERT INTO produtos(nome, preco, quantidade) VALUES (?, ?, ?)";

        try (Connection conex = conexao.conectar();
             PreparedStatement c1 = conex.prepareStatement(sql)) {

            c1.setString(1, produto.getNome());
            c1.setDouble(2, produto.getPreco());
            c1.setInt(3, produto.getQuantidade());

            c1.executeUpdate();
            System.out.println("\nProduto inserido com sucesso!\n");

        } catch (SQLException e) {
            System.err.println("Falha ao inserir produto: " + e.getMessage());
        }
    }

    public ArrayList<Produto> buscarPorNome(String nomeBusca) {
        ArrayList<Produto> listaProdutos = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE nome LIKE ?";

        try (Connection conex = conexao.conectar();
             PreparedStatement c1 = conex.prepareStatement(sql)) {

            c1.setString(1, "%" + nomeBusca + "%");

            try (ResultSet resultado = c1.executeQuery()) {
                while (resultado.next()) {
                    int id = resultado.getInt("id");
                    String nome = resultado.getString("nome");
                    Double preco = resultado.getDouble("preco");
                    int quantidade = resultado.getInt("quantidade");

                    Produto produto = new Produto(id, nome, preco, quantidade);
                    listaProdutos.add(produto);
                }
            }

        } catch (SQLException e) {
            System.err.println("Falha ao buscar produto: " + e.getMessage());
        }

        return listaProdutos;
    }

    public boolean atualizarEstoque(int id, int novaQuantidade) {
        String sql = "UPDATE produtos SET quantidade = ? WHERE id = ?";

        try (Connection conex = conexao.conectar();
             PreparedStatement c1 = conex.prepareStatement(sql)) {

            c1.setInt(1, novaQuantidade);
            c1.setInt(2, id);

            int linhasAfetadas = c1.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("\nEstoque atualizado!\n");
                return true;
            } else {
                System.out.println("\nNenhum produto encontrado com o id " + id + " informado!\n");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Falha ao atualizar estoque: " + e.getMessage());
            return false;
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conex = conexao.conectar();
             PreparedStatement c1 = conex.prepareStatement(sql)) {

            c1.setInt(1, id);
            int linhasAfetadas = c1.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("\nProduto excluído com sucesso!\n");
                return true;
            } else {
                System.out.println("\nNenhum produto encontrado com o id " + id + " informado!");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Falha ao excluir produto: " + e.getMessage());
            return false;
        }
    }
}
