# generate-descriptor.ps1

Write-Host "Creating descriptor file for gRPC services..." -ForegroundColor Green

# Create target directory if it doesn't exist
New-Item -ItemType Directory -Path src/test/resources/wiremock/grpc -Force | Out-Null

# Get all .proto files with their relative paths from src/main/proto
$protoBase = "src/main/proto"
$protoFiles = Get-ChildItem -Path $protoBase -Recurse -Filter *.proto | ForEach-Object {
    # Get the relative path from the proto_base
    $relativePath = Resolve-Path -Path $_.FullName -Relative
    # Remove the leading .\ if present
    $relativePath = $relativePath -replace '^\.\\', ''
    $relativePath
}

# Join the files with spaces
$protoFilesList = $protoFiles -join " "

Write-Host "Found proto files:" -ForegroundColor Yellow
$protoFiles | ForEach-Object { Write-Host "  $_" }

# Run protoc with relative paths
$command = "protoc --proto_path=$protoBase --include_imports --include_source_info --descriptor_set_out=src/test/resources/wiremock/grpc/services.dsc $protoFilesList"
Write-Host "Running: $command" -ForegroundColor Cyan

Invoke-Expression $command

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Descriptor created successfully: src/test/resources/wiremock/grpc/services.dsc" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to create descriptor" -ForegroundColor Red
}