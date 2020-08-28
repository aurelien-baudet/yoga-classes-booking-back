<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
Une place est libre. Merci d'indiquer si vous souhaitez prendre la place disponible.

<@class.bookedClassPreview scheduledClass=bookedClass bookedFor=student place=bookedClass.lesson.place>


	Prendre la place disponible    : ${@deploymentContextService.takeAvailablePlaceUrl(bookedClass)}
	Voir les informations du cours : ${@deploymentContextService.viewClassUrl(bookedClass)}
	Vous désinscrire               : ${@deploymentContextService.unbookUrl(bookedClass)}
</@>


---------------------------------------------

Quelques informations supplémentaires

	<@information.approvedMessage />
	
	
<@information.description scheduledClass=bookedClass />
<@layout.footer />
