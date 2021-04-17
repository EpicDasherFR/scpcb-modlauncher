; // System and constants
Const OptionFile$ = "options.ini"
Const ConfigFile$ = "Mods/config.ini"
Const AuthorName$ = "EpicDasherFR"
Const MLVersion$ = "1.0"

Const LauncherWidth% = 1280
Const LauncherHeight% = 960
Const GraphicWidth% = LauncherWidth%
Const GraphicHeight% = LauncherHeight%
Global RealGraphicWidth%, RealGraphicHeight%

Global CurTime%, ElapsedTime%, ElapsedLoops%, PrevTime%, PrevFPSFactor%, FPSfactor%, FPSfactor2%, CheckFPS%, FPS%

Global MenuScale%
Global MouseHit1%, MouseDown1%, MouseHit2%, DoubleClick%, LastMouseHit1%, MouseUp1%, AspectRatioRatio% = 1, OnSliderID% = 0
Global Input_ResetTime# = 0
Global GrabbedEntity%

Global temp%

; // Options
Global selectedLanguage$ = GetINIString(OptionFile$, "language", "selected language")
Global SFXVolume#
Global MusicVolume#
Global RListBg%, GListBg%, BListBg%
Global RModIdle%, GModIdle%, BModIdle%
Global RModSelect%, GModSelect%, BModSelect%

; // UI and sound related
Global ButtonSFX% = LoadSound_Strict("Assets\Sounds\Button.ogg")
Global Font1%
Global Font2%
Global Font3%
Global MenuWhite%
Global MenuBlack%
Global LauncherIMG%
Global BlinkMeterIMG

Global ArrowIMG[4]

Global CreditsModDB%[2], CreditsDiscord%[2], CreditsYouTube%[2], CreditsGitHub%[2]
Global CreditsInfos$[4]
For i = 0 To 3
	CreditsInfos[i] = ""
Next

; // Mods related
Global Theme%
Global Tab$

Global currpage% = 1, maxpage% = 10

Global SelectedMod% = 1
Global SModName$, SModAuthor$, SModVersion$, SModTheme$
Global SModStartLoc$
Global sizetext$

Local themesamount% = GetINIString(ConfigFile$, "themes", "themesamount")
Global CurrMusicStream, MusicCHN
Global Music$
;~IDEal Editor Parameters:
;~C#Blitz3D