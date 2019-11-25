<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
<@layout.header />
Rappel au cas où vous auriez oublié

<@class.bookedClassPreview scheduledClass=data.nextClass bookedFor=data.bookedFor place=data.nextClass.lesson.place>


	Voir les informations du cours : ${@deploymentContextService.viewClassUrl(data.nextClass)}
	Vous désinscrire               : ${@deploymentContextService.unbookUrl(data.nextClass)}
</@>


---------------------------------------------

Quelques informations supplémentaires

    <@information.approvedMessage />
    
   
<@information.description scheduledClass=data.nextClass />
<@layout.footer />