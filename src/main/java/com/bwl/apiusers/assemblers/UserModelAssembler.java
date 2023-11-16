package com.bwl.apiusers.assemblers;

import com.bwl.apiusers.dtos.UserDTO;
import com.bwl.apiusers.models.User;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserModelAssembler extends DTOModelAssembler<UserDTO, User> {
}
