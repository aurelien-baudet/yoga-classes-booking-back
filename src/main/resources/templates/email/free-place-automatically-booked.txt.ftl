<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
Une place vient de se libérer et vous êtes maintenant inscrit au cours

<@class.bookedClassPreview scheduledClass=bookedClass bookedFor=student place=bookedClass.lesson.place>


	Voir les informations du cours : ${@deploymentContextService.viewClassUrl(bookedClass)}
	Vous désinscrire               : ${@deploymentContextService.unbookUrl(bookedClass)}
</@>


---------------------------------------------

Quelques informations supplémentaires

	<@information.approvedMessage />
	
	
<@information.description scheduledClass=bookedClass />
<@layout.footer />
