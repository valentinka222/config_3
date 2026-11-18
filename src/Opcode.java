import java.util.List;

/**
 * Команды УВМ: название в JSON, код(поле А), размер в байтах
 * и поля B, C, D
 */
public enum Opcode {
    LOAD_CONST("load_const", 142, 4, List.of(
            new FieldSpec("b", 8, 11),
            new FieldSpec("c", 19, 7)
    )),
    READ_MEM("read_mem", 75, 3, List.of(
            new FieldSpec("b", 8, 7),
            new FieldSpec("c", 15, 7)
    )),
    WRITE_MEM("write_mem", 17, 5, List.of(
            new FieldSpec("b", 8, 11),
            new FieldSpec("c", 19, 7),
            new FieldSpec("d", 26, 7)
    )),
    POPCNT("popcnt", 161, 5, List.of(
            new FieldSpec("b", 8, 11),
            new FieldSpec("c", 19, 7),
            new FieldSpec("d", 26, 7)
    ));

    private final String mnemonic;
    private final int code;
    private final int size;
    private final List<FieldSpec> fields;

    /**
     * @param mnemonic название команды в JSON
     * @param code     код команды (поле A)
     * @param size     размер команды в байтах
     * @param fields   поля команды, кроме A
     */
    Opcode(String mnemonic, int code, int size, List<FieldSpec> fields) {
        this.mnemonic = mnemonic;
        this.code = code;
        this.size = size;
        this.fields = fields;
    }

    /**
     * @return название команды в JSON
     */
    public String getMnemonic() {
        return mnemonic;
    }

    /**
     * @return код команды(поле A)
     */
    public int getCode() {
        return code;
    }

    /**
     * @return размер команды в байтах
     */
    public int getSize() {
        return size;
    }

    /**
     * @return поля команды, кроме A
     */
    public List<FieldSpec> getFields() {
        return fields;
    }

    /**
     * Находит команду по названию из JSON
     *
     * @param mnemonic название команды
     * @return найденная команда
     * @throws AssemblerException если такой команды нет
     */
    public static Opcode fromMnemonic(String mnemonic)
            throws AssemblerException {
        for (Opcode opcode : values()) {
            if (opcode.mnemonic.equals(mnemonic)) {
                return opcode;
            }
        }
        throw new AssemblerException("неизвестная команда '" + mnemonic + "'");
    }
}
