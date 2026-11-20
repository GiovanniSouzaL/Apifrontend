import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Repositorio {

    private final List<Ocorrencia> ocorrencias = new ArrayList<>();
    private final List<Atualizacao> atualizacoes = new ArrayList<>();
    private final List<Contato> contatos = new ArrayList<>();

    private long proximaOcorrencia = 1;
    private long proximaAtualizacao = 1;
    private long proximoContato = 1;

    public Repositorio() {
        carregarExemplos();
    }

    public synchronized List<Ocorrencia> listarOcorrencias() {
        List<Ocorrencia> copia = new ArrayList<>(ocorrencias);
        copia.sort(Comparator.comparing((Ocorrencia o) -> o.dataCriacao).reversed());
        return copia;
    }

    public synchronized Ocorrencia buscarOcorrencia(long id) {
        for (Ocorrencia o : ocorrencias) {
            if (o.id == id) {
                return o;
            }
        }
        return null;
    }

    public synchronized Ocorrencia salvarOcorrencia(Ocorrencia nova) {
        nova.id = proximaOcorrencia++;
        nova.dataCriacao = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ocorrencias.add(nova);
        return nova;
    }

    public synchronized Ocorrencia atualizarOcorrencia(long id, Ocorrencia dados) {
        for (int i = 0; i < ocorrencias.size(); i++) {
            Ocorrencia atual = ocorrencias.get(i);
            if (atual.id == id) {
                dados.id = atual.id;
                dados.dataCriacao = atual.dataCriacao;
                ocorrencias.set(i, dados);
                return dados;
            }
        }
        return null;
    }

    public synchronized boolean removerOcorrencia(long id) {
        boolean removida = ocorrencias.removeIf(o -> o.id == id);
        atualizacoes.removeIf(a -> a.ocorrenciaId == id);
        return removida;
    }

    public synchronized List<Atualizacao> listarAtualizacoes(long ocorrenciaId) {
        List<Atualizacao> resultado = new ArrayList<>();
        for (Atualizacao a : atualizacoes) {
            if (a.ocorrenciaId == ocorrenciaId) {
                resultado.add(a);
            }
        }
        resultado.sort(Comparator.comparing((Atualizacao a) -> a.dataCriacao));
        return resultado;
    }

    public synchronized Atualizacao salvarAtualizacao(Atualizacao nova) {
        nova.id = proximaAtualizacao++;
        nova.dataCriacao = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        atualizacoes.add(nova);
        return nova;
    }

    public synchronized Contato salvarContato(Contato novo) {
        novo.id = proximoContato++;
        novo.dataCriacao = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        contatos.add(novo);
        return novo;
    }

    private void carregarExemplos() {
        long id = exemplo("Buraco grande na Rua das Acácias",
                "Buraco no meio da rua, perto do número 120. Os carros desviam para a outra faixa e já houve quase acidente.",
                "BURACO", "Vila Mariana", "Rua das Acácias, altura do número 120", "EM_ANDAMENTO", "Mariana S.", 6);
        exemploAtualizacao(id, "Recebemos o chamado e encaminhamos para a equipe de manutenção da via.", "Equipe de zeladoria", 5);
        exemploAtualizacao(id, "A equipe visitou o local e fez a medição do buraco. O reparo está programado.", "Equipe de zeladoria", 3);

        exemplo("Poste sem luz na praça do bairro",
                "O poste em frente à escola está apagado há mais de uma semana e a praça fica muito escura à noite.",
                "ILUMINACAO", "Itaquera", "Praça Central, em frente à escola", "ABERTA", "Carlos E.", 2);

        id = exemplo("Lixo acumulado na esquina",
                "Sacos de lixo e entulho acumulados na esquina, atraindo ratos e causando mau cheiro.",
                "LIXO", "Lapa", "Esquina da Rua do Mercado com a Rua Nova", "RESOLVIDA", "Juliana P.", 10);
        exemploAtualizacao(id, "Coleta especial agendada para o fim de semana.", "Equipe de zeladoria", 8);
        exemploAtualizacao(id, "Limpeza concluída. Se o problema voltar, registre uma nova ocorrência.", "Equipe de zeladoria", 7);

        exemplo("Alagamento na descida da avenida",
                "Sempre que chove forte, a água acumula na descida e os pedestres não conseguem atravessar.",
                "ALAGAMENTO", "Santo Amaro", "Avenida do Rio, próximo ao ponto de ônibus", "ABERTA", "Roberto A.", 1);

        id = exemplo("Calçada quebrada sem rampa de acesso",
                "A calçada está quebrada e não tem rampa. Cadeirantes e idosos precisam andar pela rua.",
                "CALCADA", "Mooca", "Rua Dom Pedro, em frente ao número 45", "EM_ANDAMENTO", "Fernanda L.", 8);
        exemploAtualizacao(id, "Vistoria marcada para esta semana.", "Equipe de zeladoria", 4);

        exemplo("Entulho jogado na beira do córrego",
                "Entulho de obra foi descartado na beira do córrego e pode entupir a passagem da água.",
                "LIXO", "Penha", "Rua do Córrego, final da rua", "ABERTA", "Paulo R.", 4);

        id = exemplo("Semáforo apagado no cruzamento",
                "O semáforo ficou apagado no horário de pico e o trânsito parou por causa da falta de sinalização.",
                "OUTRO", "Tatuapé", "Cruzamento da Rua Azul com a Avenida Verde", "RESOLVIDA", "Beatriz M.", 12);
        exemploAtualizacao(id, "Técnico enviado ao local.", "Equipe de zeladoria", 11);
        exemploAtualizacao(id, "Semáforo religado e funcionando normalmente.", "Equipe de zeladoria", 11);

        exemplo("Lâmpada queimada no túnel de pedestres",
                "As lâmpadas do túnel de pedestres estão queimadas e a passagem fica escura, o que causa medo de assalto.",
                "ILUMINACAO", "Pinheiros", "Túnel de pedestres da estação", "ABERTA", "Lucas T.", 3);
    }

    private long exemplo(String titulo, String descricao, String categoria, String bairro, String endereco,
            String status, String nome, int diasAtras) {
        Ocorrencia o = new Ocorrencia();
        o.id = proximaOcorrencia++;
        o.titulo = titulo;
        o.descricao = descricao;
        o.categoria = categoria;
        o.bairro = bairro;
        o.endereco = endereco;
        o.status = status;
        o.nomeCidadao = nome;
        o.dataCriacao = Instant.now().minus(diasAtras, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        ocorrencias.add(o);
        return o.id;
    }

    private void exemploAtualizacao(long ocorrenciaId, String mensagem, String autor, int diasAtras) {
        Atualizacao a = new Atualizacao();
        a.id = proximaAtualizacao++;
        a.ocorrenciaId = ocorrenciaId;
        a.mensagem = mensagem;
        a.autor = autor;
        a.dataCriacao = Instant.now().minus(diasAtras, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        atualizacoes.add(a);
    }
}
