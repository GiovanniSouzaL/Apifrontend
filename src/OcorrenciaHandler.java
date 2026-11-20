import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OcorrenciaHandler implements HttpHandler {

    private static final List<String> CATEGORIAS = List.of("BURACO", "ILUMINACAO", "LIXO", "ALAGAMENTO", "CALCADA", "OUTRO");
    private static final List<String> STATUS = List.of("ABERTA", "EM_ANDAMENTO", "RESOLVIDA");

    private final Repositorio repositorio;

    public OcorrenciaHandler(Repositorio repositorio) {
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
            if (!partes[0].equals("ocorrencias")) {
                throw new ErroApi(404, "Rota não encontrada");
            }
            if (partes.length == 1) {
                rotaColecao(troca, metodo);
            } else if (partes.length == 2) {
                rotaItem(troca, metodo, lerId(partes[1]));
            } else if (partes.length == 3 && partes[2].equals("atualizacoes")) {
                rotaAtualizacoes(troca, metodo, lerId(partes[1]));
            } else {
                throw new ErroApi(404, "Rota não encontrada");
            }
        } catch (ErroApi erro) {
            Http.responderErro(troca, erro);
        } catch (Exception erro) {
            erro.printStackTrace();
            Http.responderErro(troca, new ErroApi(500, "Erro interno no servidor"));
        }
    }

    private void rotaColecao(HttpExchange troca, String metodo) throws IOException {
        if (metodo.equals("GET")) {
            List<String> itens = new ArrayList<>();
            for (Ocorrencia o : repositorio.listarOcorrencias()) {
                itens.add(o.toJson());
            }
            Http.responder(troca, 200, Json.lista(itens));
        } else if (metodo.equals("POST")) {
            Ocorrencia nova = lerOcorrencia(Http.lerJson(troca), false);
            Ocorrencia salva = repositorio.salvarOcorrencia(nova);
            troca.getResponseHeaders().set("Location", "/ocorrencias/" + salva.id);
            Http.responder(troca, 201, salva.toJson());
        } else {
            throw new ErroApi(405, "Método não permitido");
        }
    }

    private void rotaItem(HttpExchange troca, String metodo, long id) throws IOException {
        Ocorrencia existente = repositorio.buscarOcorrencia(id);
        if (existente == null) {
            throw new ErroApi(404, "Ocorrência não encontrada");
        }
        if (metodo.equals("GET")) {
            Http.responder(troca, 200, existente.toJson());
        } else if (metodo.equals("PUT")) {
            Ocorrencia dados = lerOcorrencia(Http.lerJson(troca), true);
            Ocorrencia salva = repositorio.atualizarOcorrencia(id, dados);
            if (salva == null) {
                throw new ErroApi(404, "Ocorrência não encontrada");
            }
            Http.responder(troca, 200, salva.toJson());
        } else if (metodo.equals("DELETE")) {
            repositorio.removerOcorrencia(id);
            Http.responder(troca, 204, null);
        } else {
            throw new ErroApi(405, "Método não permitido");
        }
    }

    private void rotaAtualizacoes(HttpExchange troca, String metodo, long ocorrenciaId) throws IOException {
        Ocorrencia ocorrencia = repositorio.buscarOcorrencia(ocorrenciaId);
        if (ocorrencia == null) {
            throw new ErroApi(404, "Ocorrência não encontrada");
        }
        if (metodo.equals("GET")) {
            List<String> itens = new ArrayList<>();
            for (Atualizacao a : repositorio.listarAtualizacoes(ocorrenciaId)) {
                itens.add(a.toJson());
            }
            Http.responder(troca, 200, Json.lista(itens));
        } else if (metodo.equals("POST")) {
            Map<String, String> campos = Http.lerJson(troca);
            List<String> erros = new ArrayList<>();
            Atualizacao nova = new Atualizacao();
            nova.mensagem = Validador.texto(campos, "mensagem", 3, 300, erros);
            nova.autor = Validador.texto(campos, "autor", 2, 60, erros);
            if (!erros.isEmpty()) {
                throw new ErroApi(400, "Dados inválidos", erros);
            }
            if (ocorrencia.status.equals("RESOLVIDA")) {
                throw new ErroApi(409, "Ocorrência resolvida não aceita novas atualizações. Reabra a ocorrência para continuar.");
            }
            nova.ocorrenciaId = ocorrenciaId;
            Atualizacao salva = repositorio.salvarAtualizacao(nova);
            troca.getResponseHeaders().set("Location", "/ocorrencias/" + ocorrenciaId + "/atualizacoes");
            Http.responder(troca, 201, salva.toJson());
        } else {
            throw new ErroApi(405, "Método não permitido");
        }
    }

    private long lerId(String texto) {
        try {
            return Long.parseLong(texto);
        } catch (NumberFormatException e) {
            throw new ErroApi(404, "Ocorrência não encontrada");
        }
    }

    private Ocorrencia lerOcorrencia(Map<String, String> campos, boolean comStatus) {
        List<String> erros = new ArrayList<>();
        Ocorrencia o = new Ocorrencia();
        o.titulo = Validador.texto(campos, "titulo", 5, 80, erros);
        o.descricao = Validador.texto(campos, "descricao", 10, 500, erros);
        o.categoria = Validador.opcao(campos, "categoria", CATEGORIAS, erros);
        o.bairro = Validador.texto(campos, "bairro", 2, 60, erros);
        o.endereco = Validador.texto(campos, "endereco", 0, 120, erros);
        o.nomeCidadao = Validador.texto(campos, "nomeCidadao", 2, 60, erros);
        o.status = comStatus ? Validador.opcao(campos, "status", STATUS, erros) : "ABERTA";
        if (!erros.isEmpty()) {
            throw new ErroApi(400, "Dados inválidos", erros);
        }
        return o;
    }
}
