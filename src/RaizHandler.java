import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class RaizHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange troca) throws IOException {
        Http.liberarCors(troca);
        try {
            String metodo = troca.getRequestMethod();
            if (metodo.equals("OPTIONS")) {
                Http.responder(troca, 204, null);
                return;
            }
            if (!troca.getRequestURI().getPath().equals("/")) {
                throw new ErroApi(404, "Rota não encontrada");
            }
            if (!metodo.equals("GET")) {
                throw new ErroApi(405, "Método não permitido");
            }
            Http.responder(troca, 200,
                    "{\"nome\":\"API Cidade em Dia\",\"situacao\":\"no ar\",\"recursos\":[\"/ocorrencias\",\"/contatos\"]}");
        } catch (ErroApi erro) {
            Http.responderErro(troca, erro);
        } catch (Exception erro) {
            erro.printStackTrace();
            Http.responderErro(troca, new ErroApi(500, "Erro interno no servidor"));
        }
    }
}
