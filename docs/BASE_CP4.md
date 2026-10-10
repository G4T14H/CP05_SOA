# Auto-Escola API — Checkpoint 4 (SOA e WebServices)

API REST em Spring Boot para agendamento de instruções de uma auto-escola, com cadastro de instrutores, alunos, usuários do sistema e controle de acesso via JWT.

## Integrantes

| Nome | RM |
|---|---|
| Gabriel Dias | 556830 |
| Gabriel Galerani | 557421 |
| Gustavo Alves | 557876 |
| Leonardo Taschin | 554583 |

Disciplina: SOA e WebServices
Professor: Carlos Eduardo Machado de Oliveira

## Stack

- Java 17
- Spring Boot 3.2.5 (Web, Data JPA, Security, Validation)
- PostgreSQL (produção) / H2 (perfil de testes locais)
- Spring Security + JWT (jjwt 0.11.5)
- BCrypt para hash de senha
- Lombok
- Maven

## O que o CP4 entrega

- **Instrutores**: cadastro, listagem paginada (10/página, ordenada por nome), atualização (nome/telefone/endereço apenas — e-mail, CNH e especialidade são imutáveis) e exclusão lógica (fica `inativo`).
- **Alunos**: mesmas regras dos instrutores, adaptadas (CPF em vez de CNH/especialidade).
- **Usuários do sistema**: cadastro com senha criptografada via BCrypt; apenas ADMIN pode cadastrar, listar, atualizar e excluir usuários; qualquer usuário autenticado pode trocar a própria senha.
- **Autenticação JWT**: login em `/auth/login`, token Bearer usado em todas as demais rotas.
- **Agendamento de instrução**: valida horário de funcionamento, antecedência mínima, aluno/instrutor ativos, limite diário do aluno, conflito de agenda do instrutor e sorteio de instrutor quando não informado.
- **Cancelamento de instrução**: exige motivo (aluno desistiu / instrutor cancelou / outros) e antecedência mínima de 24h.

## Regras de negócio implementadas

### Instrutor
- Campos obrigatórios no cadastro: nome, e-mail, telefone, CNH, especialidade (MOTOS, CARROS, VANS, CAMINHOES), endereço completo (número e complemento opcionais).
- Listagem traz nome, e-mail, CNH e especialidade; paginada em 10 registros, ordenada por nome crescente.
- Atualização permite apenas nome, telefone e endereço; tentativa de mudar e-mail, CNH ou especialidade é simplesmente ignorada pelo endpoint (esses campos nem existem no DTO de atualização).
- Exclusão é lógica: o registro vira `ativo = false`, nunca é apagado.

### Aluno
- Mesmas regras do instrutor, trocando CNH/especialidade por CPF.

### Usuário
- Senha sempre armazenada com hash BCrypt.
- `POST /usuarios`, `GET /usuarios`, `PUT /usuarios/{id}`, `DELETE /usuarios/{id}` → apenas ADMIN (`@PreAuthorize("hasRole('ADMIN')")`).
- `PATCH /usuarios/me/senha` → qualquer usuário autenticado, exige a senha atual correta.
- Um usuário administrador é criado automaticamente na primeira subida da aplicação (ver seção de credenciais abaixo), já que é preciso de pelo menos um ADMIN para cadastrar os demais.

### Agendamento de instrução
- Funcionamento: segunda a sábado, 06:00 às 21:00, com instruções de 1h fixa (logo o último horário possível de início é 20:00).
- Antecedência mínima para agendar: 30 minutos a partir do momento da requisição.
- Não permite agendar com aluno inativo, nem com instrutor inativo.
- Máximo de 2 instruções agendadas por dia para o mesmo aluno.
- Não permite dois agendamentos para o mesmo instrutor na mesma data/hora.
- Instrutor é opcional: se omitido, o sistema sorteia aleatoriamente um instrutor ativo e livre naquele horário.

### Cancelamento de instrução
- Motivo obrigatório: `ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU` ou `OUTROS`.
- Só é permitido cancelar com antecedência mínima de 24 horas em relação à data/hora agendada.

## Como rodar

### Opção 1 — com PostgreSQL local
```bash
# ajuste usuário/senha/porta conforme seu banco
export DB_URL=jdbc:postgresql://localhost:5432/autoescola
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

mvn spring-boot:run
```

### Opção 2 — sem instalar nada, com H2 em memória
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

A aplicação sobe em `http://localhost:8080`. Na primeira execução um usuário administrador é criado automaticamente:

- **username**: `admin` (ou o valor de `ADMIN_USERNAME`)
- **senha**: `admin123` (ou o valor de `ADMIN_PASSWORD`)

> Troque essas credenciais via variáveis de ambiente antes de qualquer uso real; os valores padrão existem só para permitir o primeiro login em ambiente de desenvolvimento/avaliação.

## Autenticação

```
POST /auth/login
{
  "username": "admin",
  "senha": "admin123"
}
```

Resposta:
```
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer"
}
```

Use o token em todas as demais chamadas: `Authorization: Bearer <token>`.

## Principais endpoints

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/auth/login` | público | Login, retorna token JWT |
| POST | `/usuarios` | ADMIN | Cadastra usuário |
| GET | `/usuarios` | ADMIN | Lista usuários (paginado) |
| PUT | `/usuarios/{id}` | ADMIN | Atualiza role/ativo de um usuário |
| DELETE | `/usuarios/{id}` | ADMIN | Inativa um usuário |
| PATCH | `/usuarios/me/senha` | autenticado | Troca a própria senha |
| POST | `/instrutores` | autenticado | Cadastra instrutor |
| GET | `/instrutores` | autenticado | Lista instrutores (paginado, por nome) |
| GET | `/instrutores/{id}` | autenticado | Detalha um instrutor |
| PUT | `/instrutores/{id}` | autenticado | Atualiza nome/telefone/endereço |
| DELETE | `/instrutores/{id}` | autenticado | Inativa um instrutor |
| POST | `/alunos` | autenticado | Cadastra aluno |
| GET | `/alunos` | autenticado | Lista alunos (paginado, por nome) |
| GET | `/alunos/{id}` | autenticado | Detalha um aluno |
| PUT | `/alunos/{id}` | autenticado | Atualiza nome/telefone/endereço |
| DELETE | `/alunos/{id}` | autenticado | Inativa um aluno |
| POST | `/instrucoes` | autenticado | Agenda uma instrução |
| GET | `/instrucoes` | autenticado | Lista instruções (paginado, por data/hora) |
| PATCH | `/instrucoes/{id}/cancelar` | autenticado | Cancela uma instrução |

## Estrutura do projeto

```
src/main/java/com/fiap/autoescola/
├── config/          # SecurityConfig, DataSeeder (cria o admin inicial)
├── security/        # JwtService, filtro JWT, UserDetailsService
├── domain/
│   ├── entity/      # Usuario, Instrutor, Aluno, Instrucao
│   ├── embeddable/  # Endereco
│   └── enums/       # Especialidade, RoleUsuario, MotivoCancelamento, StatusInstrucao
├── repository/      # Interfaces Spring Data JPA
├── dto/             # Records de request/response, com Bean Validation
├── service/         # Regras de negócio
├── controller/      # Endpoints REST
└── exception/       # Exceções customizadas + handler global (@RestControllerAdvice)
```

## Testando rapidamente com curl

```bash
# login
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","senha":"admin123"}' | jq -r .token)

# cadastrar um instrutor
curl -X POST http://localhost:8080/instrutores \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Carlos Souza",
    "email": "carlos@autoescola.com",
    "telefone": "11999990000",
    "cnh": "12345678900",
    "especialidade": "CARROS",
    "endereco": {
      "logradouro": "Rua A", "numero": "100", "complemento": "",
      "bairro": "Centro", "cidade": "São Paulo", "uf": "SP", "cep": "01000-000"
    }
  }'
```
