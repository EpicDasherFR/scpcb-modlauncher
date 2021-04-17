Const StringsFile$ = "Assets/strings.ini"

Global global_apptitle$
Global lang_EN$, lang_FR$

Global text_mods$, text_details$, text_madeby$
Global infos_madeby$, infos_version$
Global credits_youtube$, credits_moddb$, credits_discord$, credits_github$

Global button_launchmod$, button_launchcb$, button_quit$, button_options$, button_back$

Global options_low$, options_high$, options_tabaudio$, options_optmusic$, options_optsfx$
Global options_tablang$, options_selectedlang$, options_langchange$
Global options_tabcustom$, options_custom1$, options_custom2$, options_custom3$, options_customred$, options_customblue$, options_customgreen$, options_custompreview$

Global error_0A$, error_0B$, error_1A$, error_1B$, error_2$, error_3A$, error_3B$, error_4$, error_5$, error_6A$, error_6B$, error_6C$

Function LoadStrings(lang$)
	global_apptitle$ = GetINIString(StringsFile$, lang$, "global.apptitle")
	lang_EN$ = GetINIString(StringsFile$, lang$, "lang.en")
	lang_FR$ = GetINIString(StringsFile$, lang$, "lang.fr")
	
	text_mods$ = GetINIString(StringsFile$, lang$, "text.mods")
	text_details$ = GetINIString(StringsFile$, lang$, "text.details")
	text_madeby$ = GetINIString(StringsFile$, lang$, "text.madeby")
	infos_madeby$ = GetINIString(StringsFile$, lang$, "infos.madeby")
	infos_version$ = GetINIString(StringsFile$, lang$, "infos.version")
	credits_youtube$ = GetINIString(StringsFile$, lang$, "credits.youtube")
	credits_moddb$ = GetINIString(StringsFile$, lang$, "credits.moddb")
	credits_discord$ = GetINIString(StringsFile$, lang$, "credits.discord")
	credits_github$ = GetINIString(StringsFile$, lang$, "credits.github")
	
	button_launchmod$ = GetINIString(StringsFile$, lang$, "button.launchmod")
	button_launchcb$ = GetINIString(StringsFile$, lang$, "button.launchcb")
	button_quit$ = GetINIString(StringsFile$, lang$, "button.quit")
	button_options$ = GetINIString(StringsFile$, lang$, "button.options")
	button_back$ = GetINIString(StringsFile$, lang$, "button.back")
	
	options_low$ = GetINIString(StringsFile$, lang$, "options.low")
	options_high$ = GetINIString(StringsFile$, lang$, "options.high")
	options_tabaudio$ = GetINIString(StringsFile$, lang$, "options.tabaudio")
	options_optmusic$ = GetINIString(StringsFile$, lang$, "options.optmusic")
	options_optsfx$ = GetINIString(StringsFile$, lang$, "options.optsfx")
	options_tablang$ = GetINIString(StringsFile$, lang$, "options.tablang")
	options_selectedlang$ = GetINIString(StringsFile$, lang$, "options.selectedlang")
	options_langchange$ = GetINIString(StringsFile$, lang$, "options.langchange")
	options_tabcustom$ = GetINIString(StringsFile$, lang$, "options.tabcustom")
	options_custom1$ = GetINIString(StringsFile$, lang$, "options.custom1")
	options_custom2$ = GetINIString(StringsFile$, lang$, "options.custom2")
	options_custom3$ = GetINIString(StringsFile$, lang$, "options.custom3")
	options_customred$ = GetINIString(StringsFile$, lang$, "options.customred")
	options_customgreen$ = GetINIString(StringsFile$, lang$, "options.customgreen")
	options_customblue$ = GetINIString(StringsFile$, lang$, "options.customblue")
	options_custompreview$ = GetINIString(StringsFile$, lang$, "options.custompreview")
	
	error_0A$ = GetINIString(StringsFile$, lang$, "error.0A")
	error_0B$ = GetINIString(StringsFile$, lang$, "error.0B")
	error_1A$ = GetINIString(StringsFile$, lang$, "error.1A")
	error_1B$ = GetINIString(StringsFile$, lang$, "error.1B")
	error_2$ = GetINIString(StringsFile$, lang$, "error.2")
	error_3A$ = GetINIString(StringsFile$, lang$, "error.3A")
	error_3B$ = GetINIString(StringsFile$, lang$, "error.3B")
	error_4$ = GetINIString(StringsFile$, lang$, "error.4")
	error_5$ = GetINIString(StringsFile$, lang$, "error.5")
	error_6A$ = GetINIString(StringsFile$, lang$, "error.6A")
	error_6B$ = GetINIString(StringsFile$, lang$, "error.6B")
	error_6C$ = GetINIString(StringsFile$, lang$, "error.6C")
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D