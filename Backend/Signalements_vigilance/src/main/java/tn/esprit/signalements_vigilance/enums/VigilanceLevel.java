package tn.esprit.signalements_vigilance.enums;

public enum VigilanceLevel {
    VERT(0), JAUNE(40), ORANGE(60), ROUGE(80);

    private final int minIndex;

    VigilanceLevel(int minIndex) {
        this.minIndex = minIndex;
    }

    public int getMinIndex() {
        return minIndex;
    }

    /** vert (0-39), jaune (40-59), orange (60-79), rouge (80-100) */
    public static VigilanceLevel fromIndex(int index) {
        if (index >= ROUGE.minIndex) return ROUGE;
        if (index >= ORANGE.minIndex) return ORANGE;
        if (index >= JAUNE.minIndex) return JAUNE;
        return VERT;
    }
}
