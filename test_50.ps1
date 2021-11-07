Write-Host "Start testing"

for ($h = 0; $h -lt 10; $h++) {
    for ($i = 0; $i -lt 10; $i++) {
        Start-Process -NoNewWindow java.exe PublicServices
        for ($j = 0; $j -lt 50; $j++) {
            $condation = Get-Random -Maximum 4
            switch ($condation) {
                0 { 
                    Start-Process -NoNewWindow -ArgumentList "Acceptor", "-1" java.exe
                }
                1 { 
                    Start-Process -NoNewWindow -ArgumentList "Acceptor", "30" java.exe
                }
                { 2 -or 3 } { 
                    Start-Process -NoNewWindow -ArgumentList "Acceptor", "2" java.exe
                }
            }
        }
        if ($j -gt 1) {
            java Proposer $($j * 50)
            Write-Host "One round finished"
        }
        Start-Sleep -Seconds 3
    }
    Start-Sleep -Seconds 4
    Get-Process -Name java | Stop-Process
}