package br.com.examplefatec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordForm {

    @NotBlank(message = "Token invalido ou ausente.")
    private String token;

    @NotBlank(message = "Informe a nova senha.")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
    private String senha;

    @NotBlank(message = "Confirme a nova senha.")
    @Size(min = 8, max = 72, message = "A confirmacao deve ter entre 8 e 72 caracteres.")
    private String confirmacaoSenha;
}
