# Recuperacao de Senha - FatecADS

Este documento explica apenas a funcionalidade "Esqueci minha senha" do sistema FatecADS.

## Objetivo

Permitir que um usuario cadastrado solicite um link de redefinicao de senha por e-mail, crie uma nova senha e volte a acessar o sistema sem que a senha antiga seja enviada ou revelada.

## Fluxo completo

1. O usuario clica em "Esqueci minha senha" na tela de login.
2. O sistema abre `GET /forgot-password`.
3. O usuario informa o e-mail usado no login.
4. O sistema recebe o formulario em `POST /forgot-password`.
5. O sistema normaliza o e-mail e procura o usuario.
6. Se o e-mail existir, o sistema invalida tokens antigos ainda nao usados.
7. O sistema gera um token aleatorio seguro.
8. O banco salva somente o hash SHA-256 do token, nunca o token bruto.
9. O sistema envia um e-mail com o link `/reset-password?token=...`.
10. O usuario abre o link recebido.
11. O sistema valida se o token existe, nao expirou e nao foi usado.
12. O usuario informa nova senha e confirmacao.
13. O sistema salva a nova senha criptografada com BCrypt.
14. O sistema marca o token como usado.
15. O sistema envia e-mail de confirmacao de alteracao.
16. O usuario e redirecionado para `/login?resetSuccess`.

## Classes participantes

- `PasswordResetController`: controla as rotas e views de recuperacao.
- `PasswordResetService`: contem a regra de negocio do fluxo.
- `EmailService`: envia e-mails de redefinicao e confirmacao.
- `PasswordResetToken`: entity JPA do token salvo no banco.
- `PasswordResetTokenRepository`: consultas e persistencia dos tokens.
- `UsuarioService`: atualiza a senha com BCrypt.
- `UsuarioRepository`: busca usuario por e-mail.
- `ForgotPasswordForm`: formulario com e-mail.
- `ResetPasswordForm`: formulario com token, senha e confirmacao.

## Templates usados

- `templates/forgot-password.html`
- `templates/reset-password.html`
- `templates/login.html`, que possui o link "Esqueci minha senha" e mensagem de sucesso.

## Rotas do fluxo

- `GET /forgot-password`: abre formulario de e-mail.
- `POST /forgot-password`: processa solicitacao e envia mensagem generica.
- `GET /reset-password?token=...`: valida token e abre formulario de nova senha.
- `POST /reset-password`: valida senha, troca senha e invalida token.

## Como o token funciona

O token bruto e gerado com `SecureRandom`, usando 32 bytes aleatorios codificados em Base64 URL-safe. Esse valor aparece somente no link enviado por e-mail.

No banco, o sistema salva apenas:

- hash SHA-256 do token;
- usuario vinculado;
- data de criacao;
- data de expiracao;
- data de uso, quando o token ja foi usado;
- IP da solicitacao, se disponivel.

Com isso, mesmo que alguem veja o banco, nao encontra o token bruto usado no link.

## Expiracao e uso unico

O tempo de expiracao vem da propriedade:

```properties
app.password-reset.expiration-minutes=${PASSWORD_RESET_EXPIRATION_MINUTES:30}
```

Um token so e valido quando:

- existe no banco pelo hash;
- `usedAt` esta vazio;
- `expiresAt` ainda nao passou.

Quando a senha e alterada, `usedAt` e preenchido e o mesmo link deixa de funcionar.

## Criptografia da senha

A nova senha e enviada pelo formulario e entregue ao `UsuarioService`, que usa `PasswordEncoder` com BCrypt. A senha em texto puro nao deve ser salva, exibida em tela, enviada por e-mail ou registrada em log.

## Envio de e-mail

O `EmailService` usa `JavaMailSender`. As configuracoes ficam em `application.properties`, sempre por variaveis de ambiente:

```properties
spring.mail.host=${SMTP_HOST:smtp.gmail.com}
spring.mail.port=${SMTP_PORT:587}
spring.mail.username=${SMTP_USERNAME:}
spring.mail.password=${SMTP_PASSWORD:}
spring.mail.properties.mail.smtp.auth=${SMTP_AUTH:true}
spring.mail.properties.mail.smtp.starttls.enable=${SMTP_STARTTLS:true}
```

Para Gmail SMTP:

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

Nunca coloque a senha de app real no codigo, nos READMEs ou no Git.

## Como testar manualmente

1. Rodar o sistema.
2. Criar um usuario com e-mail valido.
3. Configurar SMTP real por variaveis de ambiente.
4. Abrir `/login`.
5. Clicar em "Esqueci minha senha".
6. Informar o e-mail cadastrado.
7. Verificar a chegada do e-mail.
8. Abrir o link recebido.
9. Informar senha e confirmacao iguais.
10. Fazer login com a nova senha.
11. Confirmar que a senha antiga nao funciona.
12. Abrir novamente o mesmo link e confirmar que ele e recusado.

## Cuidados de seguranca

- Nao revelar se o e-mail existe.
- Nao salvar senha em texto puro.
- Nao enviar senha por e-mail.
- Nao expor token bruto em log.
- Nao salvar senha de app do Gmail no Git.
- Salvar no banco somente o hash do token.
- Invalidar tokens anteriores ao gerar novo link.
- Marcar token como usado depois da troca de senha.
