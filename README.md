# Autoescola — Checkpoint 5

Evolução do [CP4 original](https://github.com/GabrielGaleraniAlmeida/autoescola-api), commit `8bf61d49382539ac4ba4cc922514a978f77b679f`. REST API em Java 17 / Spring Boot 3.5.6. Regras originais preservadas e ampliadas com ViaCEP, Swagger, CORS e testes.

## Integrantes

| Nome | RM |
|---|---|
| Gustavo Alves | 557876 |
| Gabriel Dias | 556830 |
| Gabriel Galerani | 557421 |
| Leonardo Taschin | 554583 |

## Executar

Instale Java 17 ou superior. Descompacte o ZIP e abra a pasta do projeto.

**Pelo executável:** após compilar, execute `java -jar target/autoescola-api-1.0.0.jar`. Os scripts `iniciar.bat` e `iniciar.sh` compilam automaticamente se necessário. O ZIP de distribuição também contém o JAR pronto em `release/`.

**Pelo código:** Windows: `mvnw.cmd clean verify` e `mvnw.cmd spring-boot:run`. Linux/macOS: `./mvnw clean verify` e `./mvnw spring-boot:run`. A primeira compilação precisa de internet para baixar Maven/dependências. Os testes usam banco em memória isolado.

A API abre em http://localhost:8080. Swagger: http://localhost:8080/swagger-ui/index.html. OpenAPI: http://localhost:8080/v3/api-docs.

O banco H2 padrão é persistente em `dados/`; os dados sobrevivem ao reinício. Não apague essa pasta para manter os cadastros. Não é preciso instalar PostgreSQL para executar.

## Login e teste no Swagger

1. Execute `POST /auth/login` com `{"username":"admin","senha":"admin123"}`.
2. Copie o campo `token` da resposta.
3. Clique **Authorize** no Swagger e cole somente o token.
4. Use os endpoints protegidos. Pelo curl/Postman, envie `Authorization: Bearer SEU_TOKEN`.

O administrador inicial é criado apenas se o username ainda não existir. As credenciais padrão são destinadas à demonstração acadêmica; configure `ADMIN_PASSWORD` e `JWT_SECRET` ao hospedar.

## Endpoints

| Recurso | Operações | Acesso |
|---|---|---|
| `/auth/login` | POST | Público |
| `/alunos` | POST, GET | Autenticado |
| `/alunos/{id}` | GET, PUT, DELETE lógico | Autenticado |
| `/instrutores` | POST, GET | Autenticado |
| `/instrutores/{id}` | GET, PUT, DELETE lógico | Autenticado |
| `/usuarios` | POST, GET | ADMIN |
| `/usuarios/{id}` | GET, PUT, DELETE lógico | ADMIN |
| `/usuarios/me/senha` | PATCH | Própria senha, autenticado |
| `/instrucoes` | POST, GET | Autenticado |
| `/instrucoes/{id}` | GET, PUT reagendar, DELETE cancelar | Autenticado |
| `/instrucoes/{id}/cancelar` | PATCH com motivo | Autenticado |
| `/enderecos/{cep}` | GET consulta ViaCEP | Autenticado |

Listagens paginadas: `?page=0&size=10`, padrão 10. Alunos/instrutores ordenados por nome, instruções por data. Cadastros inativos continuam disponíveis para consulta e histórico; não podem participar de novos agendamentos.

## Regras do CP4

- Aluno: nome, email, telefone, CPF e endereço completo obrigatórios. Instrutor: nome, email, telefone, CNH, especialidade e endereço. Especialidades: `MOTOS`, `CARROS`, `VANS`, `CAMINHOES`.
- Endereço: logradouro, bairro, cidade, UF com dois caracteres e CEP com oito dígitos (hífen opcional). Número e complemento opcionais.
- Atualização de aluno/instrutor altera somente nome, telefone e endereço. Email, CPF/CNH e especialidade permanecem imutáveis. Campos extras enviados são ignorados.
- Exclusão lógica (`ativo=false`); emails/documentos continuam únicos, inclusive nos registros inativos.
- Usuários: BCrypt, papéis ADMIN/USER, gerenciamento restrito ao ADMIN; troca da própria senha exige senha atual correta. Nenhuma senha/hash é exposta nos DTOs.
- Aulas duram uma hora, de segunda a sábado, com início entre 06h e 20h (encerramento até 21h), fuso `America/Sao_Paulo`. Antecedência mínima de 30 minutos.
- Máximo de duas instruções agendadas por aluno por dia. Aluno e instrutor devem estar ativos.
- Sem sobreposição de intervalos para aluno ou instrutor; aulas imediatamente consecutivas são permitidas. Se o instrutor não for informado, sorteio entre ativos e disponíveis.
- Cancelamento exige 24 horas de antecedência e motivo `ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU` ou `OUTROS`. Canceladas liberam agenda e limite diário, preservando histórico.
- GET por ID, PUT de reagendamento e DELETE de instrução complementam o CRUD do CP5. PUT também exige 24h no agendamento original e valida todas as regras do novo horário. DELETE cancela com motivo `OUTROS`, com a mesma antecedência; repetição sobre aula já cancelada retorna 204.

Bloqueios transacionais e versionamento das entidades protegem gravações concorrentes. Erros: 400 entrada inválida, 401 autenticação, 403 permissão/CORS, 404 inexistente, 409 duplicidade/conflito, 422 regra de negócio, 502 serviço externo indisponível.

## API externa e CORS

`GET /enderecos/01001-000` consulta **por HTTP** `https://viacep.com.br/ws/01001000/json/`. Retorna dados reais do serviço externo; formato inválido: 400, CEP inexistente: 404, falha/resposta inválida: 502. Timeouts de conexão/leitura: 3/5 segundos. A aplicação inicia mesmo se o ViaCEP estiver indisponível. Testes usam HTTP simulado, sem depender da internet.

CORS permite por padrão `http://localhost:3000` e `http://localhost:5173`, incluindo preflight e header Authorization. Configure origens separadas por vírgula em `CORS_ORIGINS`. Nenhum curinga é utilizado.

## Configuração

| Variável | Finalidade |
|---|---|
| `PORT` | Porta HTTP, padrão 8080 |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Banco; padrão H2 em arquivo |
| `SPRING_PROFILES_ACTIVE=postgres` | Habilita PostgreSQL; informe os dados de conexão |
| `JWT_SECRET` | Chave JWT com pelo menos 32 bytes |
| `ADMIN_USERNAME`, `ADMIN_PASSWORD` | Administrador inicial |
| `CORS_ORIGINS` | Origens frontend permitidas |
| `VIACEP_URL` | Base HTTP do ViaCEP |

Perfil `h2` mantém a alternativa original em memória, para demonstrações descartáveis. O perfil padrão usa arquivo. Datas dos exemplos de teste são controladas por Clock; na utilização real informe datas futuras.

## Validação e entrega

`./mvnw clean verify` executa testes JUnit/MockMvc de todas as entidades, JWT, regras de agenda, ViaCEP, Swagger e CORS, e gera relatório JaCoCo em `target/site/jacoco/index.html`. CI disponível em `.github/workflows/ci.yml`.

Consulte `docs/VALIDACAO.md` para os resultados desta versão. As regras anteriores estão documentadas em `docs/BASE_CP4.md`.

Publique **o conteúdo desta pasta**, inclusive `src/test`, `.mvn` e `.github`, em repositório público do GitHub. O `.gitignore` exclui banco, dependências e JAR de distribuição. Confira se o README e código aparecem em janela anônima e entregue o link no Teams até **12/10/2026**, conforme o enunciado recebido. Publicação no GitHub e submissão no Teams são etapas separadas da geração do ZIP. A nota depende da avaliação do professor.
