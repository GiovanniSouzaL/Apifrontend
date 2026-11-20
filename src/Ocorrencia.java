import java.time.Instant;

public class Ocorrencia {

    public long id;
    public String titulo;
    public String descricao;
    public String categoria;
    public String bairro;
    public String endereco;
    public String status;
    public String nomeCidadao;
    public Instant dataCriacao;

    public String toJson() {
        return "{"
                + "\"id\":" + id
                + ",\"titulo\":" + Json.texto(titulo)
                + ",\"descricao\":" + Json.texto(descricao)
                + ",\"categoria\":" + Json.texto(categoria)
                + ",\"bairro\":" + Json.texto(bairro)
                + ",\"endereco\":" + Json.texto(endereco)
                + ",\"status\":" + Json.texto(status)
                + ",\"nomeCidadao\":" + Json.texto(nomeCidadao)
                + ",\"dataCriacao\":" + Json.texto(dataCriacao.toString())
                + "}";
    }
}
