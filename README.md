# Cidade em Dia - API Java

API REST em Java usada pelo front-end do **Cidade em Dia** (Global Solution 2026/2 - CitizenTech, Front-End Design Engineering, FIAP).

## Links

| Item | Link |
| --- | --- |
| Repositório do front-end | https://github.com/GiovanniSouzaL/GS-frontend2026-2 |

## Objetivo

Guardar e entregar ao front-end as ocorrências de zeladoria (buraco, lixo, iluminação, alagamento...), as atualizações de cada ocorrência e as mensagens da página de contato.

É uma API básica: usa só o JDK (`com.sun.net.httpserver`), sem frameworks, e guarda os dados **em memória**. Ao reiniciar, os dados voltam aos 8 exemplos iniciais.

## Tecnologias

- Java 21 (funciona também no 17)
- JDK `HttpServer`, sem bibliotecas externas

## Estrutura

```
src/
  Main.java                inicia o servidor e registra as rotas
  OcorrenciaHandler.java   /ocorrencias e /ocorrencias/{id}/atualizacoes
  ContatoHandler.java      /contatos
  RaizHandler.java         / (mostra que a API está no ar)
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

Métodos não permitidos devolvem 405 e erros inesperados devolvem 500. O CORS está liberado para qualquer origem. Todo erro volta em JSON:

```json
{ "status": 400, "erro": "Dados inválidos", "detalhes": ["titulo: deve ter entre 5 e 80 caracteres"] }
```

O 409 acontece ao comentar em uma ocorrência já `RESOLVIDA`.

## Como executar

Pré-requisito: JDK 17 ou 21 (`javac -version` precisa responder). Na pasta do projeto:

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

A API sobe em http://localhost:8080. Teste em http://localhost:8080/ocorrencias. Para usar outra porta, defina a variável `PORT` antes de rodar.

## Integrante

Giovanni de Souza Lima - RM 556536 - Turma 1TDSPI (projeto individual)