package br.com.examplefatec.service;

/**
 * Resultado de negocio do fluxo de redefinicao de senha.
 * Ajuda o controller a escolher a mensagem sem expor detalhes sensiveis.
 */
public enum PasswordResetResult {
    SUCCESS,
    INVALID_TOKEN,
    INVALID_PASSWORD,
    PASSWORD_MISMATCH
}
