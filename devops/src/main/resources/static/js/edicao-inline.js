/* ============================================================
   Edicao no proprio quadro da lista.
   Duplo clique numa celula marcada com class="editavel" troca o
   texto por um campo. Enter grava, Esc cancela, sair do campo grava.
   A celula precisa trazer: data-url, data-valor e data-campo.
   ============================================================ */

(function () {
  "use strict";

  function meta(nome) {
    var m = document.querySelector('meta[name="' + nome + '"]');
    return m ? m.getAttribute("content") : null;
  }

  var csrfToken = meta("_csrf");
  var csrfHeader = meta("_csrf_header") || "X-CSRF-TOKEN";

  function pisca(celula, classe) {
    celula.classList.remove("salvo", "falhou");
    // reinicia a animacao: sem isso, dois saves seguidos nao piscam de novo
    void celula.offsetWidth;
    celula.classList.add(classe);
  }

  function abrirEdicao(celula) {
    if (celula.querySelector("input")) return; // ja esta aberta

    var textoOriginal = celula.textContent.trim();
    var valorOriginal = celula.dataset.valor;

    var campo = document.createElement("input");
    campo.type = "number";
    campo.step = "0.01";
    campo.min = "0";
    campo.value = valorOriginal;

    celula.textContent = "";
    celula.appendChild(campo);
    campo.focus();
    campo.select();

    var encerrado = false;

    function fechar(texto) {
      if (encerrado) return;
      encerrado = true;
      celula.textContent = texto;
    }

    function cancelar() {
      fechar(textoOriginal);
    }

    function gravar() {
      if (encerrado) return;

      var novo = campo.value;

      if (novo === "" || isNaN(parseFloat(novo)) || parseFloat(novo) < 0) {
        fechar(textoOriginal);
        pisca(celula, "falhou");
        return;
      }

      if (parseFloat(novo) === parseFloat(valorOriginal)) {
        cancelar();
        return;
      }

      encerrado = true;
      celula.textContent = "salvando...";

      var cabecalhos = { "Content-Type": "application/x-www-form-urlencoded" };
      if (csrfToken) cabecalhos[csrfHeader] = csrfToken;

      fetch(celula.dataset.url, {
        method: "POST",
        headers: cabecalhos,
        credentials: "same-origin",
        body: encodeURIComponent(celula.dataset.campo) + "=" + encodeURIComponent(novo),
      })
        .then(function (r) {
          if (!r.ok) throw new Error("HTTP " + r.status);
          return r.json();
        })
        .then(function (dados) {
          if (!dados.ok) throw new Error(dados.erro || "recusado");
          celula.textContent = dados.textoFormatado;
          celula.dataset.valor = dados.valor;
          pisca(celula, "salvo");
        })
        .catch(function (e) {
          celula.textContent = textoOriginal;
          pisca(celula, "falhou");
          console.error("Falha ao gravar a celula:", e);
        });
    }

    campo.addEventListener("keydown", function (e) {
      if (e.key === "Enter") {
        e.preventDefault();
        gravar();
      } else if (e.key === "Escape") {
        e.preventDefault();
        cancelar();
      }
    });

    campo.addEventListener("blur", gravar);
  }

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("td.editavel").forEach(function (celula) {
      celula.addEventListener("dblclick", function () {
        abrirEdicao(celula);
      });
      celula.title = "Duplo clique para editar";
    });
  });
})();
