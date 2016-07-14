;This file will be executed next to the application bundle image
;I.e. current directory will contain folder Billiardcompetition with application files
[Setup]
AppId={{billiard.competition}}
AppName=BilliardCompetition
AppVersion=1.0
AppVerName=BilliardCompetition-1.0
AppPublisher=LWd&D
AppComments=BilliardCompetition
AppCopyright=Copyright (C) 2015
AppPublisherURL=http://billiardsoftware.be/
AppSupportURL=http://billiardsoftware.be/
AppUpdatesURL=http://billiardsoftware.be/
;DefaultDirName={localappdata}\BilliardCompetition
DefaultDirName={pf}\BilliardCompetition
DisableStartupPrompt=Yes
DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=LWd&D
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,6.1 
OutputBaseFilename=BilliardCompetition-1.0-WIN{amd64}-Setup
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=BilliardCompetition\BilliardCompetition.ico
UninstallDisplayIcon=BilliardCompetition\BilliardCompetition.ico
UninstallDisplayName=BilliardCompetition
WizardImageStretch=No
WizardSmallImageFile=BilliardCompetition-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "BilliardCompetition\BilliardCompetition.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "BilliardCompetition\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\BilliardCompetition"; Filename: "{app}\BilliardCompetition.exe"; IconFilename: "{app}\BilliardCompetition.ico"; Check: returnTrue()
Name: "{commondesktop}\BilliardCompetition"; Filename: "{app}\BilliardScore.exe";  IconFilename: "{app}\BilliardCompetition.ico"; Check: returnFalse()

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
