package sk.mkrajcovic.challenges.model;

public enum SimulatorType {

	/**
	 * Represents original Assetto Corsa and its editions and DLC
	 * variations, like Assetto Corsa Ultimate Edition.
	 * <p>
	 * These variations are considered part of the same simulator
	 * (same engine) and therefore share a single simulator type.
	 */
	ASSETTO_CORSA("Assetto Corsa"),

	/**
	 * Represents Assetto Corsa Competizione as a separate
	 * simulator from the original Assetto Corsa.
	 */
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
