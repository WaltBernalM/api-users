package com.bwl.apiusers.assemblers;

import com.bwl.apiusers.models.Profile;
import com.bwl.apiusers.dtos.ProfileDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ProfileModelAssembler extends DTOModelAssembler<ProfileDTO, Profile> {
}
