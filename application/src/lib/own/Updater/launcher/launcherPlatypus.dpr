program launcherPlatypus;

uses
  Windows, SysUtils, Classes, ShellApi, IniFiles, Math;

const
  ERROR_RESULT = 255;
  NEED_UPDATE_RESULT = 10;
  SHOW_APPLICATION = SW_RESTORE;
  HIDE_APPLICATION = SW_HIDE;
  MIN_VISTA_VERSION = 6;
  UPDATE_PARAM = 'update';
  CHECK_VERSION = 'newversion';
  SILENT = '-silent';
  JAVA_HOME = '-java-home';

var
  currentDirectory: string;
  
function getNeedUAC: boolean;
var
  ver: Cardinal;
  majorVersion: Word;
begin
  Result := false;
  ver := GetVersion;
  majorVersion := LoByte(LoWord(ver));
  if majorVersion >= MIN_VISTA_VERSION then
    Result := true;
end;

function runApp(const aFile, aParameters: string; aWinShow: integer;
  aElevate: boolean = false; aNeedWait: boolean = false): byte;
var
  runParams: TShellExecuteInfo;
  processInfo: TProcessInformation;
  startInfo: TStartupInfo;
  extCode: Cardinal;
  lastErrorMsg: String;
  javaParams: String;
begin
  if aNeedWait then
  begin
//    MessageBox(0, '1', '', 0);
    ZeroMemory(@startInfo, SizeOf(TStartupInfo));
    startInfo.cb := SizeOf(TStartupInfo);
    ZeroMemory(@processInfo, SizeOf(TProcessInformation));
    javaParams := aFile + ' ' + aParameters;
//    MessageBox(0, PWideChar('2 ' + javaParams), '', 0);
    if CreateProcess(nil, PWideChar(javaParams), nil, nil, false, 0, nil, nil,
      startInfo, processInfo) then
    begin
//      MessageBox(0, '3 ', '', 0);
      WaitForSingleObject(processInfo.hProcess, INFINITE);
      GetExitCodeProcess(processInfo.hProcess, extCode);
      CloseHandle(processInfo.hProcess);
//      MessageBox(0, PWideChar('4 ' + IntToStr(extCode)), '', 0);
      Result := extCode;
    end
    else
    begin
      lastErrorMsg := SysErrorMessage(GetLastError());
//      MessageBox(0, PWideChar('6 ' + lastErrorMsg), '', 0);
      Result := ERROR_RESULT;
    end;
  end
  else
  begin
//    MessageBox(0, '-1', '', 0);
    ZeroMemory(@runParams, SizeOf(TShellExecuteInfo));
    with runParams do
    begin
      if aElevate then
        lpVerb := 'runas'
      else
        lpVerb := '';
      cbSize := SizeOf(TShellExecuteInfo);
      Wnd := 0;
      lpDirectory := nil;
      lpFile := PWideChar(aFile);
      lpParameters := PWideChar(aParameters);
      nShow := aWinShow;
      fMask := SEE_MASK_NOASYNC;
    end;
    if ShellExecuteEx(@runParams) then
    begin
      Result := 0;
    end
    else
    begin
      Result := ERROR_RESULT;
    end;
  end;
end;

function generateRunParams(const typeRun: string): string;
var
  LAF_CN, EXT_CLASSES, MAIN_CN, MAIN_CP: String;
  URL_CONFIG, URL_UPDATE, CONFIG_NAME, TMP_UPDATE_NAME, PLATYPUS_HOME: string;
  settingsFile: TMemIniFile;
begin
  LAF_CN := 'com.jtattoo.plaf.fast.FastLookAndFeel';
  EXT_CLASSES := currentDirectory + '../ext/*';
  MAIN_CN := 'com.eas.client.updater.Updater';
  MAIN_CP := currentDirectory + '../lib/own/Updater.jar';

  settingsFile := TMemIniFile.Create(currentDirectory + 'update.ini');
  try
    URL_CONFIG := settingsFile.ReadString('Update', 'URLConfig',
      'http://optimus.altsoft.biz:8080/update/update.xml');
    URL_UPDATE := settingsFile.ReadString('Update', 'URLUpdate',
      'http://optimus.altsoft.biz:8080/update/application.zip');
    CONFIG_NAME := currentDirectory + settingsFile.ReadString('Update',
      'ConfigFileName', 'version.xml');
    TMP_UPDATE_NAME := currentDirectory + settingsFile.ReadString('Update',
      'UpdateArchiveName', '../app.zip');
    PLATYPUS_HOME := currentDirectory + settingsFile.ReadString('Update',
      'HomeDirectory', '../');
  finally
    settingsFile.Destroy;
  end;
  Result := '-cp "' + MAIN_CP + ';' + EXT_CLASSES + '" ' + MAIN_CN + ' ' +
    typeRun + ' -laf ' + LAF_CN + ' -curl "' + URL_CONFIG + '" -uurl "' +
    URL_UPDATE + '" -cname "' + CONFIG_NAME + '" -uname "' + TMP_UPDATE_NAME +
    '" -path "' + PLATYPUS_HOME + '"';
end;

procedure process;
var
  i: integer;
  statusCode: integer;
  command: String;
  isSilent: String;
  javaHome: String;
  javaExe: String;
begin
  command := '';
  javaHome := '';
  isSilent := 'false';
  currentDirectory := ExtractFilePath(ParamStr(0));
  if ParamCount > 0 then
  begin
    for i := 1 to ParamCount do
    begin
      if ParamStr(i) = UPDATE_PARAM then
      begin
        command := UPDATE_PARAM;
      end;
      if ParamStr(i) = CHECK_VERSION then
      begin
        command := CHECK_VERSION;
      end;
      if ParamStr(i) = SILENT then
      begin
        if ParamCount >= i + 1 then
        begin
          if LowerCase(ParamStr(i + 1)) = 'true' then
          begin
            isSilent := 'true';
          end;
        end;
      end;
      if ParamStr(i) = JAVA_HOME then
      begin
        if ParamCount >= i + 1 then
        begin
          javaHome := ParamStr(i + 1);
        end;
      end;
    end;
  end;
  if(javaHome <> '') then
    javaExe := javaHome + '\bin\javaw.exe'
  else
    javaExe := 'javaw.exe';
//  MessageBox(0, PWideChar('-7- ' + javaExe), '', 0);
  if command <> EmptyStr then
  begin
    if command = UPDATE_PARAM then
    begin
      runApp(javaExe, generateRunParams('update' + ' ' + SILENT + ' ' +
        isSilent), SHOW_APPLICATION, getNeedUAC, false);
    end
    else if command = CHECK_VERSION then
    begin
      statusCode := runApp(javaExe,
        generateRunParams('newversion' + ' ' + SILENT + ' ' + isSilent),
        SHOW_APPLICATION, false, true);
      ExitProcess(statusCode);
    end;
  end
  else
  begin
    if runApp(javaExe, generateRunParams('newversion'), SHOW_APPLICATION,
      false, true) = NEED_UPDATE_RESULT then
    begin
      runApp(javaExe, generateRunParams('update'), SHOW_APPLICATION, getNeedUAC(), false);
    end;
  end;
end;

begin
  process();
end.
