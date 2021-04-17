; // AAFonts
Global AASelectedFont%
Global AATextCam%,AATextSprite%[256]
Global AACharW%,AACharH%

Global AACamViewW%,AACamViewH%

Type AAFont
	Field texture%
	Field backup% ;images don't get erased by clearworld
	Field x%[256]
	Field y%[256]
	Field w%[256]
	Field h%[256]
	Field lowResFont% ;for use on other buffers
	Field mW%
	Field mH%
	Field texH%
	Field isAA%
End Type

Function InitAAFont()
	If AATextEnable Then
		;Create Camera
		Local cam% = CreateCamera()
		
		CameraViewport cam,0,0,10,10
		CameraZoom cam, 0.1
		CameraClsMode cam, 0, 0
		CameraRange cam, 0.1, 1.5
		MoveEntity cam, 0, 0, -20000
		AATextCam = cam
		CameraProjMode cam,0
		
		;Create sprite
		Local spr% = CreateMesh(cam)
		Local sf% = CreateSurface(spr)
		
		AddVertex sf, -1, 1, 0, 0, 0
		AddVertex sf, 1, 1, 0, 1, 0
		AddVertex sf, -1, -1, 0, 0, 1
		AddVertex sf, 1, -1, 0, 1, 1
		AddTriangle sf, 0, 1, 2
		AddTriangle sf, 3, 2, 1
		EntityFX spr, 17+32
		PositionEntity spr, 0, 0, 1.0001
		EntityOrder spr, -100001
		EntityBlend spr, 1
		AATextSprite[0] = spr : HideEntity AATextSprite[0]
		For i%=1 To 255
			spr = CopyMesh(AATextSprite[0],cam)
			EntityFX spr, 17+32
			PositionEntity spr, 0, 0, 1.0001
			EntityOrder spr, -100001
			EntityBlend spr, 1
			AATextSprite[i] = spr : HideEntity AATextSprite[i]
		Next
	EndIf
End Function

Function AASpritePosition(ind%,x%,y%)
	;THE HORROR
	Local nx# = (((Float(x-(AACamViewW/2))/Float(AACamViewW))*2))
	Local ny# = -(((Float(y-(AACamViewH/2))/Float(AACamViewW))*2))
	
	;how does this work pls help
	nx = nx-((1.0/Float(AACamViewW))*(((AACharW-2) Mod 2)))+(1.0/Float(AACamViewW))
	ny = ny-((1.0/Float(AACamViewW))*(((AACharH-2) Mod 2)))+(1.0/Float(AACamViewW))
	
	PositionEntity AATextSprite[ind],nx,ny,1.0
End Function

Function AASpriteScale(ind%,w%,h%)
	ScaleEntity AATextSprite[ind],1.0/Float(AACamViewW)*Float(w), 1/Float(AACamViewW)*Float(h), 1
	AACharW = w : AACharH = h
End Function

Function ReloadAAFont() ;CALL ONLY AFTER CLEARWORLD
	If AATextEnable Then
		InitAAFont()
		For font.AAFont = Each AAFont
			If font\isAA Then
				font\texture = CreateTexture(1024,1024,3)
				LockBuffer ImageBuffer(font\backup)
				LockBuffer TextureBuffer(font\texture)
				For ix%=0 To 1023
					For iy%=0 To font\texH
						px% = ReadPixelFast(ix,iy,ImageBuffer(font\backup)) Shl 24
						WritePixelFast(ix,iy,$FFFFFF+px,TextureBuffer(font\texture))
					Next
				Next
				UnlockBuffer TextureBuffer(font\texture)
				UnlockBuffer ImageBuffer(font\backup)
			EndIf
		Next
	EndIf
End Function

Function AASetFont(fnt%)
	AASelectedFont = fnt
	Local font.AAFont = Object.AAFont(AASelectedFont)
	If AATextEnable And font\isAA Then
		For i%=0 To 255
			EntityTexture AATextSprite[i],font\texture
		Next
	EndIf
End Function

Function AAStringWidth%(txt$)
	Local font.AAFont = Object.AAFont(AASelectedFont)
	If (AATextEnable) And (font\isAA) Then
		Local retVal%=0
		For i=1 To Len(txt)
			Local char% = Asc(Mid(txt,i,1))
			
			If char>=0 And char<=127 Then
				retVal=retVal+font\w[char]-2
			EndIf
		Next
		Return retVal+2
	Else
		SetFont font\lowResFont
		Return StringWidth(txt)
	EndIf
End Function

Function AAStringHeight%(txt$)
	Local font.AAFont = Object.AAFont(AASelectedFont)
	If (AATextEnable) And (font\isAA) Then
		Return font\mH
	Else
		SetFont font\lowResFont
		Return StringHeight(txt)
	EndIf
End Function

Function AAText(x%,y%,txt$,cx%=False,cy%=False,a#=1.0)
	If Len(txt)=0 Then Return
	Local font.AAFont = Object.AAFont(AASelectedFont)
	
	If (GraphicsBuffer()<>BackBuffer()) Lor (Not AATextEnable) Lor (Not font\isAA) Then
		SetFont font\lowResFont
		Local oldr% = ColorRed() : Local oldg% = ColorGreen() : Local oldb% = ColorBlue()
		Color oldr*a,oldg*a,oldb*a
		Text x,y,txt,cx,cy
		Color oldr,oldg,oldb
		Return
	EndIf
	
	If cx Then
		x=x-(AAStringWidth(txt)/2)
	EndIf
	
	If cy Then
		y=y-(AAStringHeight(txt)/2)
	EndIf
	
	If Camera<>0 Then HideEntity Camera
	If ark_blur_cam<>0 Then HideEntity ark_blur_cam
	
	Local tX% = 0
	CameraProjMode AATextCam,2
	
	Local char%
	
	Local tw%=0
	For i=1 To Len(txt)
		char = Asc(Mid(txt,i,1))
		If char>=0 And char<=127 Then
			tw=tw+font\w[char]
		EndIf
	Next
	
	AACamViewW = tw
	AACamViewW = AACamViewW+(AACamViewW Mod 2)
	AACamViewH = AAStringHeight(txt)
	AACamViewH = AACamViewH+(AACamViewH Mod 2)
	
	Local vx% = x : If vx<0 Then vx=0
	Local vy% = y : If vy<0 Then vy=0
	Local vw% = AACamViewW+(x-vx) : If vw+vx>GraphicWidth Then vw=GraphicWidth-vx
	Local vh% = AACamViewH+(y-vy) : If vh+vy>GraphicHeight Then vh=GraphicHeight-vy
	vw = vw-(vw Mod 2)
	vh = vh-(vh Mod 2)
	AACamViewH = AACamViewH+(AACamViewH Mod 2)
	AACamViewW = vw : AACamViewH = vh
	
	
	CameraViewport AATextCam,vx,vy,vw,vh
	For i=1 To Len(txt)
		EntityAlpha AATextSprite[i-1],a
		EntityColor AATextSprite[i-1],ColorRed(),ColorGreen(),ColorBlue()
		ShowEntity AATextSprite[i-1]
		char% = Asc(Mid(txt,i,1))
		If char>=0 And char<=127 Then
			AASpriteScale(i-1,font\w[char],font\h[char])
			AASpritePosition(i-1,tX+(x-vx)+(font\w[char]/2),(y-vy)+(font\h[char]/2))
			VertexTexCoords GetSurface(AATextSprite[i-1],1),0,Float(font\x[char])/1024.0,Float(font\y[char])/1024.0
			VertexTexCoords GetSurface(AATextSprite[i-1],1),1,Float(font\x[char]+font\w[char])/1024.0,Float(font\y[char])/1024.0
			VertexTexCoords GetSurface(AATextSprite[i-1],1),2,Float(font\x[char])/1024.0,Float(font\y[char]+font\h[char])/1024.0
			VertexTexCoords GetSurface(AATextSprite[i-1],1),3,Float(font\x[char]+font\w[char])/1024.0,Float(font\y[char]+font\h[char])/1024.0
			tX = tX+font\w[char]-2
		EndIf
	Next
	RenderWorld
	CameraProjMode AATextCam,0
	
	For i=1 To Len(txt)
		HideEntity AATextSprite[i-1]
	Next
	
	If Camera<>0 Then ShowEntity Camera
	If ark_blur_cam<>0 Then ShowEntity ark_blur_cam
End Function

Function AALoadFont%(file$="Tahoma", height=13, AATextScaleFactor%=2)
	Local newFont.AAFont = New AAFont
	
	newFont\lowResFont = LoadFont_Strict(file,height)
	
	SetFont newFont\lowResFont
	newFont\mW = FontWidth()
	newFont\mH = FontHeight()
	
	If AATextEnable And AATextScaleFactor>1 Then
		Local hResFont% = LoadFont(file,height*AATextScaleFactor)
		Local tImage% = CreateTexture(1024,1024,3)
		Local tX% = 0 : Local tY% = 1
		
		SetFont hResFont
		Local tCharImage% = CreateImage(FontWidth()+2*AATextScaleFactor,FontHeight()+2*AATextScaleFactor)
		ClsColor 0,0,0
		LockBuffer TextureBuffer(tImage)
		
		Local miy% = newFont\mH*((newFont\mW*95/1024)+2)
		DebugLog miy
		
		newFont\mW = 0
		
		For ix%=0 To 1023
			For iy%=0 To miy
				WritePixelFast(ix,iy,$FFFFFF,TextureBuffer(tImage))
			Next
		Next
		
		For i=32 To 126
			SetBuffer ImageBuffer(tCharImage)
			Cls
			
			Color 255,255,255
			SetFont hResFont
			Text AATextScaleFactor/2,AATextScaleFactor/2,Chr(i)
			
			Local tw% = StringWidth(Chr(i)) : Local th% = FontHeight()
			
			SetFont newFont\lowResFont
			
			Local dsw% = StringWidth(Chr(i)) : Local dsh% = FontHeight()
			Local wRatio# = Float(tw)/Float(dsw)
			Local hRatio# = Float(th)/Float(dsh)
			
			SetBuffer BackBuffer()
			
			LockBuffer ImageBuffer(tCharImage)
			
			For iy%=0 To dsh-1
				For ix%=0 To dsw-1
					Local rsx% = Int(Float(ix)*wRatio-(wRatio*0.0))
					If (rsx<0) Then rsx=0
					Local rsy% = Int(Float(iy)*hRatio-(hRatio*0.0))
					If (rsy<0) Then rsy=0
					Local rdx% = Int(Float(ix)*wRatio+(wRatio*1.0))
					If (rdx>tw) Then rdx=tw-1
					Local rdy% = Int(Float(iy)*hRatio+(hRatio*1.0))
					If (rdy>th) Then rdy=th-1
					Local ar% = 0
					If Abs(rsx-rdx)*Abs(rsy-rdy)>0 Then
						For iiy%=rsy To rdy-1
							For iix%=rsx To rdx-1
								ar=ar+((ReadPixelFast(iix,iiy,ImageBuffer(tCharImage)) And $FF))
							Next
						Next
						ar = ar/(Abs(rsx-rdx)*Abs(rsy-rdy))
						If ar>255 Then ar=255
						ar = ((Float(ar)/255.0)^(0.5))*255
					EndIf
					WritePixelFast(ix+tX,iy+tY,$FFFFFF+(ar Shl 24),TextureBuffer(tImage))
				Next
			Next
			
			UnlockBuffer ImageBuffer(tCharImage)
			
			newFont\x[i]=tX
			newFont\y[i]=tY
			newFont\w[i]=dsw+2
			
			If newFont\mW<newFont\w[i]-3 Then newFont\mW=newFont\w[i]-3
			
			newFont\h[i]=dsh+2
			tX=tX+newFont\w[i]+2
			If (tX>1024-FontWidth()-4) Then
				tX=0
				tY=tY+FontHeight()+6
			EndIf
		Next
		
		newFont\texH = miy
		
		Local backup% = CreateImage(1024,1024)
		
		LockBuffer ImageBuffer(backup)
		For ix%=0 To 1023
			For iy%=0 To newFont\texH
				px% = ReadPixelFast(ix,iy,TextureBuffer(tImage)) Shr 24
				px=px+(px Shl 8)+(px Shl 16)
				WritePixelFast(ix,iy,$FF000000+px,ImageBuffer(backup))
			Next
		Next
		UnlockBuffer ImageBuffer(backup)
		newFont\backup = backup
		
		UnlockBuffer TextureBuffer(tImage)
		
		
		FreeImage tCharImage
		FreeFont hResFont
		newFont\texture = tImage
		newFont\isAA = True
	Else
		newFont\isAA = False
	EndIf
	Return Handle(newFont)
End Function

; // INI Functions
Type INIFile
	Field name$
	Field bank%
	Field bankOffset% = 0
	Field size%
End Type

Function ReadINILine$(file.INIFile)
	Local rdbyte%
	Local firstbyte% = True
	Local offset% = file\bankOffset
	Local bank% = file\bank
	Local retStr$ = ""
	
	rdbyte = PeekByte(bank,offset)
	While ((firstbyte) Lor ((rdbyte<>13) And (rdbyte<>10))) And (offset<file\size)
		rdbyte = PeekByte(bank,offset)
		If ((rdbyte<>13) And (rdbyte<>10)) Then
			firstbyte = False
			retStr=retStr+Chr(rdbyte)
		EndIf
		offset=offset+1
	Wend
	file\bankOffset = offset
	Return retStr
End Function

Function UpdateINIFile$(filename$)
	Local file.INIFile = Null
	
	For k.INIFile = Each INIFile
		If k\name = Lower(filename) Then
			file = k
			Exit
		EndIf
	Next
	
	If file=Null Then Return
	
	If file\bank<>0 Then FreeBank file\bank
	
	Local f% = ReadFile(file\name)
	Local fleSize% = 1
	
	While fleSize<FileSize(file\name)
		fleSize=fleSize*2
	Wend
	file\bank = CreateBank(fleSize)
	file\size = 0
	While Not Eof(f)
		PokeByte(file\bank,file\size,ReadByte(f))
		file\size=file\size+1
	Wend
	CloseFile(f)
End Function

Function GetINIString$(file$, section$, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	Local lfile.INIFile = Null
	
	For k.INIFile = Each INIFile
		If k\name = Lower(file) Then
			lfile = k
			Exit
		EndIf
	Next
	
	If lfile = Null Then
		DebugLog "CREATE BANK FOR "+file
		lfile = New INIFile
		lfile\name = Lower(file)
		lfile\bank = 0
		UpdateINIFile(lfile\name)
	EndIf
	
	lfile\bankOffset = 0
	
	section = Lower(section)
	
	While lfile\bankOffset<lfile\size
		Local strtemp$ = ReadINILine(lfile)
		
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			If Mid(strtemp, 2, Len(strtemp)-2)=section Then
				Repeat
					TemporaryString = ReadINILine(lfile)
					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
						Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
					EndIf
				Until (Left(TemporaryString, 1) = "[") Lor (lfile\bankOffset>=lfile\size)
				Return defaultvalue
			EndIf
		EndIf
	Wend
	Return defaultvalue
End Function

Function GetINIInt%(file$, section$, parameter$, defaultvalue% = 0)
	Local txt$ = GetINIString(file$, section$, parameter$, defaultvalue)
	
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function

Function GetINIFloat#(file$, section$, parameter$, defaultvalue# = 0.0)
	Return Float(GetINIString(file$, section$, parameter$, defaultvalue))
End Function

Function GetINIString2$(file$, start%, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	Local f% = ReadFile(file)
	Local n%=0
	
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		
		n=n+1
		If n=start Then 
			Repeat
				TemporaryString = ReadLine(f)
				If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
					CloseFile f
					Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
				EndIf
			Until Left(TemporaryString, 1) = "[" Lor Eof(f)
			CloseFile f
			Return defaultvalue
		EndIf
	Wend
	CloseFile f	
	Return defaultvalue
End Function

Function GetINIInt2%(file$, start%, parameter$, defaultvalue$="")
	Local txt$ = GetINIString2(file$, start%, parameter$, defaultvalue$)
	
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function

Function GetINISectionLocation%(file$, section$)
	Local Temp%
	Local f% = ReadFile(file)
	Local n%=0
	
	section = Lower(section)
	
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		
		n=n+1
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			Temp = Instr(strtemp, section)
			If Temp>0 Then
				If Mid(strtemp, Temp-1, 1)="[" Lor Mid(strtemp, Temp-1, 1)="|" Then
					CloseFile f
					Return n
				EndIf
			EndIf
		EndIf
	Wend
	CloseFile f
End Function

Function PutINIValue%(file$, INI_sSection$, INI_sKey$, INI_sValue$)
	; Returns: True (Success) or False (Failed)
	INI_sSection = "[" + Trim$(INI_sSection) + "]"
	
	Local INI_sUpperSection$ = Upper$(INI_sSection)
	
	INI_sKey = Trim$(INI_sKey)
	INI_sValue = Trim$(INI_sValue)
	
	Local INI_sFilename$ = file$
	; Retrieve the INI Data (If it exists)
	Local INI_sContents$ = INI_FileToString(INI_sFilename)
	; (Re)Create the INI file updating/adding the SECTION, KEY And VALUE
	Local INI_bWrittenKey% = False
	Local INI_bSectionFound% = False
	Local INI_sCurrentSection$ = ""
	
	Local INI_lFileHandle% = WriteFile(INI_sFilename)
	
	If INI_lFileHandle = 0 Then Return False ; Create file failed!
	
	Local INI_lOldPos% = 1
	Local INI_lPos% = Instr(INI_sContents, Chr$(0))
	
	While (INI_lPos <> 0)
		Local INI_sTemp$ = Mid$(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos))
		
		If (INI_sTemp <> "") Then
			If Left$(INI_sTemp, 1) = "[" And Right$(INI_sTemp, 1) = "]" Then
				; Process SECTION
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				EndIf
				INI_sCurrentSection = Upper$(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
			Else
				If Left(INI_sTemp, 1) = ":" Then
					WriteLine INI_lFileHandle, INI_sTemp
				Else
					; KEY=VALUE				
					Local lEqualsPos% = Instr(INI_sTemp, "=")
					
					If (lEqualsPos <> 0) Then
						If (INI_sCurrentSection = INI_sUpperSection) And (Upper$(Trim$(Left$(INI_sTemp, (lEqualsPos - 1)))) = Upper$(INI_sKey)) Then
							If (INI_sValue <> "") Then INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
							INI_bWrittenKey = True
						Else
							WriteLine INI_lFileHandle, INI_sTemp
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		; Move through the INI file...
		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr$(0), INI_lOldPos)
	Wend
	
	; KEY wasn;t found in the INI file - Append a New SECTION If required And create our KEY=VALUE Line
	If (INI_bWrittenKey = False) Then
		If (INI_bSectionFound = False) Then INI_CreateSection INI_lFileHandle, INI_sSection
		INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
	EndIf
	
	CloseFile INI_lFileHandle
	
	Return True ; Success
End Function

Function INI_FileToString$(INI_sFilename$)
	Local INI_sString$ = ""
	Local INI_lFileHandle%= ReadFile(INI_sFilename)
	
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine$(INI_lFileHandle) + Chr$(0)
		Wend
		CloseFile INI_lFileHandle
	EndIf
	Return INI_sString
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine INI_lFileHandle, "" ; Blank Line between sections
	WriteLine INI_lFileHandle, INI_sNewSection
	Return INI_sNewSection
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	WriteLine INI_lFileHandle, INI_sKey + " = " + INI_sValue
	Return True
End Function

Function StripFilename$(file$)
	Local mi$=""
	Local lastSlash%=0
	
	If Len(file)>0
		For i%=1 To Len(file)
			mi=Mid(file$,i,1)
			If mi="\" Lor mi="/" Then
				lastSlash=i
			EndIf
		Next
	EndIf
	Return Left(file,lastSlash)
End Function

Function StripPath$(file$) 
	Local name$=""
	
	If Len(file$)>0 
		For i=Len(file$) To 1 Step -1 
			mi$=Mid$(file$,i,1) 
			If mi$="\" Lor mi$="/" Then Return name$
			name$=mi$+name$ 
		Next 
	EndIf 
	Return name$ 
End Function

Function Piece$(s$,entry,char$=" ")
	While Instr(s,char+char)
		s=Replace(s,char+char,char)
	Wend
	For n=1 To entry-1
		p=Instr(s,char)
		s=Right(s,Len(s)-p)
	Next
	p=Instr(s,char)
	If p<1
		a$=s
	Else
		a=Left(s,p-1)
	EndIf
	Return a
End Function

Function KeyValue$(entity,key$,defaultvalue$="")
	properties$=EntityName(entity)
	properties$=Replace(properties$,Chr(13),"")
	key$=Lower(key)
	Repeat
		p=Instr(properties,Chr(10))
		If p Then test$=(Left(properties,p-1)) Else test=properties
		testkey$=Piece(test,1,"=")
		testkey=Trim(testkey)
		testkey=Replace(testkey,Chr(34),"")
		testkey=Lower(testkey)
		If testkey=key Then
			value$=Piece(test,2,"=")
			value$=Trim(value$)
			value$=Replace(value$,Chr(34),"")
			Return value
		EndIf
		If Not p Then Return defaultvalue$
		properties=Right(properties,Len(properties)-p)
	Forever 
End Function

; // Strict functions
Type Sound
	Field internalHandle%
	Field name$
	Field channels%[32]
	Field releaseTime%
End Type

Function AutoReleaseSounds()
	Local snd.Sound
	
	For snd.Sound = Each Sound
		Local tryRelease% = True
		
		For i = 0 To 31
			If snd\channels[i] <> 0 Then
				If ChannelPlaying(snd\channels[i]) Then
					tryRelease = False
					snd\releaseTime = MilliSecs2()+5000
					Exit
				EndIf
			EndIf
		Next
		If tryRelease Then
			If snd\releaseTime < MilliSecs2() Then
				If snd\internalHandle <> 0 Then
					FreeSound snd\internalHandle
					snd\internalHandle = 0
				EndIf
			EndIf
		EndIf
	Next
End Function

Function PlaySound_Strict%(sndHandle%)
	Local snd.Sound = Object.Sound(sndHandle)
	
	If snd <> Null Then
		Local shouldPlay% = True
		For i = 0 To 31
			If snd\channels[i] <> 0 Then
				If Not ChannelPlaying(snd\channels[i]) Then
					If snd\internalHandle = 0 Then
						If FileType(snd\name) <> 1 Then
						Else
							If EnableSFXRelease Then snd\internalHandle = LoadSound(snd\name)
						EndIf
						If snd\internalHandle = 0 Then
						EndIf
					EndIf
					If ConsoleFlush Then
						snd\channels[i] = PlaySound(ConsoleFlushSnd)
					Else
						snd\channels[i] = PlaySound(snd\internalHandle)
					EndIf
					ChannelVolume snd\channels[i],SFXVolume#
					snd\releaseTime = MilliSecs2()+5000 ;release after 5 seconds
					Return snd\channels[i]
				EndIf
			Else
				If snd\internalHandle = 0 Then
					If FileType(snd\name) <> 1 Then
					Else
						If EnableSFXRelease Then snd\internalHandle = LoadSound(snd\name)
					EndIf
					
					If snd\internalHandle = 0 Then
					EndIf
				EndIf
				If ConsoleFlushSnd Then
					snd\channels[i] = PlaySound(ConsoleFlushSnd)
				Else
					snd\channels[i] = PlaySound(snd\internalHandle)
				EndIf
				ChannelVolume snd\channels[i],SFXVolume#
				snd\releaseTime = MilliSecs2()+5000 ;release after 5 seconds
				Return snd\channels[i]
			EndIf
		Next
	EndIf
	
	Return 0
End Function

Function LoadSound_Strict(file$)
	Local snd.Sound = New Sound
	
	snd\name = file
	snd\internalHandle = 0
	snd\releaseTime = 0
	If (Not EnableSFXRelease) Then
		If snd\internalHandle = 0 Then 
			snd\internalHandle = LoadSound(snd\name)
		EndIf
	EndIf
	Return Handle(snd)
End Function

Function FreeSound_Strict(sndHandle%)
	Local snd.Sound = Object.Sound(sndHandle)
	
	If snd <> Null Then
		If snd\internalHandle <> 0 Then
			FreeSound snd\internalHandle
			snd\internalHandle = 0
		EndIf
		Delete snd
	EndIf
End Function

Type Stream
	Field chn%
End Type

Const Mode% = 2
Const TwoD% = 8192

Function StreamSound_Strict(file$,volume#=1.0,custommode=Mode)
	If FileType(file$)<>1
		Return 0
	EndIf
	
	Local st.Stream = New Stream
	
	st\chn = PlayMusic(file, custommode + TwoD)
	
	If st\chn = -1
		Return -1
	EndIf
	ChannelVolume(st\chn, volume * 1.0)
	
	Return Handle(st)
End Function

Function StopStream_Strict(streamHandle%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		Return
	EndIf
	
	StopChannel(st\chn)
	Delete st
	
End Function

Function SetStreamVolume_Strict(streamHandle%,volume#)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		Return
	EndIf
	ChannelVolume(st\chn, volume * 1.0)
End Function

Function SetStreamPaused_Strict(streamHandle%,paused%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		Return
	EndIf
	
	If paused Then
		PauseChannel(st\chn)
	Else
		ResumeChannel(st\chn)
	EndIf
End Function

Function IsStreamPlaying_Strict(streamHandle%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		Return
	EndIf
	Return(ChannelPlaying(st\chn))
End Function

Function SetStreamPan_Strict(streamHandle%,pan#)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		Return
	EndIf
	ChannelPan(st\chn, pan)
End Function

Function UpdateStreamSoundOrigin(streamHandle%,cam%,entity%,range#=10,volume#=1.0)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
			SetStreamVolume_Strict(streamHandle,volume#*(1-dist#)*SFXVolume#)
			SetStreamPan_Strict(streamHandle,panvalue)
		Else
			SetStreamVolume_Strict(streamHandle,0.0)
		EndIf
	Else
		If streamHandle <> 0 Then
			SetStreamVolume_Strict(streamHandle,0.0)
		EndIf 
	EndIf
End Function

Function LoadMesh_Strict(File$,parent=0)
	If FileType(File$) <> 1 Then RuntimeError "3D Mesh " + File$ + " not found."
	tmp = LoadMesh(File$, parent)
	If tmp = 0 Then RuntimeError "Failed to load 3D Mesh: " + File$ 
	Return tmp  
End Function   

Function LoadAnimMesh_Strict(File$,parent=0)
	DebugLog File
	If FileType(File$) <> 1 Then RuntimeError "3D Animated Mesh " + File$ + " not found."
	tmp = LoadAnimMesh(File$, parent)
	If tmp = 0 Then RuntimeError "Failed to load 3D Animated Mesh: " + File$ 
	Return tmp
End Function   

;don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict(File$,flags=1)
	If FileType(File$) <> 1 Then RuntimeError "Texture " + File$ + " not found."
	tmp = LoadTexture(File$, flags+(256*(EnableVRam=True)))
	If tmp = 0 Then RuntimeError "Failed to load Texture: " + File$ 
	Return tmp 
End Function   

Function LoadBrush_Strict(file$,flags,u#=1.0,v#=1.0)
	If FileType(file$)<>1 Then RuntimeError "Brush Texture " + file$ + " not found."
	tmp = LoadBrush(file$, flags, u, v)
	If tmp = 0 Then RuntimeError "Failed to load Brush: " + file$ 
	Return tmp 
End Function 

Function LoadFont_Strict(file$="Tahoma", height=13, IgnoreScaling% = False)
	If FileType(file$)<>1 Then RuntimeError "Font " + file$ + " not found."
	tmp = LoadFont(file, (Int(height * (GraphicHeight / 1024.0))) * (Not IgnoreScaling) + IgnoreScaling * height)
	If tmp = 0 Then RuntimeError "Failed to load Font: " + file$ 
	Return tmp
End Function

Function LoadImage_Strict(file$)
	If FileType(file$)<>1 Then RuntimeError "Image " + Chr(34) + file$ + Chr(34) + " not found. "
	tmp = LoadImage(file$)
	If tmp = 0 Then RuntimeError "Failed to load Image: " + file$ 
	Return tmp
End Function

; // Math functions
Function GenerateSeedNumber(seed$)
	Local temp% = 0
	Local shift% = 0
	
	For i = 1 To Len(seed)
		temp = temp Xor (Asc(Mid(seed,i,1)) Shl shift)
		shift=(shift+1) Mod 24
	Next
	Return temp
End Function

Function CurveValue#(number#, old#, smooth#)
	If FPSfactor = 0 Then Return old
	
	If number < old Then
		Return Max(old + (number - old) * (1.0 / smooth * FPSfactor), number)
	Else
		Return Min(old + (number - old) * (1.0 / smooth * FPSfactor), number)
	EndIf
End Function

Function CurveAngle#(val#, old#, smooth#)
	If FPSfactor = 0 Then Return old
	
	Local diff# = WrapAngle(val) - WrapAngle(old)
	
	If diff > 180 Then diff = diff - 360
	If diff < - 180 Then diff = diff + 360
	Return WrapAngle(old + diff * (1.0 / smooth * FPSfactor))
End Function

Function WrapAngle#(angle#)
	If angle = Infinity Then Return(0.0)
	If angle < 0.0 Then
		Return(360.0 + (angle Mod 360.0))
	Else
		Return(angle Mod 360.0)
	EndIf
End Function

Function point_direction#(x1#,z1#,x2#,z2#)
	Local dx#, dz#
	
	dx = x1 - x2
	dz = z1 - z2
	Return ATan2(dz,dx)
End Function

Function angleDist#(a0#,a1#)
	Local b# = a0-a1
	Local bb#
	
	If b<-180.0 Then
		bb = b+360.0
	ElseIf b>180.0 Then
		bb = b-360.0
	Else
		bb = b
	EndIf
	Return bb
End Function

Function f2s$(n#, count%)
	Return Left(n, Len(Int(n))+count+1)
End Function

Function ScaledMouseX%()
	Return Float(MouseX()-(RealGraphicWidth*0.5*(1.0-AspectRatioRatio)))*Float(GraphicWidth)/Float(RealGraphicWidth*AspectRatioRatio)
End Function

Function ScaledMouseY%()
	Return Float(MouseY())*Float(GraphicHeight)/Float(RealGraphicHeight)
End Function

Function MouseOn%(x%, y%, width%, height%)
	If ScaledMouseX() > x And ScaledMouseX() < x + width Then
		If ScaledMouseY() > y And ScaledMouseY() < y + height Then
			Return True
		EndIf
	EndIf
	Return False
End Function

Function MilliSecs2()
	Local retVal% = MilliSecs()
	
	If retVal < 0 Then retVal = retVal + 2147483648
	Return retVal
End Function

; // Graphics functions
Global fresize_image%, fresize_texture%, fresize_texture2%
Global fresize_cam%

Function InitFastResize()
	;Create Camera
	Local cam% = CreateCamera()
	CameraProjMode cam, 2
	CameraZoom cam, 0.1
	CameraClsMode cam, 0, 0
	CameraRange cam, 0.1, 1.5
	MoveEntity cam, 0, 0, -10000
	
	fresize_cam = cam
	
	;Create sprite
	Local spr% = CreateMesh(cam)
	Local sf% = CreateSurface(spr)
	AddVertex sf, -1, 1, 0, 0, 0
	AddVertex sf, 1, 1, 0, 1, 0
	AddVertex sf, -1, -1, 0, 0, 1
	AddVertex sf, 1, -1, 0, 1, 1
	AddTriangle sf, 0, 1, 2
	AddTriangle sf, 3, 2, 1
	EntityFX spr, 17
	ScaleEntity spr, 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicHeight), 1
	PositionEntity spr, 0, 0, 1.0001
	EntityOrder spr, -100001
	EntityBlend spr, 1
	fresize_image = spr
	
	;Create texture
	fresize_texture = CreateTexture(2048, 2048, 1+256)
	fresize_texture2 = CreateTexture(2048, 2048, 1+256)
	TextureBlend fresize_texture2,3
	SetBuffer(TextureBuffer(fresize_texture2))
	ClsColor 0,0,0
	Cls
	SetBuffer(BackBuffer())
	EntityTexture spr, fresize_texture,0,0
	EntityTexture spr, fresize_texture2,0,1
	
	HideEntity fresize_cam
End Function

Function Graphics3DExt%(width%,height%,depth%=32,mode%=2)
	Graphics3D width,height,depth,mode
	InitFastResize()
	AntiAlias Opt_AntiAlias
End Function

; // Others
Function SetWindowSize(width%, height%)
	Graphics3DExt(width%, height%, 0, 2)
End Function

Function SaveOptions()
	PutINIValue(OptionFile$, "audio", "music volume", MusicVolume)
	PutINIValue(OptionFile$, "audio", "sound volume", SFXVolume)
	
	PutINIValue(OptionFile$, "language", "selected language", selectedLanguage)
	
	PutINIValue(OptionFile$, "customization", "bgr", RListBg)
	PutINIValue(OptionFile$, "customization", "bgg", GListBg)
	PutINIValue(OptionFile$, "customization", "bgb", BListBg)
	PutINIValue(OptionFile$, "customization", "idler", RModIdle)
	PutINIValue(OptionFile$, "customization", "idleg", GModIdle)
	PutINIValue(OptionFile$, "customization", "idleb", BModIdle)
	PutINIValue(OptionFile$, "customization", "selectr", RModSelect)
	PutINIValue(OptionFile$, "customization", "selectg", GModSelect)
	PutINIValue(OptionFile$, "customization", "selectb", BModSelect)
End Function

Function LoadOptions()
	MusicVolume = GetINIString(OptionFile$, "audio", "music volume")
	SFXVolume = GetINIString(OptionFile$, "audio", "sound volume")
	
	selectedLanguage = GetINIString(OptionFile$, "language", "selected language")
	
	RListBg = GetINIString(OptionFile$, "customization", "bgr")
	GListBg = GetINIString(OptionFile$, "customization", "bgg")
	BListBg = GetINIString(OptionFile$, "customization", "bgb")
	If RListBG>255 Then RListBG=255
	If RListBG<0 Then RListBG=0
	If GListBG>255 Then GListBG=255
	If GListBG<0 Then GListBG=0
	If BListBG>255 Then BListBG=255
	If BListBG<0 Then BListBG=0
	RModIdle = GetINIString(OptionFile$, "customization", "idler")
	GModIdle = GetINIString(OptionFile$, "customization", "idleg")
	BModIdle = GetINIString(OptionFile$, "customization", "idleb")
	If RModIdle>255 Then RModIdle=255
	If RModIdle<0 Then RModIdle=0
	If GModIdle>255 Then GModIdle=255
	If GModIdle<0 Then GModIdle=0
	If BModIdle>255 Then BModIdle=255
	If BModIdle<0 Then BModIdle=0
	RModSelect = GetINIString(OptionFile$, "customization", "selectr")
	GModSelect = GetINIString(OptionFile$, "customization", "selectg")
	BModSelect = GetINIString(OptionFile$, "customization", "selectb")
	If RModSelect>255 Then RModSelect=255
	If RModSelect<0 Then RModSelect=0
	If GModSelect>255 Then GModSelect=255
	If GModSelect<0 Then GModSelect=0
	If BModSelect>255 Then BModSelect=255
	If BModSelect<0 Then BModSelect=0
End Function
;~IDEal Editor Parameters:
;~F#7#15#3C#48#4D#62#6C#7E#88#D7#14F#156#16A#187#1B1#1BD#1C1#1DA#1E6#1FF
;~F#244#251#257#25C#26B#278#289#2A0#2A7#2C1#2F3#301#30D#314#325#334#340#351#35D#369
;~F#37F#386#38F#396#39D#3A4#3AC#3B7#3C1#3CB#3D4#3DC#3EA#3EE#3F2#3F6#3FF#40A#433#43A
;~F#43E#44F
;~C#Blitz3D