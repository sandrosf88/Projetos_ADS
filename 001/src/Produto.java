public class Produto {

    private int id;
    private String nome;
    private Double preco;
    private int quantidade;

    public Produto(int id, String nome, Double preco, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public Produto(String nome, Double preco, int quantidade) {
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void exibirProd() {
        System.out.println("Produto id: " + getId());
        System.out.println("Nome: " + getNome());
        System.out.printf("Preço R$: %.2f%n", getPreco());
        System.out.println("Quantidade: " + getQuantidade() + " unidades\n");
    }
}
