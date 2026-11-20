import java.time.Instant;

public class Contato {

    public long id;
    public String nome;
    public String email;
    public String assunto;
    public String mensagem;
    public Instant dataCriacao;

    public String toJson() {
        return "{"
                + "\"id\":" + id
                + ",\"nome\":" + Json.texto(nome)
                + ",\"email\":" + Json.texto(email)
                + ",\"assunto\":" + Json.texto(assunto)
                + ",\"mensagem\":" + Json.texto(mensagem)
                + ",\"dataCriacao\":" + Json.texto(dataCriacao.toString())
                + "}";
    }
}
