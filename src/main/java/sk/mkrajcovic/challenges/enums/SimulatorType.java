package sk.mkrajcovic.challenges.enums;

public enum SimulatorType {

    ASSETTO_CORSA("Assetto Corsa"),
    ASSETTO_CORSA_COMPETIZIONE("Assetto Corsa Competizione"),
    WRC_GENERATIONS("WRC Generations");

    private final String displayName;

    SimulatorType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
