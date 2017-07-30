;This file will be executed next to the application bundle image
;I.e. current directory will contain folder BilliardScore with application files
[Setup]
AppId={{billiard.score}}
AppName=BilliardScore
AppVersion=2.0
AppVerName=BilliardScore-2.0
AppPublisher=BilliardSoftware
AppComments=BilliardScore
AppCopyright=Copyright (C) 2015
AppPublisherURL=http://billiardsoftware.be/
AppSupportURL=http://billiardsoftware.be/
AppUpdatesURL=http://billiardsoftware.be/
;DefaultDirName={localappdata}\BilliardScore
DefaultDirName={pf}\BilliardScore
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
OutputBaseFilename=BilliardScore-2.0-WIN32-Setup
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=BilliardScore\BilliardScore.ico
UninstallDisplayIcon=BilliardScore\BilliardScore.ico
UninstallDisplayName=BilliardScore
WizardImageStretch=No
WizardSmallImageFile=BilliardScore-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "BilliardScore\BilliardScore.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "BilliardScore\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\BilliardScore"; Filename: "{app}\BilliardScore.exe"; IconFilename: "{app}\BilliardScore.ico"; Check: returnTrue()
Name: "{commondesktop}\BilliardScore"; Filename: "{app}\BilliardScore.exe";  IconFilename: "{app}\BilliardScore.ico"; Check: returnFalse()

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
