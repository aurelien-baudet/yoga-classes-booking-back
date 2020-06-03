<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
<@layout.header />
Rappel au cas où vous auriez oublié

<@class.bookedClassPreview scheduledClass=nextClass bookedFor=bookedFor place=nextClass.lesson.place>


	Voir les informations du cours : ${@deploymentContextService.viewClassUrl(nextClass)}
	Vous désinscrire               : ${@deploymentContextService.unbookUrl(nextClass)}
</@>


---------------------------------------------

Quelques informations supplémentaires

    <@information.approvedMessage />
    
   
<@information.description scheduledClass=nextClass />
<@layout.footer />