Write-Host "Running" -NoNewline
for ($i = 1; $i -le 3; $i++) {
    Start-Sleep -Seconds 0.1
    Write-Host "." -NoNewline
}
Write-Host ""  # Print newline...

try {
    # Do not log with ANSI escape sequences & Minecraft color codes...
    java -Xmx512M -Xms512M -DIReallyKnowWhatIAmDoingISwear "-Dterminal.ansi=false" -jar paper.jar nogui --log-strip-color > server.log 2>&1
}
finally {
    Write-Host "Ended!"
}
