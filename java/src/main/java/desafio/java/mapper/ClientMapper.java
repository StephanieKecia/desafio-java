package desafio.java.mapper;

import desafio.java.dto.ClientResponseDTO;
import desafio.java.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "cpf", target = "cpf")
    ClientResponseDTO mapToResponseDTO(ClientEntity client);

}
