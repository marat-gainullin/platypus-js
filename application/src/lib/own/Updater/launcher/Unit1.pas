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
  SHOW_APPLICATION   = SW_RESTORE;
  HIDE_APPLICATION   = SW_HIDE;
  MIN_VISTA_VERSION  = 6;
  UPDATE_PARAM       = 'upd';

var
  Form1: TForm1;
  currentDirectory: string;
  currentExe: string;

implementation

uses Math;

{$R *.dfm}

function getNeedUAC:boolean;
var
  ver:Cardinal;
  majorVersion:Word;
begin
  Result := false;
  ver := GetVersion;
  majorVersion := LoByte(LoWord(ver));
  if majorVersion >= MIN_VISTA_VERSION then
    Result:=true;
end;

function runApp(const aFile,aParametrs:string; aWinShow:integer; aElevate:boolean=false; aNeedWait:boolean=false):byte;
var
  runParams:TShellExecuteInfo;
  extCode:Cardinal;
begin
  extCode := ERROR_RESULT;
  Result := extCode;
  ZeroMemory(@runParams, SizeOf(TShellExecuteInfo));
  with runParams do
  begin
     if aElevate then
       lpVerb := PAnsiChar('runas')
     else
       lpVerb := PAnsiChar('');
     cbSize := SizeOf(TShellExecuteInfo);
     Wnd := Form1.Handle;
     lpDirectory := nil;
     lpFile := PAnsiChar(aFile);
     lpParameters := PAnsiChar(aParametrs);
     nShow := aWinShow;
     fMask := SEE_MASK_NOCLOSEPROCESS;
  end;
  if ShellExecuteEx(@runParams) then
   begin
     if runParams.hProcess <> 0 then
      begin
        try
        if aNeedWait then
        begin
          WaitForSingleObject(runParams.hProcess, INFINITE);
          GetExitCodeProcess(runParams.hProcess, extCode);
        end;
        finally
          CloseHandle(runParams.hProcess);
        end;
      end;
     Result := extCode;
   end;
end;

function generateRunParams(const typeRun:string):string;
var
  LAF_CN, EXT_CLASSES, MAIN_CN, MAIN_CP:String;
  URL_CONFIG, URL_UPDATE, CONFIG_NAME, TMP_UPDATE_NAME, PLATYPUS_HOME:string;
  settingsFile:TMemIniFile;
begin
  LAF_CN := 'com.jtattoo.plaf.fast.FastLookAndFeel';
  EXT_CLASSES := currentDirectory+'../ext/*';
  MAIN_CN := 'com.eas.client.updater.Updater';
  MAIN_CP := currentDirectory+'../lib/own/Updater.jar';

  settingsFile := TMemIniFile.Create(currentDirectory+'update.ini');
  try
    URL_CONFIG := settingsFile.ReadString('Update', 'URLConfig', 'http://optimus.altsoft.biz:8080/update/update.xml');
    URL_UPDATE := settingsFile.ReadString('Update', 'URLUpdate', 'http://optimus.altsoft.biz:8080/update/application.zip');
    CONFIG_NAME := currentDirectory+settingsFile.ReadString('Update', 'ConfigFileName', 'version.xml');
    TMP_UPDATE_NAME := currentDirectory+settingsFile.ReadString('Update', 'UpdateArchiveName', '../app.zip');
    PLATYPUS_HOME := currentDirectory+settingsFile.ReadString('Update', 'HomeDirectory', '../');

    Result:='-cp "'+MAIN_CP+'";"'+EXT_CLASSES+'" "'+MAIN_CN+'" '+typeRun+' -laf "'+LAF_CN+'" -curl "'+URL_CONFIG+'" -uurl "'+URL_UPDATE+
          '" -cname "'+CONFIG_NAME+'" -uname "'+TMP_UPDATE_NAME+'" -path "'+PLATYPUS_HOME+'"';
  finally
    settingsFile.Destroy;
  end;
end;

function replaceUpdaterFile():boolean;
var
  newFile, oldFile:String;
begin
  Result  := false;
  newFile := currentDirectory+'../lib/own/Updater-new.jar';
  oldFile := currentDirectory+'../lib/own/Updater.jar';
  if FileExists(newFile) then
  begin
    if (DeleteFile(oldFile)) then
      if RenameFile(newFile, oldFile) then
        Result := true;
  end;
end;

procedure TForm1.FormActivate(Sender: TObject);
begin
  Left := Screen.Width div 2;
  Top := Screen.Height div 2;
  currentDirectory := ExtractFilePath(ParamStr(0));
  currentExe := ExtractFileName(ParamStr(0));
  if ParamStr(1) = UPDATE_PARAM then begin
     if runApp('javaw', generateRunParams('update'), SHOW_APPLICATION, false, true) <> ERROR_RESULT then begin
        replaceUpdaterFile();
     end;
  end else begin
     if runApp('javaw', generateRunParams('newversion'), SHOW_APPLICATION, false, true) = NEED_UPDATE_RESULT then begin
       runApp(currentDirectory + currentExe, UPDATE_PARAM, HIDE_APPLICATION, getNeedUAC, true)
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
