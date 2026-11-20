# Cidade em Dia - API Java

API REST em Java usada pelo front-end do projeto **Cidade em Dia** (Global Solution 2026/2 - CitizenTech, disciplina Front-End Design Engineering).

Repositório do front-end: https://github.com/GiovanniSouzaL/cidade-em-dia

## Objetivo

Guardar e entregar para o front-end as ocorrências de zeladoria registradas pelos cidadãos (buraco, lixo, iluminação, alagamento...), as atualizações de cada ocorrência e as mensagens da página de contato.

É uma API bem básica: usa só o JDK (pacote `com.sun.net.httpserver`), sem frameworks, e guarda os dados **em memória**. Quando o servidor reinicia, os dados voltam para os exemplos iniciais.

## Tecnologias

- Java 21 (funciona também em Java 17)
- JDK `HttpServer`, sem Spring e sem bibliotecas externas
- Docker (deploy no Render)

## Estrutura

```
src/
  Main.java                inicia o servidor e registra as rotas
  OcorrenciaHandler.java   rotas /ocorrencias e /ocorrencias/{id}/atualizacoes
  ContatoHandler.java      rota /contatos
  RaizHandler.java         rota / (mostra que a API está no ar)
  Repositorio.java         dados em memória e exemplos iniciais
  Ocorrencia.java, Atualizacao.java, Contato.java   modelos
  Validador.java           validação dos campos
  Http.java, Json.java, LeitorJson.java, ErroApi.java   apoio (resposta, CORS, JSON, erros)
Dockerfile
```

## Endpoints

| Método | Rota | Sucesso | Erros |
| --- | --- | --- | --- |
| GET | `/` | 200 | - |
| GET | `/ocorrencias` | 200 | - |
| POST | `/ocorrencias` | 201 + Location | 400 |
| GET | `/ocorrencias/{id}` | 200 | 404 |
| PUT | `/ocorrencias/{id}` | 200 | 400, 404 |
| DELETE | `/ocorrencias/{id}` | 204 | 404 |
| GET | `/ocorrencias/{id}/atualizacoes` | 200 | 404 |
| POST | `/ocorrencias/{id}/atualizacoes` | 201 + Location | 400, 404, 409 |
| POST | `/contatos` | 201 + Location | 400 |

Métodos não permitidos devolvem 405 e erros inesperados devolvem 500 com mensagem genérica. O CORS está liberado para qualquer origem.

Todo erro volta em JSON:

```json
{ "status": 400, "erro": "Dados inválidos", "detalhes": ["titulo: deve ter entre 5 e 80 caracteres"] }
```

O 409 acontece quando alguém tenta adicionar uma atualização em uma ocorrência que já está `RESOLVIDA`.

### Exemplo de ocorrência (POST /ocorrencias)

Entrada:

```json
{
  "titulo": "Buraco grande na Rua das Acácias",
  "descricao": "Buraco no meio da rua, perto do número 120.",
  "categoria": "BURACO",
  "bairro": "Vila Mariana",
  "endereco": "Rua das Acácias, 120",
  "nomeCidadao": "Mariana S."
}
```

Saída (201):

```json
{
  "id": 9,
  "titulo": "Buraco grande na Rua das Acácias",
  "descricao": "Buraco no meio da rua, perto do número 120.",
  "categoria": "BURACO",
  "bairro": "Vila Mariana",
  "endereco": "Rua das Acácias, 120",
  "status": "ABERTA",
  "nomeCidadao": "Mariana S.",
  "dataCriacao": "2026-11-19T17:54:24.842Z"
}
```

Valores aceitos:

- `categoria`: BURACO, ILUMINACAO, LIXO, ALAGAMENTO, CALCADA, OUTRO
- `status` (só no PUT): ABERTA, EM_ANDAMENTO, RESOLVIDA

### Exemplo de atualização (POST /ocorrencias/{id}/atualizacoes)

```json
{ "mensagem": "Equipe a caminho do local.", "autor": "Equipe de zeladoria" }
```

### Exemplo de contato (POST /contatos)

```json
{ "nome": "Giovanni", "email": "giovanni@exemplo.com", "assunto": "Elogio", "mensagem": "Muito bom o projeto!" }
```

## Como rodar no computador

Precisa do JDK 17 ou 21 instalado.

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

A API sobe em http://localhost:8080. Para testar, abra http://localhost:8080/ocorrencias no navegador.

## Deploy no Render

1. Suba esta pasta para um repositório no GitHub.
2. No Render, clique em **New** > **Web Service** e escolha o repositório.
3. Em **Language**, escolha **Docker** (o Render usa o `Dockerfile` da raiz) e o plano **Free**.
4. Clique em **Deploy Web Service** e espere o build terminar.
5. A URL pública aparece no topo da página do serviço (`https://nome-do-servico.onrender.com`). Essa URL vai no front-end e no README.

No plano gratuito o Render "dorme" depois de alguns minutos sem uso, então o primeiro acesso pode levar cerca de 1 minuto.

## URL da API publicada

https://SUBSTITUIR-PELA-URL-DO-RENDER.onrender.com

## Integrante

- Giovanni de Souza Lima - RM 556536 - Turma 1TDSPI

## Uso de IA generativa

Foi usada a ferramenta Claude (Anthropic) como apoio na escrita do código desta API e deste README. O autor leu, executou e testou o projeto antes da entrega.
