package sk.mkrajcovic.challenges.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import sk.mkrajcovic.challenges.enums.SimulatorType;

@Entity
@Getter @Setter
public class Simulator extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private SimulatorType name;

}
