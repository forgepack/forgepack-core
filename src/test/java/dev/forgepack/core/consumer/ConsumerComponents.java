package dev.forgepack.core.consumer;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.internal.controller.ControllerCrudReadImpl;
import dev.forgepack.core.internal.service.ServiceCrudReadImpl;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

record ConsumerRequest(UUID id) implements DTOIdentifiable<UUID> {}

class ConsumerResponse extends RepresentationModel<ConsumerResponse>
        implements DTOIdentifiable<UUID> {

    @Override
    public UUID id() {
        return null;
    }
}

@Component
class ConsumerMapper implements Mapper<ConsumerEntity, ConsumerRequest, ConsumerResponse> {

    @Override
    public ConsumerEntity toEntity(ConsumerRequest request) {
        return new ConsumerEntity();
    }

    @Override
    public ConsumerResponse toResponse(ConsumerEntity entity) {
        return new ConsumerResponse();
    }

    @Override
    public void updateEntity(ConsumerRequest request, ConsumerEntity entity) {}

    @Override
    public Set<ConsumerResponse> toResponseSet(Set<ConsumerEntity> entities) {
        return Set.of();
    }
}

@Service
class ConsumerService extends ServiceCrudReadImpl<ConsumerEntity, ConsumerRequest, ConsumerResponse> {

    ConsumerService(ConsumerRepository repository, ConsumerMapper mapper) {
        super(ConsumerEntity.class, repository, mapper);
    }
}

@RestController
@RequestMapping("/consumer-items")
class ConsumerController extends ControllerCrudReadImpl<ConsumerEntity, ConsumerRequest, ConsumerResponse> {

    ConsumerController(ConsumerService service) {
        super(ConsumerEntity.class, service);
    }
}