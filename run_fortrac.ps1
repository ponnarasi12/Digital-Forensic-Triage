# FORTRAC Spring Boot Launcher Script
$toolsDir = "C:\Users\DELL\tools"

# Find JDK 17 directory
$jdkHome = Get-ChildItem -Path "$toolsDir\jdk17" -Filter "bin" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 | ForEach-Object { $_.Parent.FullName }
if (-not $jdkHome) {
    $jdkHome = Get-ChildItem -Path "$toolsDir\jdk17" -Filter "java.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 | ForEach-Object { $_.Directory.Parent.FullName }
}

# Find Maven directory
$mvnBin = Get-ChildItem -Path "$toolsDir\maven" -Filter "mvn.cmd" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 | ForEach-Object { $_.Directory.FullName }

if ($jdkHome) {
    $env:JAVA_HOME = $jdkHome
    $env:PATH = "$jdkHome\bin;$env:PATH"
    Write-Host "[FORTRAC] Configured JAVA_HOME: $jdkHome"
} else {
    Write-Host "[FORTRAC] Warning: Portable JDK home not found in $toolsDir\jdk17. Attempting system java..."
}

if ($mvnBin) {
    $env:PATH = "$mvnBin;$env:PATH"
    Write-Host "[FORTRAC] Configured Maven path: $mvnBin"
} else {
    Write-Host "[FORTRAC] Warning: Portable Maven bin not found in $toolsDir\maven. Attempting system mvn..."
}

Write-Host "[FORTRAC] Java Version Check:"
java -version

Write-Host "[FORTRAC] Building and Launching FORTRAC System..."
mvn spring-boot:run
