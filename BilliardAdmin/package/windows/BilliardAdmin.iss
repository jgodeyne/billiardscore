;This file will be executed next to the application bundle image
;I.e. current directory will contain folder BilliardAdmin with application files
[Setup]
AppId={{billiard.admin}}
AppName=BilliardAdmin
AppVersion=2.0
AppVerName=BilliardAdmin-2.0
AppPublisher=BilliardSoftware
AppComments=BilliardAdmin
AppCopyright=Copyright (C) 2015
AppPublisherURL=http://billiardsoftware.be/
AppSupportURL=http://billiardsoftware.be/
AppUpdatesURL=http://billiardsoftware.be/
;DefaultDirName={localappdata}\BilliardAdmin
DefaultDirName={pf}\BilliardAdmin
DisableStartupPrompt=Yes
DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=BilliardSoftware
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,6.1 
OutputBaseFilename=BilliardAdmin-2.0-WIN32-Setup
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=BilliardAdmin\BilliardAdmin.ico
UninstallDisplayIcon=BilliardAdmin\BilliardAdmin.ico
UninstallDisplayName=BilliardAdmin
WizardImageStretch=No
WizardSmallImageFile=BilliardAdmin-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "BilliardAdmin\BilliardAdmin.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "BilliardAdmin\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\BilliardAdmin"; Filename: "{app}\BilliardAdmin.exe"; IconFilename: "{app}\BilliardAdmin.ico"; Check: returnTrue()
Name: "{commondesktop}\BilliardAdmin"; Filename: "{app}\BilliardAdmin.exe";  IconFilename: "{app}\BilliardAdmin.ico"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
