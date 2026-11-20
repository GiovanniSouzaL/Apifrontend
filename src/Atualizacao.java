import java.time.Instant;

public class Atualizacao {

    public long id;
    public long ocorrenciaId;
    public String mensagem;
    public String autor;
    public Instant dataCriacao;

    public String toJson() {
        return "{"
                + "\"id\":" + id
                + ",\"ocorrenciaId\":" + ocorrenciaId
                + ",\"mensagem\":" + Json.texto(mensagem)
                + ",\"autor\":" + Json.texto(autor)
                + ",\"dataCriacao\":" + Json.texto(dataCriacao.toString())
                + "}";
    }
}
