Write-Host "Start testing"
for ($h = 0; $h -lt 10; $h++) {
    for ($i = 0; $i -lt 10; $i++) {
        Start-Process -NoNewWindow java.exe PublicServices
        for ($j = 0; $j -lt 50; $j++) {
            Start-Process -NoNewWindow -ArgumentList "Acceptor", "2" java.exe
        }
        if ($i -gt 0) {
            java Proposer $($i * 50)
            Write-Host "One round finished"
            Start-Sleep -Seconds 3
        }
    }
    Start-Sleep -Seconds 4
    Stop-Process -Name "java"
}