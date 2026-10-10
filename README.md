# Autoescola — Checkpoint 5

API REST para cadastro de alunos, instrutores e usuários, com agendamento de aulas. Desenvolvida em Java 17 e Spring Boot, a partir do [projeto CP4](https://github.com/GabrielGaleraniAlmeida/autoescola-api).

## Integrantes

| Nome | RM |
|---|---|
| Gustavo Alves | 557876 |
| Gabriel Dias | 556830 |
| Gabriel Galerani | 557421 |
| Leonardo Taschin | 554583 |

## Executar

É necessário Java 17 ou superior e internet na primeira compilação.

No Windows:

```bat
mvnw.cmd clean verify
mvnw.cmd spring-boot:run
```

No Linux/macOS:

```sh
./mvnw clean verify
./mvnw spring-boot:run
```

Também é possível usar `iniciar.bat` ou `iniciar.sh`.

Swagger: http://localhost:8080/swagger-ui/index.html

O banco padrão é H2, salvo na pasta `dados/`. Há um perfil opcional para PostgreSQL.

## Autenticação

Faça uma requisição para `POST /auth/login`:

```json
{"username":"admin","senha":"admin123"}
```

Copie o token retornado e cole em **Authorize** no Swagger. Nas outras ferramentas, envie o header `Authorization: Bearer TOKEN`. As credenciais padrão são para execução local.

## Funcionalidades

- Cadastro, consulta, atualização e exclusão lógica de alunos, instrutores e usuários.
- Gerenciamento de usuários restrito ao ADMIN e troca da própria senha.
- Agendamento, consulta, reagendamento e cancelamento de instruções.
- Consulta de endereço pelo ViaCEP em `GET /enderecos/{cep}`.
- Documentação Swagger e CORS configurável.

## Regras principais

- Alunos e instrutores precisam de nome, email, telefone e endereço. Aluno possui CPF; instrutor possui CNH e especialidade.
- Especialidades: `MOTOS`, `CARROS`, `VANS` e `CAMINHOES`.
- Apenas nome, telefone e endereço podem ser alterados nesses cadastros.
- Exclusões tornam os cadastros inativos e preservam o histórico.
- Listagens paginadas, com 10 registros por página por padrão.
- Aulas de uma hora, de segunda a sábado, com início entre 06h e 20h, no horário de São Paulo.
- Agendamento com pelo menos 30 minutos de antecedência e limite de duas aulas por aluno por dia.
- Aluno e instrutor devem estar ativos, sem aulas sobrepostas. Quando o instrutor é omitido, o sistema sorteia um disponível.
- Cancelamento e reagendamento exigem pelo menos 24 horas de antecedência no horário original.
- Motivos de cancelamento: `ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU` ou `OUTROS`. DELETE de instrução realiza cancelamento com motivo `OUTROS`.

## Configuração e testes

As variáveis `PORT`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `ADMIN_USERNAME`, `ADMIN_PASSWORD`, `CORS_ORIGINS` e `VIACEP_URL` permitem ajustar a execução.

CORS permite inicialmente `http://localhost:3000` e `http://localhost:5173`. Para PostgreSQL, use `SPRING_PROFILES_ACTIVE=postgres` e informe a conexão.

O comando `clean verify` executa os testes e gera o relatório em `target/site/jacoco/index.html`. O GitHub Actions executa a verificação a cada push.

Exemplos de requisições estão em `exemplos/requisicoes.http`. Detalhes da validação e das regras do CP4 estão em `docs/`.
