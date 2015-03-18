unit Unit1;

interface

uses
  Windows, SysUtils, Classes, Forms, ShellApi, IniFiles, Dialogs;

type
  TForm1 = class(TForm)
    procedure FormActivate(Sender: TObject);
    procedure FormCreate(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

const
  ERROR_RESULT = 255;
  NEED_UPDATE_RESULT = 10;
  SHOW_APPLICATION = SW_RESTORE;
  HIDE_APPLICATION = SW_HIDE;
  MIN_VISTA_VERSION = 6;
  UPDATE_PARAM = 'update';
  CHECK_VERSION = 'newversion';
  SILENT = '-silent';

var
  Form1: TForm1;
  currentDirectory: string;
  currentExe: string;

implementation

uses Math;

{$R *.dfm}

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
  buffer     : array[1..MAX_PATH] of WideChar;
  bufSize:Integer;
  sysFolder: String;
  javaPath: String;
  javaParams: String;
begin
  if aNeedWait then
  begin
    ZeroMemory(@startInfo, SizeOf(TStartupInfo));
    startInfo.cb := SizeOf(TStartupInfo);
    ZeroMemory(@processInfo, SizeOf(TProcessInformation));
    bufSize := GetSystemDirectory(PWideChar(@buffer), MAX_PATH);
    SetString(sysFolder, PWideChar(@buffer), bufSize);
    javaPath := sysFolder + '\' + aFile;
    javaParams := aParameters;
    if CreateProcess(PWideChar(javaPath), PWideChar(javaParams), nil, nil, false, 0, nil,
      nil, startInfo, processInfo) then
    begin
      WaitForSingleObject(processInfo.hProcess, INFINITE);
      GetExitCodeProcess(processInfo.hProcess, extCode);
      CloseHandle(processInfo.hProcess);
      Result := extCode;
    end
    else
    begin
      lastErrorMsg := SysErrorMessage(GetLastError());
      Result := ERROR_RESULT;
    end;
  end
  else
  begin
    ZeroMemory(@runParams, SizeOf(TShellExecuteInfo));
    with runParams do
    begin
      if aElevate then
        lpVerb := 'runas'
      else
        lpVerb := '';
      cbSize := SizeOf(TShellExecuteInfo);
      Wnd := Form1.Handle;
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
  Result := ' -cp "' + MAIN_CP + ';' + EXT_CLASSES + '" ' + MAIN_CN + ' ' +
    typeRun + ' -laf ' + LAF_CN + ' -curl "' + URL_CONFIG + '" -uurl "' +
    URL_UPDATE + '" -cname "' + CONFIG_NAME + '" -uname "' + TMP_UPDATE_NAME +
    '" -path "' + PLATYPUS_HOME + '"';
end;

procedure TForm1.FormActivate(Sender: TObject);
var
  i: integer;
  statusCode: integer;
  command: String;
  isSilent: String;
begin
  command := '';
  isSilent := 'false';
  Left := Screen.Width div 2;
  Top := Screen.Height div 2;
  currentDirectory := ExtractFilePath(ParamStr(0));
  currentExe := ExtractFileName(ParamStr(0));
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
    end;
  end;
  if command <> EmptyStr then
  begin
    if command = UPDATE_PARAM then
    begin
      runApp('javaw.exe', generateRunParams('update' + ' ' + SILENT + ' ' +
        isSilent), SHOW_APPLICATION, getNeedUAC, false);
    end
    else if command = CHECK_VERSION then
    begin
      statusCode := runApp('javaw.exe',
        generateRunParams('newversion' + ' ' + SILENT + ' ' + isSilent),
        SHOW_APPLICATION, false, true);
      Halt(statusCode);
    end;
  end
  else
  begin
    if runApp('javaw.exe', generateRunParams('newversion'), SHOW_APPLICATION,
      false, true) = NEED_UPDATE_RESULT then
    begin
      runApp(currentDirectory + currentExe, UPDATE_PARAM, HIDE_APPLICATION,
        getNeedUAC, true)
    end;
  end;
  Close;
  Exit;
end;

procedure TForm1.FormCreate(Sender: TObject);
begin
  Width := 0;
  Height := 0;
end;

end.
