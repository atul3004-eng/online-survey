$ErrorActionPreference = 'Stop'
$fixDirectory = Split-Path $PSScriptRoot -Parent
$verificationClasses = Join-Path $env:TEMP ('importation-export-verification-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $verificationClasses | Out-Null
$repository = Join-Path $env:USERPROFILE '.m2/repository'
$dependencies = @(
    'org/primefaces/primefaces/6.1/primefaces-6.1.jar',
    'javax/javaee-api/7.0/javaee-api-7.0.jar',
    'org/apache/poi/poi/3.14/poi-3.14.jar',
    'org/jsoup/jsoup/1.8.3/jsoup-1.8.3.jar'
)
$jars = @($dependencies | ForEach-Object {
    $jar = Join-Path $repository $_
    if (!(Test-Path $jar)) { throw "Resolve dependency with Maven first: $_" }
    $jar
})
$classpath = $jars -join ';'
$sources = @((Join-Path $fixDirectory 'ImportationExportController.java'), (Join-Path $PSScriptRoot 'ExportVerification.java'))
$sources += @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'stubs') -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })
& javac -encoding UTF-8 -source 8 -target 8 -cp $classpath -d $verificationClasses $sources
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
& java -cp "$verificationClasses;$classpath" dps.jsf.ExportVerification
if ($LASTEXITCODE -ne 0) { throw 'Verification failed' }
$xml = New-Object System.Xml.XmlDocument
$xml.XmlResolver = $null
$xml.Load((Join-Path $fixDirectory 'importationList.xhtml'))
Write-Output 'PASS: replacement XHTML parses.'
