Set WshShell = CreateObject("WScript.Shell")
WshShell.Run "cmd.exe /c cd C:\SD\ShutdownTime\src && java Shut_Down.SimpleUsageLimiter", 0, False
