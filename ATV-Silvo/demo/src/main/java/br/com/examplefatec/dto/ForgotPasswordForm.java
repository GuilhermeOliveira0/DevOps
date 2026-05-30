package br.com.examplefatec.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordForm {

    @NotBlank(message = "Informe o email.")
    @Email(message = "Informe um email valido.")
    @Size(max = 40, message = "O email deve ter no maximo 40 caracteres.")
    private String email;
}
