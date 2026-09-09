package sk.mkrajcovic.challenges.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter @Setter
public class Simulator extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private SimulatorType name;

}
