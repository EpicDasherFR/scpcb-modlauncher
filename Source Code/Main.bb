Include "Source Code/Variables.bb"

Include "Source Code/Languages.bb"
LoadStrings(selectedLanguage$)

Local InitErrorStr$ = ""

If FileSize("fmod.dll")=0 Then InitErrorStr=InitErrorStr+ "fmod.dll"+Chr(13)+Chr(10)
If FileSize("BlitzMovie.dll")=0 Then InitErrorStr=InitErrorStr+ "BlitzMovie.dll"+Chr(13)+Chr(10)
If FileSize("dplayx.dll")=0 Then InitErrorStr=InitErrorStr+ "dplayx.dll"+Chr(13)+Chr(10)
If FileSize("FreeImage.dll")=0 Then InitErrorStr=InitErrorStr+ "FreeImage.dll"+Chr(13)+Chr(10)
If FileSize("zlibwapi.dll")=0 Then InitErrorStr=InitErrorStr+ "zlibwapi.dll"+Chr(13)+Chr(10)

If Len(InitErrorStr)>0 Then
	ErrorCode(0)
EndIf

; // Files including
Include "Source Code/Functions.bb"
Include "Source Code/Mods.bb"
Include "Source Code/Launcher.bb"

For i = 0 To 3
	ArrowIMG[i] = LoadImage_Strict("Assets\Graphics\arrow.png")
	RotateImage(ArrowIMG[i], 90 * i)
	HandleImage(ArrowIMG[i], 0, 0)
Next

UpdateLauncher()

Function MainLoop()
	CurTime = MilliSecs2()
	ElapsedTime = (CurTime - PrevTime) / 1000.0
	PrevTime = CurTime
	PrevFPSFactor = FPSfactor
	FPSfactor = Max(Min(ElapsedTime * 70, 5.0), 0.2)
	FPSfactor2 = FPSfactor
	
	If CheckFPS < MilliSecs2() Then
		FPS = ElapsedLoops
		ElapsedLoops = 0
		CheckFPS = MilliSecs2()+1000
	EndIf
	ElapsedLoops = ElapsedLoops + 1
	
	If Input_ResetTime<=0.0
		DoubleClick = False
		;MouseHit1 = MouseHit(1)
		If MouseHit1 Then
			If MilliSecs2() - LastMouseHit1 < 800 Then DoubleClick = True
			LastMouseHit1 = MilliSecs2()
		EndIf
	
		Local prevmousedown1 = MouseDown1
		MouseDown1 = MouseDown(1)
		If prevmousedown1 = True And MouseDown1=False Then MouseUp1 = True Else MouseUp1 = False
	
		MouseHit2 = MouseHit(2)
		
		If (Not MouseDown1) And (Not MouseHit1) Then GrabbedEntity = 0
	Else
		Input_ResetTime = Max(Input_ResetTime-FPSfactor,0.0)
	EndIf
End Function

Function ErrorCode(id%)
	Local temperror$
	
	Select ErrorCode
		Case 0
			temperror = error_0A$+Chr(13)+Chr(10)+Chr(13)+Chr(10)+InitErrorStr+Chr(13)+Chr(10)+error_0B$
		Case 1
			temperror = error_1A$+i+" "+error_1B$
		Case 2
			temperror = error_2$
		Case 3
			temperror = error_3A$+" "+Chr(34)+Tab$+Chr(34)+" "+error_3B$
		Case 4
			temperror = error_4$
		Case 5
			temperror = error_5$
		Case 6
			temperror = error_6A$+SelectedMod+error_6B+" "+configfile$+error_6C$
	End Select 
	
	RuntimeError = temperror
End Function
;~IDEal Editor Parameters:
;~F#1E#41
;~C#Blitz3D