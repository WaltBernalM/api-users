package com.bwl.apiusers.assemblers;

import com.bwl.apiusers.dtos.ClientDTO;
import com.bwl.apiusers.models.Client;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ClientModelAssembler extends  DTOModelAssembler<ClientDTO, Client> {
}
