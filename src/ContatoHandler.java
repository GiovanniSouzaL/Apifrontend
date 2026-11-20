import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContatoHandler implements HttpHandler {

    private final Repositorio repositorio;

    public ContatoHandler(Repositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void handle(HttpExchange troca) throws IOException {
        Http.liberarCors(troca);
        try {
            String metodo = troca.getRequestMethod();
            if (metodo.equals("OPTIONS")) {
                Http.responder(troca, 204, null);
                return;
            }
            String[] partes = Http.partesDoCaminho(troca);
            if (partes.length != 1 || !partes[0].equals("contatos")) {
                throw new ErroApi(404, "Rota não encontrada");
            }
            if (!metodo.equals("POST")) {
                throw new ErroApi(405, "Método não permitido");
            }
            Map<String, String> campos = Http.lerJson(troca);
            List<String> erros = new ArrayList<>();
            Contato novo = new Contato();
            novo.nome = Validador.texto(campos, "nome", 2, 60, erros);
            novo.email = Validador.email(campos, "email", erros);
            novo.assunto = Validador.texto(campos, "assunto", 3, 80, erros);
            novo.mensagem = Validador.texto(campos, "mensagem", 10, 500, erros);
            if (!erros.isEmpty()) {
                throw new ErroApi(400, "Dados inválidos", erros);
            }
            Contato salvo = repositorio.salvarContato(novo);
            troca.getResponseHeaders().set("Location", "/contatos/" + salvo.id);
            Http.responder(troca, 201, salvo.toJson());
        } catch (ErroApi erro) {
            Http.responderErro(troca, erro);
        } catch (Exception erro) {
            erro.printStackTrace();
            Http.responderErro(troca, new ErroApi(500, "Erro interno no servidor"));
        }
    }
}
