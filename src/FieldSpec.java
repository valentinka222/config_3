/**
 * Описание одного поля команды: имя, номер первого
 * бита и ширина в битах
 */
public record FieldSpec(String name, int start, int width) {

    /**
     * Наибольшее значение, которое может поместиться в поле
     *
     * @return максимальное значение поля
     */
    public int maxValue() {
        return (1 << width) - 1;
    }
}
