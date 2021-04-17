Global modsamount% = GetINIString(configfile$, "config", "modsamount")

Function LoadMods()
	Local diffname$
	
	SetFont Font1
	Color(0, 0, 0)
	If modsamount%<33 Then
		For i = 1 To modsamount%
			diffname$ = GetINIString(configfile$, "mod"+i, "name")
			If diffname$="" Then ErrorCode(1)
			If ClickText(690, 190+((i-1)*20), diffname$, False, i) Then
				SelectedMod% = i
				UpdateModInfos()
			EndIf
		Next
	Else
		If currpage%<maxpage% Then
			For i = 32*currpage%-32+1 To 32*currpage%
				diffname$ = GetINIString(configfile$, "mod"+i, "name")
				If diffname$="" Then ErrorCode(1)
				If ClickText(690, 190+((i-1)*20), diffname$, False, i) Then
					SelectedMod% = i
					UpdateModInfos()
				EndIf
			Next
		Else
			For i = 32*currpage%-32+1 To modsamount%
				diffname$ = GetINIString(configfile$, "mod"+i, "name")
				If diffname$="" Then ErrorCode(1)
				If ClickText(690, 190+((i-1)*20), diffname$, False, i) Then
					SelectedMod% = i
					UpdateModInfos()
				EndIf
			Next
		EndIf
	EndIf
End Function

Function PlayTheme(modid%)
	Local temptheme$
	
	StopStream_Strict(MusicCHN)
	
	DebugLog("")
	temptheme$ = GetINIString(configfile$, "mod"+SelectedMod, "theme")
	DebugLog("Getting theme name: "+temptheme)
	Music = "Assets\Sounds\Themes\"+temptheme+".ogg"
	MusicCHN = StreamSound_Strict(Music,MusicVolume,Mode)
	DebugLog("Playing theme: "+Music+" ("+SModTheme$+")")
End Function

Function ClickText%(x%, y%, txt$, bigfont% = False, id%)
	Local clicked% = False
	Local width% = 490, height% = 18
	
	If MouseOn(x, y-9, width, height) Then
		If (MouseHit1 And (Not waitForMouseUp)) Lor (MouseUp1 And waitForMouseUp) Then 
			clicked = True
			PlaySound_Strict(ButtonSFX)
		EndIf
	EndIf
	
	Color(RListBg,GListBg,BListBg)
	Rect(x, y-9, width, height)
	
	If SelectedMod%=id%
		Color(RModSelect,GModSelect,BModSelect)
	Else
		Color(RModIdle,GModIdle,BModIdle)
	EndIf
	If bigfont Then SetFont Font2 Else SetFont Font1
	Text(x, y, txt, False, True)
	
	Return clicked
End Function

Function UpdateModInfos()
	SModName$ = GetINIString(configfile$, "mod"+SelectedMod, "name")
	SModAuthor$ = GetINIString(configfile$, "mod"+SelectedMod, "author")
	SModVersion$ = GetINIString(configfile$, "mod"+SelectedMod, "version")
	SModStartLoc$ = "Mods\"+GetINIString(configfile$, "mod"+SelectedMod, "folder")+"\"+GetINIString(configfile$, "mod"+SelectedMod, "exe")+".exe"
	SModTheme$ = GetINIString(configfile$, "mod"+SelectedMod, "theme")
	If SModName$ = "" Or SModAuthor$ = "" Or SModVersion$ = "" Or SModStartLoc$ = "" Then ErrorCode(6)
	PlayTheme(SModTheme$)
	DebugLog("")
	DebugLog("Loading mod"+SelectedMod+" infos.")
	DebugLog("Loaded name: "+SModName$)
	DebugLog("Loaded author: "+SModAuthor$)
	DebugLog("Loaded version: "+SModVersion$)
	DebugLog("Loaded startloc: "+SModStartLoc$)
	DebugLog("Loaded theme: "+Music+" ("+SModTheme$+")")
End Function
;~IDEal Editor Parameters:
;~F#34
;~C#Blitz3D