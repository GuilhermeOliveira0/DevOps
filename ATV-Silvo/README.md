# FatecADS - Sistema Academico

Sistema academico simples desenvolvido com Spring Boot e Thymeleaf para gerenciar usuarios, alunos, cursos, professores e disciplinas. O projeto tambem possui login com Spring Security e fluxo de recuperacao de senha por e-mail.

## Tecnologias usadas

- Java 17
- Spring Boot 4
- Spring Security
- Thymeleaf
- Spring Data JPA / Hibernate
- Maven Wrapper
- PostgreSQL no perfil padrao `postgres`
- H2 no perfil `h2` para rodar localmente sem configurar PostgreSQL
- Envio de e-mail com Spring Mail e SMTP configurado por variaveis de ambiente

## Como o sistema funciona

O usuario acessa a aplicacao pelo navegador, faz login e entra na home interna. A home mostra os modulos principais do sistema:

- Alunos
- Cursos
- Professores
- Disciplinas
- Usuarios

Cada modulo possui telas Thymeleaf para listar, criar, editar e excluir registros. A seguranca fica centralizada no Spring Security:

- Rotas publicas: pagina inicial, login, cadastro de usuario, recuperacao e redefinicao de senha.
- Rotas autenticadas: home, alunos, cursos, professores, disciplinas e listagem de usuarios.
- Rotas de ADMIN: edicao e exclusao de usuarios.

## Funcionalidades principais

- Login com e-mail e senha.
- Cadastro publico de usuarios com role padrao `ROLE_USER`.
- Home com navegacao para os modulos.
- Cadastro, listagem, edicao e exclusao de alunos.
- Cadastro, listagem, edicao e exclusao de cursos.
- Cadastro, listagem, edicao e exclusao de professores.
- Cadastro, listagem, edicao e exclusao de disciplinas.
- Listagem de usuarios para usuario logado.
- Edicao e exclusao de usuarios somente para ADMIN.
- Recuperacao de senha com token seguro e envio de e-mail.

## Como rodar localmente

Entre na pasta do projeto Spring:

```powershell
cd C:\Users\Guilherme\Documents\Projetos-FATEC\DevOps\ATV-Silvo\demo
```

Para rodar com H2 local, sem PostgreSQL:

```powershell
$env:SPRING_PROFILES_ACTIVE="h2"
.\mvnw.cmd spring-boot:run
```

Depois acesse:

```text
http://localhost:8082
```

Para rodar com PostgreSQL, mantenha o perfil padrao ou configure:

```powershell
$env:SPRING_PROFILES_ACTIVE="postgres"
$env:DB_URL="jdbc:postgresql://localhost:5432/fatecads_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="sua_senha_do_banco"
.\mvnw.cmd spring-boot:run
```

## Configuracao de e-mail real

O envio de recuperacao de senha usa SMTP. Para Gmail, gere uma senha de app na conta Google e configure por variaveis de ambiente. Nao salve a senha real no Git.

```powershell
$env:SMTP_HOST="smtp.gmail.com"
$env:SMTP_PORT="587"
$env:SMTP_USERNAME="seu-email@gmail.com"
$env:SMTP_PASSWORD="sua-senha-de-app"
$env:SMTP_AUTH="true"
$env:SMTP_STARTTLS="true"
$env:APP_MAIL_FROM="seu-email@gmail.com"
$env:APP_BASE_URL="http://localhost:8082"
.\mvnw.cmd spring-boot:run
```

## Estrutura de pastas

- `src/main/java/br/com/examplefatec/controller`: controllers MVC que recebem requisicoes e retornam views.
- `src/main/java/br/com/examplefatec/service`: regras de negocio e orquestracao entre controller e repository.
- `src/main/java/br/com/examplefatec/repository`: interfaces JPA para acesso ao banco.
- `src/main/java/br/com/examplefatec/entity`: entities JPA que representam tabelas e relacionamentos.
- `src/main/java/br/com/examplefatec/Security`: configuracao de seguranca, login e adaptadores do Spring Security.
- `src/main/java/br/com/examplefatec/dto`: objetos de formulario usados nas telas.
- `src/main/resources/templates`: paginas Thymeleaf.
- `src/main/resources/static/css`: CSS da interface.
- `src/test`: testes automatizados.

## Como testar

Rode os testes automatizados:

```powershell
cd C:\Users\Guilherme\Documents\Projetos-FATEC\DevOps\ATV-Silvo\demo
.\mvnw.cmd test
```

Verificacao manual recomendada:

1. Abrir `http://localhost:8082`.
2. Criar um usuario em `/usuarios/criar`.
3. Fazer login com o usuario criado.
4. Abrir `/home`.
5. Abrir listagens de alunos, cursos, professores, disciplinas e usuarios.
6. Criar, editar e excluir registros de teste.
7. Testar o fluxo "Esqueci minha senha" com SMTP configurado.

## Observacoes importantes

- A senha de usuario e salva com BCrypt.
- A role padrao de novo usuario e `ROLE_USER`.
- Para criar um administrador, altere a role do usuario para `ROLE_ADMIN` no banco de dados.
- O arquivo `target/` e gerado pelo Maven e nao faz parte da entrega final.
- Senhas reais de banco, Gmail ou SMTP devem ficar apenas em variaveis de ambiente.
- O projeto mantem `ddl-auto=update` para simplicidade academica; em producao, o ideal seria usar migrations versionadas.
