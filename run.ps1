$ErrorActionPreference = "Stop"

if (Test-Path "bin") {
    Remove-Item -Recurse -Force "bin\*" -ErrorAction SilentlyContinue
} else {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = @(Get-ChildItem -Recurse -Filter *.java "src\main\java", "src\test\java" | ForEach-Object { $_.FullName })

javac -encoding UTF-8 -cp "lib/*" -d bin @sources
java -cp "bin;lib/*" lab1.Lab1Checks
java -cp "bin;lib/*" lab1.Lab1App
