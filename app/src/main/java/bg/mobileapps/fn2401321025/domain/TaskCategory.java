package bg.mobileapps.fn2401321025.domain;

/** Категория на задачата. В базата се пази само числото id. */
public enum TaskCategory {
    PERSONAL(0),
    STUDY(1),
    WORK(2),
    SHOPPING(3);

    public final int id;

    TaskCategory(int id) {
        this.id = id;
    }

    /** Непознато id (напр. от по-нова версия) се приема за "Лични". */
    public static TaskCategory fromId(int id) {
        for (TaskCategory category : values()) {
            if (category.id == id) {
                return category;
            }
        }
        return PERSONAL;
    }
}
