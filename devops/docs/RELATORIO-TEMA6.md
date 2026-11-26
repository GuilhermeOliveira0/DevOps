# Relatório — Tema 6: Teste de validação do cadastro de produtos

**Disciplina:** Integração e Entrega Contínua (DevOps) · Fatec
**Integrantes:** [Integrante 1] · [Integrante 2] · [Integrante 3]

## 1. Conceito pesquisado

Teste de validação é o teste que confere se o sistema **recusa dados inválidos antes de gravá-los** e se
informa o usuário com uma mensagem clara. É uma característica de qualidade ligada à **integridade dos dados**:
um produto sem nome ou com preço negativo no banco gera erro em todo o resto do sistema (carrinho, pedidos,
relatórios).

Validar só no HTML (`required`, `min`) não basta: essas regras rodam no navegador e podem ser ignoradas com
DevTools, curl ou Postman. A validação precisa acontecer **no servidor**, e o teste automatizado garante que
ela continue funcionando depois de qualquer mudança no código.

## 2. Ferramentas utilizadas

| Ferramenta | Uso no projeto |
|---|---|
| **JUnit 5** | Estrutura dos testes (`@Test`, `@ParameterizedTest`, asserções) |
| **Jakarta Bean Validation** (`spring-boot-starter-validation`) | Regras declaradas na entidade: `@NotBlank`, `@NotNull`, `@Positive`, `@Size` |
| **Spring Boot Test + MockMvc** (`@WebMvcTest`) | Simula o envio do formulário `/produtos/salvar` sem abrir navegador |
| **Mockito** (`@MockitoBean`) | Substitui o `ProdutoService` para provar que nada é salvo quando o dado é inválido |

## 3. Procedimentos realizados

**Diagnóstico:** antes da mudança o `ProdutoController` salvava qualquer coisa que chegasse. Nome vazio e preço
negativo eram gravados; campos nulos geravam erro 500 do PostgreSQL.

**Implementação:**

1. `pom.xml` — adicionada a dependência `spring-boot-starter-validation`.
2. `entity/Produto.java` — regras nos campos:
   - `descricaoProduto`: `@NotBlank`, `@Size(max = 40)`
   - `valorProduto`: `@NotNull`, `@Positive` (recusa zero e negativo)
   - `unidadeProduto`: `@NotBlank`, `@Size(max = 10)`
   - `marcaProduto`: `@NotBlank`, `@Size(max = 30)`
3. `controller/ProdutoController.java` — `salvar` recebe `@Valid Produto` + `BindingResult`. Com erro, volta para o
   formulário e **não chama o service**.
4. `templates/produto/formularioProduto.html` — mensagem de erro embaixo de cada campo (`th:errors`).
5. `messages.properties` — mensagem amigável quando o valor não é número ("abc").
6. `service/ProdutoService.java` e `static/js/edicao-inline.js` — a edição de preço direto na listagem passou a
   seguir a mesma regra (maior que zero).

**Testes criados** (`src/test/java/br/com/fatecads/fatecads/`):

- `entity/ProdutoValidacaoTests.java` — **17 testes** unitários com o `Validator` do Jakarta, sem subir o Spring.
- `controller/ProdutoCadastroValidacaoTests.java` — **8 testes** com MockMvc que enviam o formulário e conferem
  status, tela devolvida, mensagem no HTML e `verify(produtoService, never()).save(...)`.

## 4. Resultados (esperado × obtido)

| Caso | Entrada | Esperado | Obtido |
|---|---|---|---|
| Produto válido | Caderno, 25.90, UN, Tilibra | Salva e redireciona para `/produtos/listar` | Passou |
| Nome vazio | `""`, `"   "`, `null` | Erro em `descricaoProduto` | Passou |
| Nome longo | 41 caracteres | Erro `Size` | Passou |
| Preço negativo | -0.01, -1, -150 | Erro "O valor precisa ser maior que zero." | Passou |
| Preço zero | 0 | Erro em `valorProduto` | Passou |
| Preço não numérico | "abc" | Erro "O valor precisa ser um número válido." | Passou |
| Nenhum campo | formulário vazio | 4 erros, nada salvo | Passou |
| Menor preço válido | 0.01 | Aceita | Passou |

Execução completa (`mvnw test`):

```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0 -- in Tema 6 - Validação do formulário de cadastro de produtos
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0 -- in Tema 6 - Regras de validação da entidade Produto
[INFO] Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Os 9 testes que já existiam no projeto continuaram passando.

## 5. Como executar

```bash
cd devops
./mvnw test -Dtest="ProdutoValidacaoTests,ProdutoCadastroValidacaoTests"
```

Os testes do Tema 6 não precisam do PostgreSQL. A suíte completa (`./mvnw test`) precisa, porque os testes
antigos usam `@SpringBootTest`.

## 6. Conclusão

As regras do cadastro agora ficam em um lugar só (a entidade), valem para qualquer forma de envio e são
conferidas automaticamente a cada execução dos testes. Se alguém remover ou alterar uma regra por engano,
os testes falham na hora, o que torna o projeto pronto para entrar em um pipeline de integração contínua.
