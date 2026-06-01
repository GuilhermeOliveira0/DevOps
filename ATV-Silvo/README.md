# FatecADS - Sistema Academico

Sistema academico feito com Spring Boot, Thymeleaf, Spring Security e JPA.
Ele permite gerenciar usuarios, alunos, cursos, professores e disciplinas, alem de ter login e recuperacao de senha por e-mail.

## Guia Rapido

Entre na pasta do projeto:

```powershell
cd C:\Users\Guilherme\Documents\Projetos-FATEC\DevOps\ATV-Silvo\demo
```

Para ligar o sistema localmente:

```powershell
.\run-local.ps1
```

Depois acesse:

```text
http://localhost:8082
```

Para parar o sistema em outro PowerShell:

```powershell
cd C:\Users\Guilherme\Documents\Projetos-FATEC\DevOps\ATV-Silvo\demo
.\stop-local.ps1
```

Tambem da para parar no proprio terminal do sistema com `Ctrl+C`. Se aparecer:

```text
Deseja finalizar o arquivo em lotes (S/N)?
```

digite `S` e aperte Enter.

## Importante Sobre O Terminal

Quando voce roda:

```powershell
.\run-local.ps1
```

o terminal fica aberto e parece que esta rodando "infinito". Isso e normal.
Enquanto o terminal estiver assim, o Spring Boot esta ligado e o site funciona no navegador.

Se o terminal voltar para:

```powershell
PS C:\Users\Guilherme\Documents\Projetos-FATEC\DevOps\ATV-Silvo\demo>
```

significa que o sistema parou ou falhou ao iniciar.

## O Que O Sistema Faz

O sistema possui estes modulos:

- Home
- Usuarios
- Alunos
- Cursos
- Professores
- Disciplinas
- Esqueci minha senha

Fluxo basico:

1. Abrir `http://localhost:8082`.
2. Criar um usuario em `/usuarios/criar`.
3. Fazer login com e-mail e senha.
4. Acessar a home em `/home`.
5. Cadastrar alunos, cursos, professores e disciplinas.
6. Usar "Esqueci minha senha" se precisar redefinir a senha.

## Tecnologias Usadas

- Java 17
- Spring Boot 4
- Spring Security
- Thymeleaf
- Spring Data JPA
- Hibernate
- Maven Wrapper
- PostgreSQL
- H2 para testes/perfil local
- Spring Mail para envio de e-mail

## Banco De Dados

Por padrao o projeto usa o perfil `postgres`.

As principais propriedades ficam em:

```text
demo/src/main/resources/application.properties
demo/src/main/resources/application-postgres.properties
demo/src/main/resources/application-h2.properties
```

O projeto usa:

```properties
spring.jpa.hibernate.ddl-auto=update
server.port=8082
```

Isso facilita o uso academico porque o Hibernate atualiza as tabelas automaticamente.

## Scripts Locais

Existem dois scripts locais para facilitar:

```text
demo/run-local.ps1
demo/stop-local.ps1
```

`run-local.ps1`:

- configura o SMTP localmente;
- para qualquer processo antigo na porta `8082`;
- inicia o Spring Boot;
- deixa o terminal ocupado enquanto o sistema estiver ligado.

`stop-local.ps1`:

- procura processo rodando na porta `8082`;
- encerra esse processo;
- confirma que a porta ficou livre.

Esses scripts ficam no `.gitignore`, porque podem conter configuracoes locais e dados sensiveis.

## Login E Permissoes

O login usa e-mail como usuario.

Regras principais:

- Novo usuario cadastrado pelo sistema recebe `ROLE_USER`.
- Usuario logado consegue acessar home e listagens.
- `/usuarios/listar` funciona para usuario logado.
- Editar e excluir usuarios fica reservado para `ROLE_ADMIN`.

Para transformar um usuario em admin, altere no banco o campo de role para:

```text
ROLE_ADMIN
```

## Validador Dos Campos

Os cadastros possuem validacoes para evitar dados errados e erro 500.

Exemplos:

- Nome aceita letras, espacos e acentos.
- CPF deve ter 11 numeros.
- Telefone deve ter 10 ou 11 numeros.
- E-mail precisa ter formato valido.
- Carga horaria deve ser maior que zero.
- RA, RM e sigla aceitam letras e numeros.
- Campos respeitam os limites do banco.

Se algum campo estiver errado, o sistema volta para o formulario e mostra a mensagem de erro.

## Recuperacao De Senha

O fluxo "Esqueci minha senha" usa e-mail real.

Resumo:

1. Usuario clica em "Esqueci minha senha".
2. Informa o e-mail cadastrado.
3. Sistema envia link de redefinicao.
4. Usuario abre o link.
5. Digita nova senha e confirmacao.
6. Sistema salva a nova senha com BCrypt.
7. Link usado deixa de funcionar.

Leia tambem:

```text
README-ESQUECI-SENHA.md
```

## Como Rodar Testes

Na pasta `demo`, rode:

```powershell
.\mvnw.cmd test
```

Esse comando compila o projeto e executa os testes automatizados.

## Teste Manual Recomendado

Depois de ligar com `.\run-local.ps1`, teste:

1. Abrir `http://localhost:8082`.
2. Criar usuario em `/usuarios/criar`.
3. Fazer login.
4. Abrir `/home`.
5. Abrir `/alunos/listar`.
6. Abrir `/cursos/listar`.
7. Abrir `/professores/listar`.
8. Abrir `/disciplinas/listar`.
9. Abrir `/usuarios/listar`.
10. Criar registros de teste em cada modulo.
11. Tentar salvar campo invalido e confirmar que aparece mensagem.
12. Testar "Esqueci minha senha".

## Problemas Comuns

### Porta 8082 ja esta em uso

Erro comum:

```text
Web server failed to start. Port 8082 was already in use.
```

Solucao:

```powershell
cd C:\Users\Guilherme\Documents\Projetos-FATEC\DevOps\ATV-Silvo\demo
.\stop-local.ps1
.\run-local.ps1
```

### O terminal fica rodando e nao volta

Isso e normal. O sistema esta ligado.

Para parar:

```powershell
.\stop-local.ps1
```

### E-mail de recuperacao nao chegou

Confira:

- sistema foi iniciado com `.\run-local.ps1`;
- e-mail digitado existe no cadastro;
- Gmail pode mandar para spam;
- senha de app do Gmail deve estar correta;
- porta `587`, auth e starttls precisam estar ativos.

### Erro ao executar script PowerShell

Se aparecer erro de permissao de script, rode no mesmo PowerShell:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

Depois rode novamente:

```powershell
.\run-local.ps1
```

## Estrutura Principal

```text
demo/src/main/java/br/com/examplefatec/controller
demo/src/main/java/br/com/examplefatec/service
demo/src/main/java/br/com/examplefatec/repository
demo/src/main/java/br/com/examplefatec/entity
demo/src/main/java/br/com/examplefatec/Security
demo/src/main/java/br/com/examplefatec/dto
demo/src/main/resources/templates
demo/src/main/resources/static/css
demo/src/test
```

## Cuidados

- Nao colocar senha real no Git.
- Nao colocar senha de app do Gmail no README.
- Nao commitar `run-local.ps1` se ele tiver dados pessoais.
- Senhas de usuario sao salvas com BCrypt.
- Token de recuperacao e salvo no banco apenas como hash.
