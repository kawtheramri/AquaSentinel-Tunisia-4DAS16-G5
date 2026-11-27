@echo off
echo Arret de tous les modules AquaSentinel...
taskkill /FI "WINDOWTITLE eq AquaSentinel-*" /T /F
echo Termine.
pause