/**
 * Ошибка ассемблера: неверный JSON, неизвестная команда
 * или значение вне диапазона
 */
public class AssemblerException extends Exception {

    /**
     * @param message описание ошибки
     */
    public AssemblerException(String message) {
        super(message);
    }
}
