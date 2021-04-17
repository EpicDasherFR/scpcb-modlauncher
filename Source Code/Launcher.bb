RealGraphicWidth = GraphicWidth
RealGraphicHeight = GraphicHeight
SetWindowSize(LauncherWidth, LauncherHeight)

If modsamount%>31968 Then ErrorCode(2)
If modsamount%<0 Then ErrorCode(4)
If modsamount%=0 Then ErrorCode(5)
If modsamount%<33 Then maxpage% = 1
If modsamount%>32 Then maxpage% = Floor(modsamount/32)+1
For i = 1 To 999
	If modsamount%/32 = i Then maxpage% = i
Next

LoadOptions()
UpdateModInfos()
PlayTheme(SModTheme$)

Function UpdateLauncher()
	Local i%, n%
	Local x%, y%
	
	;[Block]
	MenuScale = 1
	
	SetBuffer(BackBuffer())
	
	Font1% = LoadFont_Strict("Assets\Graphics\Fonts\Courier New.ttf", 16, True)
	Font2% = LoadFont_Strict("Assets\Graphics\Fonts\Courier New.ttf", 52, True)
	Font3% = LoadFont_Strict("Assets\Graphics\Fonts\Fixedsys Excelsior 3.01.ttf", 16, True)
	MenuWhite% = LoadImage_Strict("Assets\Graphics\menuwhite.png")
	MenuBlack% = LoadImage_Strict("Assets\Graphics\menublack.png")
	SetFont(Font1)
	MaskImage(MenuBlack, 255, 255, 0)
	
	BlinkMeterIMG% = LoadImage_Strict("Assets\Graphics\blinkmeter.png")
	
	CreditsModDB[0] = LoadImage_Strict("Assets\Graphics\Networks\moddb.png")
	CreditsDiscord[0] = LoadImage_Strict("Assets\Graphics\Networks\discord.png")
	CreditsYouTube[0] = LoadImage_Strict("Assets\Graphics\Networks\youtube.png")
	CreditsGitHub[0] = LoadImage_Strict("Assets\Graphics\Networks\github.png")
	CreditsModDB[1] = LoadImage_Strict("Assets\Graphics\Networks\moddb_s.png")
	CreditsDiscord[1] = LoadImage_Strict("Assets\Graphics\Networks\discord_s.png")
	CreditsYouTube[1] = LoadImage_Strict("Assets\Graphics\Networks\youtube_s.png")
	CreditsGitHub[1] = LoadImage_Strict("Assets\Graphics\Networks\github_s.png")
	
	AppTitle(global_apptitle$+MLVersion)
	;[End Block]
	
	Local Quit% = False
	
	SetTab("launcher")
	
	Repeat
		MainLoop()
		LoadStrings(selectedLanguage$)
		Color(0, 0, 0)
		Rect(0, 0, LauncherWidth, LauncherHeight)
		
		MouseHit1 = MouseHit(1)
		
		Color(255, 255, 255)
		DrawImage(LauncherIMG, 0, 0)
		
		Select Tab$
			Case "launcher"
				;[Block]
				If DrawButton(LauncherWidth - 50 - 65, 15, 100, 50, button_options$, False)
					SetTab("options")
				EndIf
				
				If DrawButton(LauncherWidth - 50 - 65, 85, 100, 50, button_quit$, False)
					SaveOptions()
					Quit = True
					End()
				EndIf
				
				
				; // Details frame
				Text(72, 155, text_details$)
				DrawFrame(72, 178, 540, 398)
				DrawFrame(72, 178, 540, 35)
				Text(72 + 540/2, 190, SModName$, True)
				Text(72 + 10, 230, infos_madeby$+" "+SModAuthor$)
				Text(72 + 10, 250, infos_version$+" "+SModVersion$)
				DisplayFileSize(SModStartLoc$)
				Text(72 + 10, 270, "Executable Size: "+sizetext$)
				;DrawFrame(72 + 540/2 - 256/2, 576 - 256, 256, 256)
				
				If DrawButton(72, 586, 128, 64, button_launchcb$, False) Then
				EndIf
				If SelectedMod<>0 Then 
					If DrawButton(484, 586, 128, 64, button_launchmod$, False) Then
						ExecFile(SModStartLoc$)
						DebugLog("")
						DebugLog("Starting "+SModStartLoc$)
						DebugLog("")
						Quit = True
						End()
					EndIf
				EndIf
				
				; // Credits frame
				Text(72, 695, text_madeby$+" "+AuthorName$)
				DrawFrame(72, 718, 526, 182)
				If DrawNetwork(76, 722, "moddb", credits_moddb$) Then
					ExecFile("https://www.moddb.com/mods/scp-containment-breach-mod-launcher/")
				EndIf
				If DrawNetwork(76 + 128 + 2, 722, "discord", credits_discord$) Then
					ExecFile("https://discord.gg/zpa5xRG/")
				EndIf
				If DrawNetwork(76 + 128*2 + 4, 722, "youtube", credits_youtube$) Then
					ExecFile("https://www.youtube.com/channel/UC3lFQDi3eJCAPAGMhGTX7ow/")
				EndIf
				If DrawNetwork(76 + 128*3 + 6, 722, "github", credits_github$) Then
					ExecFile("https://github.com/EpicDasherFR/scpcb-modlauncher/")
				EndIf
				Text(80, 880, CreditsInfos[0])
				Text(80, 880, CreditsInfos[1])
				Text(80, 880, CreditsInfos[2])
				Text(80, 880, CreditsInfos[3])
				
				Color(255,255,255)
				Text(684, 155, text_mods$)
				Color(RListBg,GListBg,BListBg)
				Rect(684, 180, 504, 680)
				;DrawTiledImageRect(MenuWhite%, 0, (180 Mod 256), 512, 512, 684, 180, 504, 680)
				
				SetFont Font1
				LoadMods()
				
				; // Page switch
				;[Block]
				If maxpage%=1 Then
					DrawFrame(684, 836, 126.5, 64)
					Color 100,100,100 
				Else 
					If DrawButton(684, 836, 126.5, 64, "", True) Then ChangePage(-1)
					Color 255,255,255
				EndIf
				SetFont Font2
				Text(684 + 126.5 / 2, 836 + 64 / 2, "<", True, True)
				
				If maxpage%=1 Then
					DrawFrame(1063, 836, 126.5, 64)
					Color 100,100,100
				Else
				If DrawButton(1063, 836, 126.5, 64, "", True) Then ChangePage(1)
					Color 255,255,255
				EndIf
				SetFont Font2
				Text(1063 + 126.5 / 2, 836 + 64 / 2, ">", True, True)
				
				Color(255,255,255)
				DrawFrame(810.5, 836, 253, 64, currpage%+"/"+maxpage%, True)
				;[End Block]
				
				;[End Block]
				
			Case "options"
				;[Block]
				If DrawButton(LauncherWidth - 50 - 65, 15, 100, 50, button_back$, False)
					SaveOptions()
					SetTab("launcher")
				EndIf
				
				If DrawButton(LauncherWidth - 50 - 65, 85, 100, 50, button_quit$, False)
					SaveOptions()
					Quit = True
					End()
				EndIf
				
				x = 72
				y = 155
				
				DrawFrame(x - 10, y - 10, 750, 700)
				;DrawFrame(x + 750, y - 10, 450, 700)
				
				Color(255,255,0)
				Text(x, y, options_tabaudio$)
				
				x = 120
				y = y + 30
				
				Color(255,255,255)
				Text(x, y, options_optmusic$)
				MusicVolume# = (SlideBar(x + 200, y, 150, MusicVolume#*100.0)/100.0)
				Color(255,255,255)
				Text(x + 200 + 250, y, "("+MusicVolume#*100+"%)")
				
				y = y + 30
				
				Text(x, y, options_optsfx$)
				SFXVolume# = (SlideBar(x + 200, y, 150, SFXVolume#*100.0)/100.0)
				Color(255,255,255)
				Text(x + 200 + 250, y, "("+SFXVolume#*100+"%)")
				
				
				
				x = 72
				y = y + 50
				
				Color(255,255,0)
				Text(x, y, options_tablang$)
				
				x = 120
				y = y + 30
				
				Color(255,255,255)
				Select selectedLanguage$
					Case "EN"
						langname$ = lang_EN$
					Case "FR"
						langname$ = lang_FR$
				End Select
				Text(x, y, options_selectedlang$+" "+langname$)
				
				y = y + 25
				
				If DrawButton(x, y, 100, 50, options_langchange$, False) Then
					SetTab("options_lang")
				EndIf
				
				x = 72
				y = y + 85
				
				Color(255,255,0)
				Text(x, y, options_tabcustom$)
				
				x = 120
				y = y + 30
				
				Color(255,255,255)
				Text(x, y, options_custom1$)
				
				y = y + 35
				
				Color(255,51,51)
				Text(x + 100, y, options_customred$)
				Color(255,255,255)
				Text(x + 500, y, options_custompreview$)
				Rect(x + 600, y, 65, 65)
				Color(RListBg,GListBg,BListBg)
				Rect(x + 603, y + 3, 59, 59)
				
				y = y - 5
				
				RListBg = ColorSlideBar(x + 170, y, 250, RListBg, RListBg)
				
				y = y + 35
				
				Color(0,255,0)
				Text(x + 100, y, options_customgreen$)
				
				y = y - 5
				
				GListBg = ColorSlideBar(x + 170, y, 250, GListBg, GListBg)
				
				y = y + 35
				
				Color(3,98,252)
				Text(x + 100, y, options_customblue$)
				
				y = y - 5
				
				BListBg = ColorSlideBar(x + 170, y, 250, BListBg, BListBg)
				
				
				x = 120
				y = y + 50
				
				Color(255,255,255)
				Text(x, y, options_custom2$)
				
				y = y + 35
				
				Color(255,51,51)
				Text(x + 100, y, options_customred$)
				Color(255,255,255)
				Text(x + 500, y, options_custompreview$)
				Rect(x + 600, y, 65, 65)
				Color(RModIdle,GModIdle,BModIdle)
				Rect(x + 603, y + 3, 59, 59)
				
				y = y - 5
				
				RModIdle = ColorSlideBar(x + 170, y, 250, RModIdle, RModIdle)
				
				y = y + 35
				
				Color(0,255,0)
				Text(x + 100, y, options_customgreen$)
				
				y = y - 5
				
				GModIdle = ColorSlideBar(x + 170, y, 250, GModIdle, GModIdle)
				
				y = y + 35
				
				Color(3,98,252)
				Text(x + 100, y, options_customblue$)
				
				y = y - 5
				
				BModIdle = ColorSlideBar(x + 170, y, 250, BModIdle, BModIdle)
				
				
				x = 120
				y = y + 50
				
				Color(255,255,255)
				Text(x, y, options_custom3$)
				
				y = y + 35
				
				Color(255,51,51)
				Text(x + 100, y, options_customred$)
				Color(255,255,255)
				Text(x + 500, y, options_custompreview$)
				Rect(x + 600, y, 65, 65)
				Color(RModSelect,GModSelect,BModSelect)
				Rect(x + 603, y + 3, 59, 59)
				
				y = y - 5
				
				RModSelect = ColorSlideBar(x + 170, y, 250, RModSelect, RModSelect)
				
				y = y + 35
				
				Color(0,255,0)
				Text(x + 100, y, options_customgreen$)
				
				y = y - 5
				
				GModSelect = ColorSlideBar(x + 170, y, 250, GModSelect, GModSelect)
				
				y = y + 35
				
				Color(3,98,252)
				Text(x + 100, y, options_customblue$)
				
				y = y - 5
				
				BModSelect = ColorSlideBar(x + 170, y, 250, BModSelect, BModSelect)
				;[End Block]
			Case "options_lang"
				If DrawButton(LauncherWidth - 50 - 65, 15, 100, 50, button_back$, False)
					SaveOptions()
					SetTab("options")
				EndIf
				
				If DrawButton(LauncherWidth - 50 - 65, 85, 100, 50, button_quit$, False)
					SaveOptions()
					Quit = True
					End()
				EndIf
				
				x = 72
				y = 155
				
				DrawFrame(x - 10, y - 10, 750, 700)
				;DrawFrame(x + 750, y - 10, 450, 700)
			Default
				ErrorCode(3)
		End Select
		SetFont Font3
		Color(255,255,255)
		Text(20, LauncherHeight - 20, "v"+MLVersion)
		Flip()
	Forever
	If Quit Then End()
	
	FreeImage(LauncherIMG)
End Function


; // Launcher functions
Function DrawFrame(x%, y%, width%, height%, txt$="", bigfont%=False, xoffset%=0, yoffset%=0)
	Color 255, 255, 255
	DrawTiledImageRect(MenuWhite%, xoffset, (y Mod 256), 512, 512, x, y, width, height)
	
	DrawTiledImageRect(MenuBlack%, yoffset, (y Mod 256), 512, 512, x+3*MenuScale, y+3*MenuScale, width-6*MenuScale, height-6*MenuScale)
	
	If bigfont Then SetFont Font2 Else SetFont Font1
	Text(x + width / 2, y + height / 2, txt, True, True)
End Function

Function DrawFrame2(x%, y%, width%, height%, txt$="", bigfont%=False, xoffset%=0, yoffset%=0)
	Color 0, 0, 0
	DrawTiledImageRect(MenuBlack%, xoffset, (y Mod 256), 512, 512, x, y, width, height)
	
	DrawTiledImageRect(MenuWhite%, yoffset, (y Mod 256), 512, 512, x+3*MenuScale, y+3*MenuScale, width-6*MenuScale, height-6*MenuScale)
	
	If bigfont Then SetFont Font2 Else SetFont Font1
	Text(x + width / 2, y + height / 2, txt, True, True)
End Function

Function DrawButton%(x%, y%, width%, height%, txt$, bigfont% = True, waitForMouseUp%=False, usingAA%=True)
	Local clicked% = False
	
	DrawFrame (x, y, width, height)
	If MouseOn(x, y, width, height) Then
		Color(30, 30, 30)
		If (MouseHit1 And (Not waitForMouseUp)) Lor (MouseUp1 And waitForMouseUp) Then 
			clicked = True
			PlaySound_Strict(ButtonSFX)
		EndIf
		Rect(x + 4, y + 4, width - 8, height - 8)	
	Else
		Color(0, 0, 0)
	EndIf
	
	Color (255, 255, 255)
	;If usingAA Then
	;	If bigfont Then AASetFont Font2 Else AASetFont Font1
	;	AAText(x + width / 2, y + height / 2, txt, True, True)
	;Else
	If bigfont Then SetFont Font2 Else SetFont Font1
	Text(x + width / 2, y + height / 2, txt, True, True)
	;EndIf
	
	Return clicked
End Function

Function DrawTick%(x%, y%, selected%, locked% = False)
	Local width% = 20 * MenuScale, height% = 20 * MenuScale
	
	Color (255, 255, 255)
	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, width, height)
	
	Local Highlight% = MouseOn(x, y, width, height) And (Not locked)
	
	If Highlight Then
		Color(50, 50, 50)
		If MouseHit1 Then selected = (Not selected) : PlaySound_Strict (ButtonSFX)
	Else
		Color(0, 0, 0)		
	EndIf
	
	Rect(x + 2, y + 2, width - 4, height - 4)
	
	If selected Then
		If Highlight Then
			Color 255,255,255
		Else
			Color 200,200,200
		EndIf
		DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x + 4, y + 4, width - 8, height - 8)
	EndIf
	
	Color 255, 255, 255
	
	Return selected
End Function

Function DrawTiledImageRect(img%, srcX%, srcY%, srcwidth#, srcheight#, x%, y%, width%, height%)
	Local x2% = x
	
	While x2 < x+width
		Local y2% = y
		While y2 < y+height
			If x2 + srcwidth > x + width Then srcwidth = srcwidth - Max((x2 + srcwidth) - (x + width), 1)
			If y2 + srcheight > y + height Then srcheight = srcheight - Max((y2 + srcheight) - (y + height), 1)
			DrawImageRect(img, x2, y2, srcX, srcY, srcwidth, srcheight)
			y2 = y2 + srcheight
		Wend
		x2 = x2 + srcwidth
	Wend
End Function

Function SlideBar#(x%, y%, width%, value#)
	
	If MouseDown1 And OnSliderID=0 Then
		If ScaledMouseX() >= x And ScaledMouseX() <= x + width + 14 And ScaledMouseY() >= y And ScaledMouseY() <= y + 20 Then
			value = Min(Max((ScaledMouseX() - x) * 100 / width, 0), 100)
		EndIf
	EndIf
	
	Color 255,255,255
	Rect(x, y, width + 14, 20,False)
	
	DrawImage(BlinkMeterIMG, x + width * value / 100.0 +3, y+3)
	
	Color 170,170,170 
	Text(x - 50 * MenuScale, y + 3 * MenuScale, options_low$)					
	Text(x + width + 38 * MenuScale, y + 3 * MenuScale, options_high$)	
	
	Return value
End Function

Function ColorSlideBar#(x%, y%, width%, value#, outputtext$)
	
	If MouseDown1 And OnSliderID=0 Then
		If ScaledMouseX() >= x And ScaledMouseX() <= x + width + 14 And ScaledMouseY() >= y And ScaledMouseY() <= y + 20 Then
			value = Min(Max((ScaledMouseX() - x) * 255 / width, 0), 255)
		EndIf
	EndIf
	
	Color 255,255,255
	Rect(x, y, width + 14, 20,False)
	
	DrawImage(BlinkMeterIMG, x + width * value / 255.0 +3, y+3)
	
	Color 170,170,170 					
	Text(x + width + 38 * MenuScale, y+4*MenuScale, outputtext)	
	
	Return value
	
End Function

Function SetTab(tabname$)
	Tab$ = tabname$
	Select tabname$
		Default
			LauncherIMG% = LoadImage_Strict("Assets\Graphics\back.png")
	End Select
End Function

Function ChangePage(factor%)
	Local temppage%=currpage%+factor
	
	If temppage%<1 Then temppage%=maxpage%
	If temppage%>maxpage% Then temppage%=1
	
	currpage%=temppage%
End Function

Function Negative(red%,green%,blue%)
	Color(255-red,255-green,255-blue)
End Function 
	
Function BlackWhite(red%,green%,blue%)
	Local m% = (red+blue+green)/3
	
	Color(m%,m%,m%)
End Function

Function NegatBW(red%,green%,blue%)
	Local m% = (red+blue+green)/3
	
	Color(255-m%,255-m%,255-m%)
End Function

Function DrawNetwork%(x%, y%, id$, outputtext$)
	Local clicked% = False
	Local width% = 128, height% = 128
	Local outputimg%
	
	If MouseOn(x, y, width, height) Then
		Select id
			Case "moddb"
				outputimg% = CreditsModDB[1]
				CreditsInfos[0] = outputtext
			Case "discord"
				outputimg% = CreditsDiscord[1]
				CreditsInfos[1] = outputtext
			Case "youtube"
				outputimg% = CreditsYouTube[1]
				CreditsInfos[2] = outputtext
			Case "github"
				outputimg% = CreditsGitHub[1]
				CreditsInfos[3] = outputtext
		End Select
		If (MouseHit1 And (Not waitForMouseUp)) Lor (MouseUp1 And waitForMouseUp) Then 
			clicked = True
			PlaySound_Strict(ButtonSFX)
		EndIf
	Else
		Select id
			Case "moddb"
				outputimg% = CreditsModDB[0]
				CreditsInfos[0] = ""
			Case "discord"
				outputimg% = CreditsDiscord[0]
				CreditsInfos[1] = ""
			Case "youtube"
				outputimg% = CreditsYouTube[0]
				CreditsInfos[2] = ""
			Case "github"
				outputimg% = CreditsGitHub[0]
				CreditsInfos[3] = ""
		End Select
	EndIf
	
	DrawImageRect(outputimg%, x, y, 0, 0, width, height)
	
	Return clicked
End Function

Function DisplayFileSize(file$)
	Local unit$, outputtext$
	Local size# = FileSize(file)
	If size<1000 Then
		unit="o"
	ElseIf size<1000000 Then
		unit="Ko"
	ElseIf size<1000000000 Then
		unit="Mo"
	ElseIf size<1000000000000 Then
		unit="Go"
	Else
		unit="To"
	EndIf
	
	If unit="o" Then outputtext$=size+" "+unit$
	;If unit="Ko" Then outputtext$=Floor(size*10^-3)+" "+unit
	If unit="Mo" Then outputtext$=Floor(size*10^-6)+" "+unit
	If unit="Go" Then outputtext$=Floor(size*10^-9)+" "+unit
	If unit="To" Then outputtext$=Floor(size*10^-12)+" "+unit
	
	If unit="Ko" Then outputtext$=(Floor(size*10^-1)*10^-2)+" "+unit
	If unit="Mo" Then outputtext$=(Floor(size*10^-4)*10^-2)+" "+unit
	
	sizetext$=outputtext$
End Function
;~IDEal Editor Parameters:
;~F#15
;~C#Blitz3D