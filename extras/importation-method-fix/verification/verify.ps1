$ErrorActionPreference = 'Stop'
$fixDirectory = Split-Path $PSScriptRoot -Parent
$verificationClasses = Join-Path $env:TEMP ('importation-export-verification-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $verificationClasses | Out-Null
$repository = Join-Path $env:USERPROFILE '.m2/repository'
$primefaces = Join-Path $repository 'org/primefaces/primefaces/6.1/primefaces-6.1.jar'
$faces = Join-Path $repository 'org/glassfish/javax.faces/2.3.9/javax.faces-2.3.9.jar'
if (!(Test-Path $primefaces) -or !(Test-Path $faces)) {
    throw 'Resolve org.primefaces:primefaces:6.1 and org.glassfish:javax.faces:2.3.9 with Maven first.'
}
$classpath = "$primefaces;$faces"
& javac -source 8 -target 8 -cp $classpath -d $verificationClasses (Join-Path $fixDirectory 'ImportationExportController.java') (Join-Path $PSScriptRoot 'ExportVerification.java')
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
& java -cp "$verificationClasses;$classpath" dps.jsf.ExportVerification
if ($LASTEXITCODE -ne 0) { throw 'Verification failed' }
$xml = New-Object System.Xml.XmlDocument
$xml.XmlResolver = $null
$xml.Load((Join-Path $fixDirectory 'importationList.xhtml'))
Write-Output 'PASS: replacement XHTML parses.'
