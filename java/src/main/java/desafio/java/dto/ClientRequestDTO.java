package desafio.java.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "Somente letras, números e espaços")
    private String name;

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotNull
    @Size(min = 1, message = "Ao menos um email é obrigatório")
    @Valid
    private List<EmailDTO> emails;

    @NotNull
    @Size(min = 1, message = "Ao menos um telefone é obrigatório")
    @Valid
    private List<PhoneDTO> phones;

    @NotNull
    @Size(min = 1, message = "Ao menos um endereço é obrigatório")
    @Valid
    private List<AddressDTO> addresses;


}
