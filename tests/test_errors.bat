@echo off
chcp 65001 >nul
cd /d "%~dp0.."

echo Нет аргументов
call run_asm.bat
echo.

echo Файл не найден
call run_asm.bat tests\errors\nope.json result.bin
echo.

echo Неизвестный флаг
call run_asm.bat tests\spec.json result.bin --fast
echo.

echo Некорректный JSON
call run_asm.bat tests\errors\bad_json.json result.bin
echo.

echo Неизвестная команда
call run_asm.bat tests\errors\error_op.json result.bin
echo.

echo Нет поля c
call run_asm.bat tests\errors\miss_fields.json result.bin
echo.

echo Значение не помещается в поле
call run_asm.bat tests\errors\big.json result.bin
echo.

echo Лишнее поле
call run_asm.bat tests\errors\extra_field.json result.bin
echo.

echo Строка вместо числа
call run_asm.bat tests\errors\string.json result.bin
echo.