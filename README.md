# API de Votação em Cooperativa

API REST desenvolvida em **Java com Spring Boot** para atender ao desafio técnico de votação em cooperativa. A aplicação permite cadastrar pautas, abrir sessões de votação, registrar votos únicos por associado e consultar o resultado da apuração.

A solução foi mantida propositalmente simples, com foco no fluxo solicitado pelo enunciado, organização clara do código, tratamento padronizado de erros, documentação via Swagger/OpenAPI, logs de aplicação e testes automatizados.

## Tecnologias utilizadas

| Tecnologia | Uso no projeto |
|---|---|
| Java 17 | Linguagem principal da aplicação. |
| Spring Boot | Criação da API REST e configuração da aplicação. |
| Spring Web | Exposição dos endpoints HTTP. |
| Spring Data JPA | Persistência das entidades no banco de dados. |
| MySQL | Banco principal da aplicação. |
| H2 | Banco em memória utilizado apenas nos testes automatizados. |
| JUnit 5, MockMvc e Spring Boot Test | Testes automatizados da API e da integração externa. |
| SpringDoc OpenAPI | Documentação da API pelo Swagger. |

## Requisitos atendidos

| Requisito | Implementação |
|---|---|
| Cadastrar nova pauta | `POST /api/v1/pautas`, recebendo `titulo` e `descricao`. |
| Abrir sessão de votação | `POST /api/v1/pautas/{pautaId}/sessoes`, com duração opcional. |
| Duração padrão da sessão | Quando a duração não é informada, a sessão é aberta por 1 minuto. |
| Registrar voto | `POST /api/v1/pautas/{pautaId}/votos`, recebendo `associadoId`, `cpf` e `escolha`. |
| Aceitar apenas Sim ou Não | O campo `escolha` aceita somente `SIM` ou `NAO`. |
| Impedir voto duplicado | A aplicação valida voto duplicado e o banco possui restrição única por pauta e associado. |
| Consultar resultado | `GET /api/v1/pautas/{pautaId}/resultado`, retornando votos `SIM`, votos `NAO`, total e situação final. |
| Persistir dados | A execução principal usa MySQL, mantendo pautas, sessões e votos salvos após reiniciar a aplicação. |
| Retornar JSON para cliente mobile | As respostas seguem os padrões `FORMULARIO` e `SELECAO`. |
| Integração externa de CPF | Implementada de forma configurável no serviço `ClienteCpfServico`. |
| Performance | A apuração utiliza contagem no banco de dados, sem carregar todos os votos em memória. |
| Versionamento da API | Todos os endpoints públicos estão versionados com `/api/v1`. |

## Endpoints da API

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/pautas` | Cadastra uma nova pauta. |
| `POST` | `/api/v1/pautas/{pautaId}/sessoes` | Abre uma sessão de votação para a pauta informada. |
| `POST` | `/api/v1/pautas/{pautaId}/votos` | Registra o voto de um associado. |
| `GET` | `/api/v1/pautas/{pautaId}/resultado` | Consulta o resultado da votação. |

A documentação interativa da API fica disponível pelo Swagger após a aplicação iniciar.

```text
http://localhost:8080/swagger-ui/index.html
```

## Estrutura do projeto

A arquitetura segue uma divisão simples e comum em aplicações Spring Boot, evitando camadas desnecessárias e mantendo as responsabilidades bem separadas.

| Pacote | Responsabilidade |
|---|---|
| `controle` | Recebe as requisições HTTP e expõe os endpoints REST. |
| `servico` | Concentra as regras de negócio da votação e da integração de CPF. |
| `repositorio` | Realiza o acesso ao banco de dados com Spring Data JPA. |
| `dominio` | Contém as entidades e enums principais do domínio. |
| `dto` | Define os objetos de entrada e saída usados pela API. |
| `configuracao` | Centraliza configurações, Swagger e tratamento global de erros. |
| `excecao` | Contém a exceção de regra de negócio da aplicação. |

## Banco de dados

A aplicação principal usa **MySQL**. O banco H2 é utilizado somente nos testes automatizados, permitindo executar a suíte de testes sem depender de um MySQL local.

Antes de iniciar a aplicação localmente, crie o banco:

```sql
CREATE DATABASE IF NOT EXISTS desafio_votos;
```

As configurações principais ficam em `src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/desafio_votos?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

Caso o MySQL local use outro usuário ou senha, basta alterar as propriedades `spring.datasource.username` e `spring.datasource.password`.

## Como executar localmente

Para executar o projeto, é necessário ter **Java 17**, **Maven** e **MySQL** instalados. Na raiz do projeto, execute:

```bash
mvn spring-boot:run
```

Depois, acesse o Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Fluxo básico de uso

O fluxo principal da aplicação é composto por quatro etapas: criar uma pauta, abrir a sessão de votação, registrar votos e consultar o resultado.

### 1. Criar pauta

```http
POST /api/v1/pautas
Content-Type: application/json

{
  "titulo": "Aprovação do novo estatuto",
  "descricao": "Votação para aprovar ou rejeitar o novo estatuto."
}
```

### 2. Abrir sessão

```http
POST /api/v1/pautas/1/sessoes
Content-Type: application/json

{
  "durationMinutes": 5
}
```

Se o corpo da requisição não for enviado, a sessão será aberta com a duração padrão de **1 minuto**.

### 3. Registrar voto

```http
POST /api/v1/pautas/1/votos
Content-Type: application/json

{
  "associadoId": "assoc-001",
  "cpf": "12345678909",
  "escolha": "SIM"
}
```

### 4. Consultar resultado

```http
GET /api/v1/pautas/1/resultado
```

Exemplo de resposta:

```json
{
  "tipo": "FORMULARIO",
  "titulo": "Resultado da votação",
  "itens": [
    { "tipo": "TEXTO", "texto": "Pauta: Aprovação do novo estatuto" },
    { "tipo": "TEXTO", "texto": "Votos SIM: 1" },
    { "tipo": "TEXTO", "texto": "Votos NAO: 0" },
    { "tipo": "TEXTO", "texto": "Total de votos: 1" },
    { "tipo": "TEXTO", "texto": "Resultado: APROVADA" }
  ],
  "botaoOk": {
    "titulo": "Atualizar",
    "url": "http://localhost:8080/api/v1/pautas/1/resultado",
    "body": {
      "pautaId": 1
    }
  }
}
```

## Integração externa de CPF

A integração com o serviço externo de CPF está implementada e pode ser habilitada por configuração. Por padrão, ela fica desabilitada para facilitar a execução local e evitar dependência de instabilidade externa durante os testes.

```properties
integracao.cpf.habilitada=false
```

Para habilitar a chamada real ao serviço externo, altere as propriedades abaixo:

```properties
integracao.cpf.habilitada=true
integracao.cpf.url-base=https://user-info.herokuapp.com/users
integracao.cpf.timeout-segundos=3
```

Quando habilitada, a API consulta o CPF antes de registrar o voto. O voto é permitido apenas quando o serviço externo retorna `ABLE_TO_VOTE`. Caso retorne `UNABLE_TO_VOTE`, `404` ou ocorra falha de comunicação, a aplicação bloqueia o voto e retorna erro padronizado em JSON.

## Performance

A apuração dos votos foi implementada com consultas de contagem diretamente no banco de dados, usando métodos de repositório baseados em `countByPautaIdAndEscolha`. Dessa forma, a aplicação não precisa carregar todos os votos em memória para calcular o resultado.

Além disso, a entidade de voto possui restrição única para impedir que o mesmo associado vote mais de uma vez na mesma pauta, reforçando a consistência também no nível do banco de dados.

## Versionamento da API

A API utiliza versionamento por caminho de URL. Todos os endpoints públicos da versão atual começam com `/api/v1`.

Essa estratégia foi escolhida por ser simples, explícita e fácil de validar pelo Swagger. Caso surja uma mudança incompatível no contrato da API, uma nova versão poderá ser criada em `/api/v2`, mantendo a versão `/api/v1` disponível para clientes antigos.

## Tratamento de erros

O tratamento de erros é centralizado em um manipulador global, garantindo respostas padronizadas para falhas de validação, regras de negócio, JSON inválido e violações de integridade no banco.

As respostas de erro também seguem o formato `FORMULARIO`, mantendo consistência com o contrato de resposta esperado pela aplicação cliente.

## Logs da aplicação

A aplicação registra logs nos principais pontos do fluxo de negócio, como criação de pauta, abertura de sessão, registro de voto, cálculo de resultado, validação de CPF e tratamento de erros.

Esses logs facilitam o acompanhamento da execução e ajudam no diagnóstico de problemas durante testes locais ou execução em nuvem.

## Testes automatizados

Para executar os testes, use:

```bash
mvn clean test
```

A suíte cobre o fluxo principal da votação, duração padrão da sessão, bloqueio de voto duplicado, tentativa de voto sem sessão aberta, escolha de voto inválida e os principais cenários da integração externa de CPF.

| Classe de teste | Cobertura |
|---|---|
| `VotacaoApiIntegracaoTest` | Testa o fluxo da API, regras de votação e respostas padronizadas. |
| `ClienteCpfServicoTest` | Testa a integração de CPF com respostas simuladas para `ABLE_TO_VOTE`, `UNABLE_TO_VOTE` e `404`. |

## Decisões técnicas

A solução foi construída com Spring Boot por ser uma escolha direta e adequada para APIs REST em Java. A arquitetura separa controller, service, repository, domínio e DTOs, mantendo o código organizado sem adicionar complexidade desnecessária.

A persistência principal foi feita com MySQL para garantir que pautas, sessões e votos permaneçam salvos após reiniciar a aplicação. Nos testes automatizados, foi utilizado H2 em memória para simplificar a execução da suíte sem exigir banco externo.

A validação externa de CPF foi isolada em um serviço próprio e controlada por configuração, permitindo ativar o bônus quando necessário sem prejudicar os testes locais. A apuração do resultado foi implementada com contagem no banco para melhorar a eficiência em cenários com muitos votos.

O versionamento por URL com `/api/v1` foi escolhido por ser simples, claro e suficiente para o escopo do desafio. O tratamento global de erros e os logs de negócio foram adicionados para melhorar a legibilidade, a manutenção e o diagnóstico da aplicação.

## Execução em nuvem

Em ambiente de nuvem, as configurações sensíveis devem ser fornecidas por variáveis de ambiente, sem alterar o código-fonte.

| Variável | Finalidade |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexão com o MySQL em nuvem. |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco de dados. |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados. |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estratégia de atualização do schema, como `update`. |
| `INTEGRACAO_CPF_HABILITADA` | Define se a integração externa de CPF ficará ativa. |
| `APP_BASE_URL` | URL pública da API usada nas respostas com links de próxima ação. |

Após o deploy, o Swagger ficará disponível na URL pública da aplicação, no caminho:

```text
/swagger-ui/index.html
```

## Observações finais

O projeto foi desenvolvido com foco em simplicidade, legibilidade e atendimento direto aos requisitos do desafio. A API expõe apenas os endpoints necessários para o fluxo de votação, mantém as regras de negócio em serviços, usa persistência em banco relacional, possui tratamento de erros padronizado, contém logs nos pontos relevantes e inclui testes automatizados para validar os principais cenários.

## Deploy em produção

A API está publicada no Railway e pode ser acessada por HTTPS em:

https://desafio-votos-production.up.railway.app/swagger-ui/index.html
