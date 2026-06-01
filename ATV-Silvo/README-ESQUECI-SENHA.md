# README - Esqueci Minha Senha

Este README explica como foi feita a funcionalidade de recuperacao de senha do sistema FatecADS, quais arquivos participam do fluxo e como testar.

## Objetivo Da Funcionalidade

A funcionalidade "Esqueci minha senha" permite que um usuario cadastrado redefina a senha sem precisar estar logado.

O sistema nao envia a senha antiga por e-mail. Ele gera um link temporario, valida esse link e depois salva uma nova senha criptografada com BCrypt.

## Como Usar

1. Inicie o projeto:

```powershell
cd C:\Users\Guilherme\Documents\Projetos-FATEC\DevOps\ATV-Silvo\demo
.\run-local.ps1
```

2. Acesse o login:

```text
http://localhost:8082/login
```

3. Clique em:

```text
Esqueci minha senha
```

4. Informe o e-mail de um usuario cadastrado.
5. Abra o e-mail recebido.
6. Clique no link de redefinicao.
7. Digite a nova senha e confirme.
8. Volte para o login e entre com a nova senha.

## Rotas Criadas

```text
GET  /forgot-password
POST /forgot-password
GET  /reset-password?token=...
POST /reset-password
```

Essas rotas ficam publicas no Spring Security porque o usuario que esqueceu a senha ainda nao esta autenticado.

## Arquivos Criados Ou Usados No Fluxo

### Controller

```text
demo/src/main/java/br/com/examplefatec/controller/PasswordResetController.java
```

Responsavel pelas rotas do fluxo.

Ele abre a tela de pedido de recuperacao, recebe o e-mail informado, abre a tela de nova senha quando o token e valido e envia a nova senha para o service.

### DTOs Dos Formularios

```text
demo/src/main/java/br/com/examplefatec/dto/ForgotPasswordForm.java
demo/src/main/java/br/com/examplefatec/dto/ResetPasswordForm.java
```

`ForgotPasswordForm` recebe e valida o e-mail.

`ResetPasswordForm` recebe o token, a nova senha e a confirmacao da senha.

### Entity Do Token

```text
demo/src/main/java/br/com/examplefatec/entity/PasswordResetToken.java
```

Representa o token salvo no banco.

Campos principais:

- usuario vinculado;
- hash do token;
- data de criacao;
- data de expiracao;
- data de uso;
- IP de quem solicitou, quando disponivel.

O token real nao e salvo no banco. O banco salva apenas o hash SHA-256.

### Repository

```text
demo/src/main/java/br/com/examplefatec/repository/PasswordResetTokenRepository.java
```

Faz as consultas dos tokens de recuperacao.

Ele busca token pelo hash, lista tokens ativos de um usuario e remove tokens quando necessario.

### Service Principal

```text
demo/src/main/java/br/com/examplefatec/service/PasswordResetService.java
```

Contem a regra de negocio da recuperacao de senha.

Ele:

- normaliza o e-mail;
- procura o usuario pelo e-mail;
- invalida tokens antigos do usuario;
- gera um token aleatorio com `SecureRandom`;
- salva apenas o hash SHA-256 do token;
- monta o link de redefinicao;
- valida se o token existe, nao expirou e nao foi usado;
- valida tamanho da nova senha;
- confere se senha e confirmacao sao iguais;
- chama o `UsuarioService` para salvar a senha com BCrypt;
- marca o token como usado;
- envia e-mail de confirmacao.

### Resultado Do Fluxo

```text
demo/src/main/java/br/com/examplefatec/service/PasswordResetResult.java
```

Enum usado para informar ao controller se a redefinicao deu certo ou se falhou por token invalido, senha invalida ou senhas diferentes.

### Envio De E-mail

```text
demo/src/main/java/br/com/examplefatec/service/EmailService.java
```

Envia dois tipos de e-mail:

- e-mail com o link de redefinicao;
- e-mail avisando que a senha foi alterada.

Se o SMTP falhar, o erro e registrado sem expor senha ou token.

### UsuarioService

```text
demo/src/main/java/br/com/examplefatec/service/UsuarioService.java
```

Foi usado para salvar a nova senha.

O metodo `updatePassword` recebe a senha em texto puro apenas no momento da troca, criptografa com BCrypt e salva no usuario.

### Configuracao De Seguranca

```text
demo/src/main/java/br/com/examplefatec/Security/SecurityConfig.java
```

Libera as rotas:

```text
/forgot-password
/reset-password
```

Tambem define o `BCryptPasswordEncoder`, usado para criptografar as senhas.

### Templates HTML

```text
demo/src/main/resources/templates/forgot-password.html
demo/src/main/resources/templates/reset-password.html
demo/src/main/resources/templates/login.html
```

`forgot-password.html` mostra o formulario para informar o e-mail.

`reset-password.html` mostra o formulario de nova senha quando o token e valido, ou uma mensagem de erro quando o link esta invalido, expirado ou ja foi usado.

`login.html` recebeu o link "Esqueci minha senha" e tambem mostra a mensagem de sucesso depois da troca de senha.

### Configuracoes

```text
demo/src/main/resources/application.properties
```

Configuracoes importantes:

```properties
app.base-url=${APP_BASE_URL:http://localhost:8082}
app.password-reset.expiration-minutes=${PASSWORD_RESET_EXPIRATION_MINUTES:30}
app.mail.from=${APP_MAIL_FROM:${SMTP_USERNAME:no-reply@fatecads.local}}

spring.mail.host=${SMTP_HOST:smtp.gmail.com}
spring.mail.port=${SMTP_PORT:587}
spring.mail.username=${SMTP_USERNAME:}
spring.mail.password=${SMTP_PASSWORD:}
spring.mail.properties.mail.smtp.auth=${SMTP_AUTH:true}
spring.mail.properties.mail.smtp.starttls.enable=${SMTP_STARTTLS:true}
```

O tempo padrao de expiracao do link e 30 minutos.

### Dependencias

```text
demo/pom.xml
```

Dependencias usadas no fluxo:

- `spring-boot-starter-mail`;
- `spring-boot-starter-validation`;
- `spring-boot-starter-security`;
- `spring-boot-starter-thymeleaf`;
- `spring-boot-starter-data-jpa`.

### Testes Automatizados

```text
demo/src/test/java/br/com/examplefatec/service/PasswordResetServiceTest.java
demo/src/test/java/br/com/examplefatec/controller/PasswordResetControllerTest.java
```

Os testes cobrem:

- abertura da tela de esqueci senha;
- envio do pedido de recuperacao;
- mensagem generica para nao revelar se o e-mail existe;
- token valido;
- token inexistente;
- token expirado;
- token ja usado;
- senha curta;
- senha diferente da confirmacao;
- troca de senha com BCrypt;
- marcacao do token como usado;
- redirecionamento para `/login?resetSuccess`.

## Como O Fluxo Funciona

### 1. Usuario Solicita Recuperacao

O usuario acessa `/forgot-password`, informa o e-mail e envia o formulario.

O controller chama:

```text
PasswordResetService.requestPasswordReset(email, ip)
```

O sistema sempre mostra a mesma mensagem:

```text
Se o email estiver cadastrado, enviaremos instrucoes para redefinir sua senha.
```

Isso evita revelar se um e-mail esta cadastrado ou nao.

### 2. Sistema Gera O Token

Se o e-mail existir, o service gera um token aleatorio usando `SecureRandom`.

O token real e colocado apenas no link enviado por e-mail:

```text
http://localhost:8082/reset-password?token=...
```

No banco, o sistema salva somente o hash SHA-256 desse token.

### 3. Tokens Antigos Sao Invalidados

Antes de criar um novo token, o sistema marca tokens antigos do mesmo usuario como usados.

Assim, quando o usuario pede um novo link, o link anterior deixa de funcionar.

### 4. Usuario Abre O Link

Quando o usuario acessa:

```text
/reset-password?token=...
```

o sistema valida:

- se o token foi informado;
- se o hash existe no banco;
- se ainda nao expirou;
- se ainda nao foi usado.

Se estiver tudo certo, a tela de nova senha aparece.

### 5. Usuario Define Nova Senha

O usuario informa a nova senha e a confirmacao.

O sistema valida:

- senha com 8 a 72 caracteres;
- senha igual a confirmacao;
- token ainda valido.

Se passar nas validacoes, o `UsuarioService.updatePassword` criptografa a nova senha com BCrypt e salva.

### 6. Token E Finalizado

Depois da troca de senha, o token recebe `usedAt`.

Isso faz o mesmo link nao funcionar novamente.

O usuario e redirecionado para:

```text
/login?resetSuccess
```

## Regras De Seguranca

- A senha antiga nunca e enviada por e-mail.
- O token real nunca e salvo no banco.
- O banco guarda apenas hash SHA-256 do token.
- O token so pode ser usado uma vez.
- O token expira por padrao em 30 minutos.
- Um novo pedido invalida os links antigos.
- O sistema nao revela se o e-mail existe.
- A nova senha e salva com BCrypt.
- O e-mail de erro do SMTP nao exibe senha nem token.

## Como Configurar O E-mail

O envio usa SMTP por variaveis de ambiente.

Exemplo para Gmail:

```powershell
$env:SMTP_HOST="smtp.gmail.com"
$env:SMTP_PORT="587"
$env:SMTP_USERNAME="seu-email@gmail.com"
$env:SMTP_PASSWORD="senha-de-app-do-gmail"
$env:SMTP_AUTH="true"
$env:SMTP_STARTTLS="true"
$env:APP_BASE_URL="http://localhost:8082"
.\run-local.ps1
```

Importante: para Gmail, use senha de app. Nao use a senha normal da conta Google.

Nao coloque senha real no README, no codigo ou no GitHub.

## Como Testar Manualmente

1. Inicie o sistema com `.\run-local.ps1`.
2. Crie um usuario com e-mail valido.
3. Saia do sistema.
4. Acesse `/login`.
5. Clique em "Esqueci minha senha".
6. Digite o e-mail cadastrado.
7. Abra o e-mail recebido.
8. Clique no link.
9. Digite a nova senha e confirme.
10. Entre no sistema com a nova senha.
11. Tente entrar com a senha antiga e confirme que nao funciona.
12. Tente abrir o mesmo link novamente e confirme que ele fica invalido.

## Como Rodar Os Testes

Na pasta `demo`, execute:

```powershell
.\mvnw.cmd test
```

## Problemas Comuns

### O E-mail Nao Chegou

Confira:

- se o e-mail existe no cadastro;
- se o sistema foi iniciado com as variaveis SMTP corretas;
- se a senha de app do Gmail esta correta;
- se o e-mail caiu no spam;
- se a porta SMTP esta como `587`;
- se `SMTP_AUTH` e `SMTP_STARTTLS` estao como `true`.

### O Link Esta Invalido

Possiveis causas:

- o link expirou;
- o link ja foi usado;
- o link foi copiado incompleto;
- outro pedido de recuperacao gerou um link novo e invalidou o anterior.

### A Porta 8082 Esta Em Uso

Execute:

```powershell
cd C:\Users\Guilherme\Documents\Projetos-FATEC\DevOps\ATV-Silvo\demo
.\stop-local.ps1
.\run-local.ps1
```

## Resumo Para Apresentacao

A funcionalidade "Esqueci minha senha" foi feita com Spring Boot, Thymeleaf, Spring Security, JPA e Spring Mail. O usuario informa o e-mail, recebe um link temporario, cria uma nova senha e o sistema salva essa senha com BCrypt. Por seguranca, o token real nao fica salvo no banco, apenas o hash, e cada link so pode ser usado uma vez.
