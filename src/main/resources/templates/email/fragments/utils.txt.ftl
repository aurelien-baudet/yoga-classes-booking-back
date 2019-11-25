<#macro indent spaces=4>
	<#-- ${(text!"")?replace('(\r?\n)', '$1[NEWLINE]'?right_pad(spaces), 'r')} -->
	<#nested>
</#macro>