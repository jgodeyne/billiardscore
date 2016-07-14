;This file will be executed next to the application bundle image
;I.e. current directory will contain folder BilliardScore with application files
[Setup]
AppId={{billiard.score}}
AppName={applname}
AppVersion={version}
AppVerName={applname}-{version}
AppPublisher={vendor}
AppComments={applname}
AppCopyright=Copyright (C) 2015
AppPublisherURL=http://billiardsoftware.be/
AppSupportURL=http://billiardsoftware.be/
AppUpdatesURL=http://billiardsoftware.be/
;DefaultDirName={localappdata}\{applname}
DefaultDirName={pf}\{applname}
DisableStartupPrompt=Yes
DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName={vendor}
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,6.1 
OutputBaseFilename={applname}-{version}-WIN32-Setup
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile={applname}\{applname}.ico
UninstallDisplayIcon={applname}\{applname}.ico
UninstallDisplayName={applname}
WizardImageStretch=No
WizardSmallImageFile={applname}-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "{applname}\{applname}.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "{applname}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\{applname}"; Filename: "{app}\{applname}.exe"; IconFilename: "{app}\{applname}.ico"; Check: returnTrue()
Name: "{commondesktop}\{applname}"; Filename: "{app}\{applname}.exe";  IconFilename: "{app}\{applname}.ico"; Check: returnFalse()

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
