<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
<@layout.header />
Je vous remercie de votre inscription

<#if !data.isApproved()>
	<@information.waitingMessage />
	
	
---------------------------------------------
</#if> 

<@class.bookedClassPreview scheduledClass=data.bookedClass bookedFor=data.bookedFor place=data.bookedClass.lesson.place>


	Voir les informations du cours : ${@deploymentContextService.viewClassUrl(data.bookedClass)}
	Vous désinscrire               : ${@deploymentContextService.unbookUrl(data.bookedClass)}
</@>


<#if data.isApproved()>
---------------------------------------------

Quelques informations supplémentaires

    <@information.approvedMessage />
    
    
</#if>
<@information.description scheduledClass=data.bookedClass />
<@layout.footer />
