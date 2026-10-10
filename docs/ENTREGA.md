# Checklist da entrega

- [x] Java 17 e Spring Boot.
- [x] CRUD das quatro entidades com regras herdadas do CP4.
- [x] Autenticação JWT e autorização ADMIN.
- [x] Consumo HTTP de API externa (ViaCEP).
- [x] Swagger automático e autenticação no Swagger.
- [x] CORS configurável e testado.
- [x] Testes para cada entidade e regras de negócio.
- [x] README com quatro integrantes e RMs.
- [x] Maven Wrapper e integração contínua.
- [ ] Publicar esta versão em repositório **público** no GitHub.
- [ ] Abrir o repositório em janela anônima e conferir código/README/testes.
- [ ] Entregar o link no Teams até 12/10/2026.

Para substituir o código de um repositório existente, copie a nova pasta preservando o `.git` do clone local. Não envie o banco `dados/` nem a pasta `target/`. Rode `mvnw.cmd clean verify` (Windows) ou `./mvnw clean verify` (Linux/macOS), faça commit e push. Confira a aba Actions após a publicação. O `.gitignore` já exclui o JAR, que acompanha o ZIP apenas para facilitar a execução.

Se usar o upload de arquivos pelo site do GitHub, confira também as pastas `.mvn` e `.github`: elas fazem parte do projeto. Um cliente Git preserva essas pastas mais facilmente.

Esta geração de ZIP não publica nem submete o trabalho automaticamente. Nota final depende do professor.
