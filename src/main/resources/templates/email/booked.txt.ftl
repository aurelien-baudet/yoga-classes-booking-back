<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
<@layout.header />
Je vous remercie de votre inscription

<#if !approved>
	<@information.waitingMessage />
	
	
---------------------------------------------
</#if> 

<@class.bookedClassPreview scheduledClass=bookedClass bookedFor=bookedFor place=bookedClass.lesson.place>


	Voir les informations du cours : ${@deploymentContextService.viewClassUrl(bookedClass)}
	Vous désinscrire               : ${@deploymentContextService.unbookUrl(bookedClass)}
</@>


<#if approved>
---------------------------------------------

Quelques informations supplémentaires

    <@information.approvedMessage />
    
    
</#if>
<@information.description scheduledClass=bookedClass />
<@layout.footer />
