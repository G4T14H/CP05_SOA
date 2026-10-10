# Validação da versão CP5

Data: 07/10/2026. Java 17.0.20, Spring Boot 3.5.6, Maven 3.9.9, H2. Testes automatizados: **55 executados, 0 falhas, 0 erros, 0 ignorados**.

| Requisito | Evidência |
|---|---|
| Alunos e instrutores | CRUD, dados obrigatórios, duplicidade, paginação, ordenação, imutabilidade e exclusão lógica |
| Usuários | CRUD ADMIN, BCrypt, autenticação USER, troca de senha, token de inativo bloqueado |
| Instruções | Cadastro, consulta, reagendamento, cancelamento lógico, motivos e preservação de histórico |
| Funcionamento | Segunda a sábado, início 06h/20h, proibição domingo, limites exatos de 30min/24h |
| Agenda | Duas aulas por dia, aluno/instrutor ativos, sobreposição de uma hora e horários consecutivos |
| Sorteio | Somente instrutores disponíveis; rejeita ausência de candidato |
| Concorrência | Duas solicitações simultâneas: apenas uma reserva gravada |
| API externa | RestClient por HTTP; sucesso, hífen, CEP inválido/inexistente, falha, JSON inválido e resposta vazia |
| Swagger | Documento OpenAPI e interface públicos, esquema bearerAuth e rota ViaCEP |
| CORS | Preflight autorizado para origem configurada; origem desconhecida recusada |

JaCoCo: 331/350 linhas cobertas (**94.6%**). Cobertura mede execução de código, não garante nota. Relatórios completos acompanham o ZIP de distribuição; para gerar neste repositório, rode `./mvnw clean verify`.

A integração ViaCEP foi verificada com respostas HTTP simuladas, sem depender do serviço público. O teste do executável, incluindo persistência após reinício, está registrado em `SMOKE.txt`. PostgreSQL e Windows não foram executados neste ambiente; foram fornecidos perfil e script próprios. O executável foi validado em Linux com Java 17.

Esta versão cobre o enunciado CP5 recebido e preserva as regras do CP4 fornecido no GitHub. GET por ID, reagendamento e DELETE lógico de instrução são complementos documentados. Publicação em GitHub público e submissão do link no Teams precisam ser concluídas pelo grupo. Não é possível garantir 100% na avaliação.

Consulta direta ao serviço público ViaCEP em 07/10/2026: HTTP 200 para 01001000, UF SP, logradouro Praça da Sé. Essa verificação usou cliente HTTP separado; os testes do cliente Java e do endpoint usam servidor HTTP simulado.
