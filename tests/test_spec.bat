@echo off
chcp 65001 >nul
cd /d "%~dp0.."
if not exist out mkdir out
call run_asm.bat tests\spec.json result.bin --test > out\actual.txt
fc tests\spec_expected.txt out\actual.txt >nul
if errorlevel 1 (
    echo FAIL: вывод отличается от спецификации
) else (
    echo OK: поля и значения совпали со спецификацией
)