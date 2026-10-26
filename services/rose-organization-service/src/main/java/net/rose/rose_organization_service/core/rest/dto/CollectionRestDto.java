package net.rose.rose_organization_service.core.rest.dto;


import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionRestDto<T> {

    @Singular
    private List<T> items;

}
