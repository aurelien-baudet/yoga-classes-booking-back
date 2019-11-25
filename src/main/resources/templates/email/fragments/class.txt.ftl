<#import "utils.txt.ftl" as utils>
<#macro classPreview scheduledClass place={}>
	<@utils.indent>
	Date         : ${(statics['fr.yoga.booking.util.DateRangeUtil'].format(scheduledClass.start, scheduledClass.end))?cap_first}
    </@>
	<#if place?has_content>
	
		<@utils.indent>
		Lieu         : ${place.name} - ${place.address}
    	</@>
    </#if>
    <#nested>
</#macro>

<#macro bookedClassPreview scheduledClass bookedFor place={}>
	<@classPreview scheduledClass=scheduledClass place=place />
	
	<@utils.indent>
	Réservé pour : ${bookedFor.displayName}
    </@>
    <@utils.indent>
	<#nested>
    </@>
</#macro>